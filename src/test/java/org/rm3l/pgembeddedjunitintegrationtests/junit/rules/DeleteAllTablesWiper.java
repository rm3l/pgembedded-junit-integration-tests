package org.rm3l.pgembeddedjunitintegrationtests.junit.rules;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import static org.rm3l.pgembeddedjunitintegrationtests.junit.rules.PostgreSQLServer.JDBC_PASSWORD;
import static org.rm3l.pgembeddedjunitintegrationtests.junit.rules.PostgreSQLServer.JDBC_USERNAME;

public class DeleteAllTablesWiper extends AbstractDatabaseWiper {

    DeleteAllTablesWiper(PostgreSQLServer postgreSQLServer) {
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
                databaseTruncationStatement.execute(
                        "SET session_replication_role = replica"); //Disable all constraints
                databaseTruncationStatement.execute("BEGIN TRANSACTION");
                final Set<String> temporaryTablesStatements = new TreeSet<>();
                int index = 0;
                final Collection<String> allDatabaseTables = this.postgreSQLServer.getAllDatabaseTables();
                for (final String table : allDatabaseTables) {
                    //Much faster to delete all tables in a single statement
                    temporaryTablesStatements.add(
                            String.format("table_%s AS (DELETE FROM %s)", index++, table));
                }
                databaseTruncationStatement.execute(
                        String.format("WITH %S SELECT 1", String.join(",", temporaryTablesStatements)));
                databaseTruncationStatement.execute("COMMIT TRANSACTION");
                databaseTruncationStatement.execute(
                        "SET session_replication_role = DEFAULT"); //Reset constraints
            }
        }

    }
}
