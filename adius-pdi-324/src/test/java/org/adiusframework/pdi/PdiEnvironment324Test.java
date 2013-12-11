package org.adiusframework.pdi;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PdiEnvironment324Test {

	private PdiEnvironment324 env;

	@Before
	public void init() {
		this.env = new PdiEnvironment324(null);
	}

	@After
	public void close() {
		this.env.close();
	}

	@Test
	public void launchSimpleTest() {
		PdiJobWrapper job = this.env.createJob("src/test/resources/simpleTest.kjb", null);
		processJob(job);
	}

	@Test
	public void launchVariableTest() {
		PdiJobWrapper job = this.env.createJob("src/test/resources/variableTest.kjb", null);
		job.setParameter("TEST", "test", true);
		processJob(job);
	}

	private void processJob(PdiJobWrapper job) {
		job.start(true);
		PdiResult result = job.getResult();
		assertTrue(result.getNrErrors() == 0);
	}
}
