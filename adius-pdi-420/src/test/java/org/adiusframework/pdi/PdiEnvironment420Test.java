package org.adiusframework.pdi;

import static org.junit.Assert.*;

import org.adiusframework.pdi.PdiEnvironment420;
import org.adiusframework.pdi.PdiJobWrapper;
import org.adiusframework.pdi.PdiResult;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PdiEnvironment420Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(PdiEnvironment420Test.class);

	@Test
	public void launchSimpleTest() {

		// lets create the environment
		LOGGER.debug("Starting...");
		PdiEnvironment420 env = new PdiEnvironment420();
		PdiJobWrapper job = env.createJob("src/test/resources/simpleTest.kjb", null);

		// and start it
		job.start(true);
		PdiResult result = job.getResult();
		LOGGER.debug("Result available...");
		assertTrue(result.getNrErrors() == 0);
	}

}
