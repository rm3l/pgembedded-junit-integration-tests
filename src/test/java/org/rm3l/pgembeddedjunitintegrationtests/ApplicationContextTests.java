package org.rm3l.pgembeddedjunitintegrationtests;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationContextTests extends AbstractIT {

	private static final Logger logger =
			LoggerFactory.getLogger(ApplicationContextTests.class);

	@Test
	public void contextLoads() {
		logger.info("Great - Spring Application Context successfully loaded! :)");
	}

}
