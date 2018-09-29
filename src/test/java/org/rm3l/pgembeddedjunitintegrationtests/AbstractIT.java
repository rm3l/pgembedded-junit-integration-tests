package org.rm3l.pgembeddedjunitintegrationtests;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.rm3l.pgembeddedjunitintegrationtests.junit.rules.AbstractDatabaseWiper;
import org.rm3l.pgembeddedjunitintegrationtests.junit.rules.PostgreSQLServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class AbstractIT {

    private static final Logger logger = LoggerFactory.getLogger(AbstractIT.class);

    @org.junit.ClassRule
    public static final PostgreSQLServer databaseServer = new PostgreSQLServer();

    @org.junit.Rule
    public final AbstractDatabaseWiper databaseWiper =
            AbstractDatabaseWiper.getInstance(databaseServer);

    //Discover actual server HTTP port at runtime
    @Value("${local.server.port}")
    protected int port;

    @Autowired
    protected WebApplicationContext context;

    protected MockMvc mvc;

    @Before
    public final void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .alwaysDo(result -> {
                    if (logger.isTraceEnabled()) {
                        final MockHttpServletResponse response = result.getResponse();
                        logger.trace(
                                "<<< Response : [{}]\n{}",
                                response.getStatus(),
                                response.getContentAsString());
                    }
                })
                .build();
    }
}
