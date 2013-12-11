package org.adiusframework.util.net;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import java.io.File;

import org.adiusframework.util.datastructures.FtpConnectionContainer;
import org.adiusframework.util.datastructures.Properties;
import org.adiusframework.util.testing.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ FtpUtil.class })
public class BasicFileToFtpMappingTest {
	private static final String TEST_FTP_DIR = "test/dir";
	private static final String MAPPED_PATH = "mapped/path";
	private static final String NOT_EXISTING_TEST_FILE = "notExistingTestFile";

	private static final String KEY_1 = "Key1";
	private static final String KEY_2 = "Key2";

	private FtpConnectionContainer connection;
	private BasicFileToFtpMapping mapping;

	@Before
	public void init() {
		connection = mock(FtpConnectionContainer.class);
		mapping = new BasicFileToFtpMapping(connection);

		PowerMockito.mockStatic(FtpUtil.class);

		when(FtpUtil.copyToFtpServer(eq(connection), any(File.class), eq(TEST_FTP_DIR), anyString())).thenAnswer(
				new Answer<String>() {

					@Override
					public String answer(InvocationOnMock invocation) throws Throwable {
						if (((File) invocation.getArguments()[1]).getAbsolutePath().equals(
								TestUtil.TEST_FILE.getAbsolutePath())
								&& invocation.getArguments()[3] == null)
							return MAPPED_PATH;

						return null;
					}
				});

		TestUtil.initTestFile();
	}

	@After
	public void destroy() {
		TestUtil.destroyTestFile();
	}

	@Test
	public void testGenerate() {
		Properties properties = mapping.generate(TEST_FTP_DIR,
				TestUtil.mockProperties(KEY_1, TestUtil.TEST_FILE.getAbsolutePath(), KEY_2, NOT_EXISTING_TEST_FILE));

		assertTrue(properties.get(KEY_1).equals(MAPPED_PATH));
		assertFalse(properties.get(KEY_1).equals(NOT_EXISTING_TEST_FILE));
	}

	@Test
	public void testRevert() {
		mapping.generate(TEST_FTP_DIR, TestUtil.mockProperties(KEY_1, TestUtil.TEST_FILE.getAbsolutePath()));

		mapping.revert();

		PowerMockito.verifyStatic(times(1));
		FtpUtil.copyToFile(connection, MAPPED_PATH, TestUtil.TEST_FILE.getAbsolutePath());
	}

	@Test
	public void testDelete() {
		mapping.generate(TEST_FTP_DIR, TestUtil.mockProperties(KEY_1, TestUtil.TEST_FILE.getAbsolutePath()));

		mapping.delete();

		PowerMockito.verifyStatic(times(1));
		FtpUtil.delete(connection, MAPPED_PATH, true);
	}
}
