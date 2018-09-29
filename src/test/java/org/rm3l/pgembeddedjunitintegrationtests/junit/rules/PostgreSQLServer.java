package org.rm3l.pgembeddedjunitintegrationtests.junit.rules;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import javax.persistence.Persistence;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;

import static java.util.Arrays.asList;
import static ru.yandex.qatools.embed.postgresql.EmbeddedPostgres.cachedRuntimeConfig;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.V10_3;

public class PostgreSQLServer extends ExternalResource {

    private static Logger logger = LoggerFactory.getLogger(PostgreSQLServer.class);

    private static final Version POSTGRESQL_VERSION = V10_3;
    private static EmbeddedPostgres postgres;

    private String jdbcUrl;
    private static final String TEST_DB = "my_test_database";
    static final String JDBC_USERNAME = "my_test_username";
    static final String JDBC_PASSWORD = "my_test_password";

    private static final List<String> DEFAULT_ADD_PARAMS = asList(
            "-E", "SQL_ASCII",
            "--locale=C",
            "--lc-collate=C",
            "--lc-ctype=C");

    private static final SortedSet<String> ALL_TABLES = new TreeSet<>();

    @Override
    protected void before() throws Throwable {
        synchronized (PostgreSQLServer.class) {
            if (postgres == null) {
                //The line below starts a new PostgreSQL server
                //listening on any available local port.
                //The library makes sure to download the right
                //PostgreSQL version,
                //to extract and configure it accordingly
                postgres = new EmbeddedPostgres(POSTGRESQL_VERSION);
                postgres.start(
                        //Providing a cache dir to avoid extracting the archive at each JVM process run
                        cachedRuntimeConfig(
                                Paths.get(System.getProperty("user.home"), ".local", "tmp", ".postgresql-embedded")),
                        "localhost", findFreePort(), TEST_DB, JDBC_USERNAME, JDBC_PASSWORD,
                        DEFAULT_ADD_PARAMS);

                //Register hook to shutdown the
                //PostgreSQL Embedded server at JVM shutdown.
                Runtime.getRuntime().addShutdownHook(
                        new Thread(() -> Optional
                                .ofNullable(postgres)
                                .ifPresent(EmbeddedPostgres::stop)));

                jdbcUrl = postgres.getConnectionUrl()
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Failed to get PostgreSQL Connection URL"));

                //Now that we have a JDBC URL,
                //we may create the schema/tables
                //as well as inject some default data
                //...
                final Properties schemaGenerationProps = new Properties();
                schemaGenerationProps.put("javax.persistence.schema-generation.database.action",
                        "create");
                schemaGenerationProps.put("javax.persistence.jdbc.driver",
                        org.postgresql.Driver.class.getCanonicalName());
                schemaGenerationProps.put("javax.persistence.jdbc.url", jdbcUrl);
                schemaGenerationProps.put("javax.persistence.jdbc.username", JDBC_USERNAME);
                schemaGenerationProps.put("javax.persistence.jdbc.password", JDBC_PASSWORD);
                Persistence.generateSchema("samplePU", schemaGenerationProps);

                final String[] types = {"TABLE"};
                try (final Connection connection =
                             DriverManager.getConnection(jdbcUrl, JDBC_USERNAME, JDBC_PASSWORD)) {
                    final DatabaseMetaData databaseMetaData = connection.getMetaData();
                    try (final java.sql.Statement statement = connection.createStatement();
                         final ResultSet resultSet = databaseMetaData.getTables(null, null, "%", types)) {
                        while (resultSet.next()) {
                            final String tableName = resultSet.getString("TABLE_NAME");

                            ALL_TABLES.add(tableName);

                            //Create trigger for listening to table changes and notifying on the specified channel
                            statement.execute(
                                    String.format(
                                            "CREATE OR REPLACE FUNCTION PUBLIC.NOTIFY_%1$s() RETURNS trigger AS "
                                                    + "$BODY$ "
                                                    + "BEGIN "
                                                    + "  PERFORM pg_notify('table_insertions', '%1$s');"
                                                    + "  RETURN new;"
                                                    + "END;"
                                                    + "$BODY$"
                                                    + "LANGUAGE 'plpgsql';",
                                            tableName));

                            statement.execute(
                                    String.format(
                                            "CREATE TRIGGER %1$s_changes "
                                                    + "AFTER INSERT ON %1$s "
                                                    + "FOR EACH ROW "
                                                    + "EXECUTE PROCEDURE PUBLIC.NOTIFY_%1$s()",
                                            tableName));
                        }
                    }
                }
            }
        }

        //Now since this is being used as a Junit ClassRule,
        //we can set some properties that can be used
        //later on during the tests.
        // For example, if you make use of Spring/SpringBoot,
        //you can set the datasource as JVM properties that will get
        //picked when the Spring Application Context is initialized!
        System.setProperty("spring.datasource.url", jdbcUrl);
        System.setProperty("spring.datasource.username", JDBC_USERNAME);
        System.setProperty("spring.datasource.password", JDBC_PASSWORD);
    }

    String getJdbcUrl() {
        return jdbcUrl;
    }

    Collection<String> getAllDatabaseTables() {
        return Collections.unmodifiableSortedSet(ALL_TABLES);
    }

    private static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            closeQuietly(socket);
            return port;
        } catch (IOException e) {
            logger.trace("Failed to open socket", e);
        } finally {
            if (socket != null) {
                closeQuietly(socket);
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }

    private static void closeQuietly(ServerSocket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            logger.trace("Failed to close socket", e);
        }
    }
}
