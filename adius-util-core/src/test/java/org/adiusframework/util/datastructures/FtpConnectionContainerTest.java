package org.adiusframework.util.datastructures;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class FtpConnectionContainerTest {
	private static final String TEST_HOST = "TestHost";
	private static final String TEST_USER = "TestUser";
	private static final String TEST_PASSWORD = "TestPassword";
	private static final int TEST_PORT = 42;

	private static final String TEST_URL = "ftp://TestUser:TestPassword@TestHost:42";

	private FtpConnectionContainer container;

	@Before
	public void init() {
		container = new FtpConnectionContainer();
		container.setHost(TEST_HOST);
		container.setPassword(TEST_PASSWORD);
		container.setPort(TEST_PORT);
		container.setUser(TEST_USER);
	}

	@Test
	public void testGetUrl() {
		assertEquals("Unequality: " + container.getURL(), container.getURL().toString(), TEST_URL);
	}
}
