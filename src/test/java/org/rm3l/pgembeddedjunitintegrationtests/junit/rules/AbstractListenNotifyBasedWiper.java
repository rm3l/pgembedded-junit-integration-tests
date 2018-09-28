package org.rm3l.pgembeddedjunitintegrationtests.junit.rules;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.rm3l.pgembeddedjunitintegrationtests.junit.rules.PostgreSQLServer.JDBC_PASSWORD;
import static org.rm3l.pgembeddedjunitintegrationtests.junit.rules.PostgreSQLServer.JDBC_USERNAME;

public abstract class AbstractListenNotifyBasedWiper extends AbstractDatabaseWiper {

    private static final Logger logger =
            LoggerFactory.getLogger(AbstractListenNotifyBasedWiper.class);

    private Connection eventListenerConnection;

    AbstractListenNotifyBasedWiper(PostgreSQLServer postgreSQLServer) {
        super(postgreSQLServer);
    }

    @Override
    protected void before() throws SQLException {
        this.eventListenerConnection = DriverManager
                .getConnection(this.postgreSQLServer.getJdbcUrl(),
                        JDBC_USERNAME, JDBC_PASSWORD);
        try (final java.sql.Statement statement = eventListenerConnection.createStatement()) {
            statement.execute("LISTEN table_insertions");
        }
    }

    @Override
    protected void after() throws SQLException {
        try {
            this.wipeDatabase();
        } finally {
            closeEventListenerConnection();
        }
    }

    private void closeEventListenerConnection() {
        try {
            if (eventListenerConnection != null && !eventListenerConnection.isClosed()) {
                eventListenerConnection.close();
                eventListenerConnection = null;
            }
        } catch (final SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void wipeDatabase() throws SQLException {
        Collection<String> tablesModifiedAndCandidateForTruncation = null;
        synchronized (this) {
            if (eventListenerConnection == null) {
                return;
            }

            // issue a dummy query to contact the backend
            // and receive any pending notifications.
            try (final java.sql.Statement statement = eventListenerConnection.createStatement()) {
                final ResultSet rs = statement.executeQuery("SELECT 1");
                rs.close();

                final PGNotification[] notifications =
                        ((PGConnection) eventListenerConnection).getNotifications();

                if (notifications != null) {
                    tablesModifiedAndCandidateForTruncation =
                            Arrays.stream(notifications)
                                    .map(PGNotification::getParameter)
                                    .collect(Collectors.toSet());
                }
            }

            logger.info("tablesModifiedAndCandidateForTruncation: {}", tablesModifiedAndCandidateForTruncation);

            if (tablesModifiedAndCandidateForTruncation == null ||
                    tablesModifiedAndCandidateForTruncation.isEmpty()) {
                return;
            }
        }

        synchronized (PostgreSQLServer.class) {
            this.doWipeTables(tablesModifiedAndCandidateForTruncation);
        }

    }

    protected abstract void doWipeTables(final Collection<String> tables) throws SQLException;

}
