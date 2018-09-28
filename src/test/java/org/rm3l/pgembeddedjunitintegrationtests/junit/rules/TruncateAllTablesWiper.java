package org.rm3l.pgembeddedjunitintegrationtests.junit.rules;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.rm3l.pgembeddedjunitintegrationtests.junit.rules.PostgreSQLServer.JDBC_PASSWORD;
import static org.rm3l.pgembeddedjunitintegrationtests.junit.rules.PostgreSQLServer.JDBC_USERNAME;

public class TruncateAllTablesWiper extends AbstractDatabaseWiper {

    TruncateAllTablesWiper(PostgreSQLServer postgreSQLServer) {
        super(postgreSQLServer);
    }

    @Override
    protected void before() {
        //Nothing to do
    }

    @Override
    protected void after() throws Exception {
        synchronized (PostgreSQLServer.class) {
            try (final Connection connection =
                         DriverManager.getConnection(
                                 this.postgreSQLServer.getJdbcUrl(),
                                 JDBC_USERNAME, JDBC_PASSWORD);
                 final java.sql.Statement databaseTruncationStatement = connection.createStatement()) {
                databaseTruncationStatement.execute("BEGIN TRANSACTION");
                databaseTruncationStatement.execute(
                        String.format("TRUNCATE %s RESTART IDENTITY CASCADE",
                                String.join(",", this.postgreSQLServer.getAllDatabaseTables())));
                databaseTruncationStatement.execute("COMMIT TRANSACTION"); //Reset constraints
            }
        }

    }
}
