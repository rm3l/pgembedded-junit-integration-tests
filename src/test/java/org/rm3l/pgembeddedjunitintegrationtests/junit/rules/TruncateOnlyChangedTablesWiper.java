package org.rm3l.pgembeddedjunitintegrationtests.junit.rules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;

import static org.rm3l.pgembeddedjunitintegrationtests.junit.rules.PostgreSQLServer.JDBC_PASSWORD;
import static org.rm3l.pgembeddedjunitintegrationtests.junit.rules.PostgreSQLServer.JDBC_USERNAME;

public class TruncateOnlyChangedTablesWiper extends AbstractListenNotifyBasedWiper {


    TruncateOnlyChangedTablesWiper(PostgreSQLServer postgreSQLServer) {
        super(postgreSQLServer);
    }

    @Override
    protected void doWipeTables(Collection<String> tables) throws SQLException {
        try (final Connection connection =
                     DriverManager.getConnection(
                             this.postgreSQLServer.getJdbcUrl(),
                             JDBC_USERNAME, JDBC_PASSWORD);
             final java.sql.Statement databaseTruncationStatement = connection.createStatement()) {
            databaseTruncationStatement.execute("BEGIN TRANSACTION");
            databaseTruncationStatement.execute(
                    String.format("TRUNCATE %s RESTART IDENTITY CASCADE",
                            String.join(",", tables)));
            databaseTruncationStatement.execute("COMMIT TRANSACTION");
        }
    }


}
