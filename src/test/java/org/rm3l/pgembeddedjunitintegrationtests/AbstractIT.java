package org.rm3l.pgembeddedjunitintegrationtests;

import org.junit.runner.RunWith;
import org.rm3l.pgembeddedjunitintegrationtests.junit.rules.AbstractDatabaseWiper;
import org.rm3l.pgembeddedjunitintegrationtests.junit.rules.PostgreSQLServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractIT {

    @org.junit.ClassRule
    public static final PostgreSQLServer databaseServer = new PostgreSQLServer();

    @org.junit.Rule
    public final AbstractDatabaseWiper databaseWiper =
            AbstractDatabaseWiper.getInstance(databaseServer);
}
