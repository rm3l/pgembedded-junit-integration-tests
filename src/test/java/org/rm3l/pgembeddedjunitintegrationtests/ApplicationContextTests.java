package org.rm3l.pgembeddedjunitintegrationtests;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationContextTests extends AbstractIT {

	private static final Logger logger =
			LoggerFactory.getLogger(ApplicationContextTests.class);

	@Test
	public void contextLoads() throws Exception {
		logger.info("Great - Spring Application Context successfully loaded! :)");
		this.mvc.perform(get("/profile")).andExpect(status().isOk());
	}

}
