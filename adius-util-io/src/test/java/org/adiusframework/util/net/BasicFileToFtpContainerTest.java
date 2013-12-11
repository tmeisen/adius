package org.adiusframework.util.net;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.adiusframework.util.datastructures.FtpConnectionContainer;
import org.adiusframework.util.datastructures.Properties;
import org.adiusframework.util.testing.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BasicFileToFtpContainer.class })
@PowerMockIgnore("org.slf4j.*,org.apache.log4j.*")
public class BasicFileToFtpContainerTest {
	private static final String TEST_ID = "Test";
	private static final String TEST_DIR = "Te/st";
	
	private Properties test_properties;

	private BasicFileToFtpContainer container;

	private BasicFileToFtpMapping mapping;

	@Before
	public void init() {
		FtpConnectionContainer connection = mock(FtpConnectionContainer.class);
		mapping = mock(BasicFileToFtpMapping.class);
		test_properties = TestUtil.mockProperties("KeyA", "ValueA");

		try {
			PowerMockito.whenNew(BasicFileToFtpMapping.class).withArguments(connection).thenReturn(mapping);
		} catch (Exception e) {
			fail(e.getMessage());
		}

		container = new BasicFileToFtpContainer();
		container.setFtpConnection(connection);
	}

	@Test
	public void testGenerate() {
		container.generate(TEST_ID, TEST_DIR, test_properties);

		verify(mapping, times(1)).generate(TEST_DIR, test_properties);
	}

	@Test
	public void testDelete() {
		container.generate(TEST_ID, TEST_DIR, test_properties);
		container.delete(TEST_ID);

		verify(mapping, times(1)).delete();
	}

	@Test
	public void testRevert() {
		container.generate(TEST_ID, TEST_DIR, test_properties);
		container.delete(TEST_ID);
	}

	@Test
	public void testExistsTrue() {
		container.generate(TEST_ID, null, null);

		assertTrue(container.exist(TEST_ID));
	}

	@Test
	public void testExistsFalse() {
		container.generate(TEST_DIR, null, null);

		assertFalse(container.exist(TEST_ID));
	}
}
