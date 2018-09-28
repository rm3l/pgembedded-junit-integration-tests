package org.rm3l.pgembeddedjunitintegrationtests.junit.rules;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

public abstract class AbstractDatabaseWiper implements TestRule {

    private static final Logger logger =
            LoggerFactory.getLogger(AbstractDatabaseWiper.class);

    final PostgreSQLServer postgreSQLServer;

    AbstractDatabaseWiper(PostgreSQLServer postgreSQLServer) {
        this.postgreSQLServer = postgreSQLServer;
    }

    @Override
    public final Statement apply(@NonNull final Statement base, @NonNull final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    AbstractDatabaseWiper.this.before();
                    base.evaluate();
                } finally{
                    AbstractDatabaseWiper.this.after();
                }
            }
        };
    }

    protected abstract void before() throws Exception;

    protected abstract void after() throws Exception;

    public static AbstractDatabaseWiper getInstance(final PostgreSQLServer postgreSQLServer) {
        final WipingStrategy wipingStrategy = WipingStrategy.valueOf(
                System.getProperty("wiping.strategy",
                        WipingStrategy.DELETE_ONLY_CHANGED_TABLES.name())
                        .replaceAll("-", "_").toUpperCase());

        logger.info("Using wiping strategy: {}", wipingStrategy);

        switch (wipingStrategy) {
            case DELETE_ALL_TABLES:
                return new DeleteAllTablesWiper(postgreSQLServer);
            case DELETE_ONLY_CHANGED_TABLES:
                return new DeleteOnlyChangedTablesWiper(postgreSQLServer);
            case TRUNCATE_ALL_TABLES:
                return new TruncateAllTablesWiper(postgreSQLServer);
            case TRUNCATE_ONLY_CHANGED_TABLES:
                return new TruncateOnlyChangedTablesWiper(postgreSQLServer);
            default:
                throw new IllegalStateException("Unhandled wiping strategy: " + wipingStrategy);
        }
    }

    enum WipingStrategy {
        DELETE_ALL_TABLES,
        DELETE_ONLY_CHANGED_TABLES,
        TRUNCATE_ALL_TABLES,
        TRUNCATE_ONLY_CHANGED_TABLES
    }
}
