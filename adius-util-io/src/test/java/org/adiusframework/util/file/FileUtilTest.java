package org.adiusframework.util.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.MalformedURLException;

import org.adiusframework.util.testing.TestUtil;
import org.junit.Before;
import org.junit.Test;

public class FileUtilTest {
	private static String SEPERATOR = System.getProperty("file.separator");
	private static String TEST_PATH = "TestDisk:" + SEPERATOR + "TestDir";
	private static String TEST_FILENAME = "TestFile";
	private static String TEST_EXTENSION = ".test";
	private static String NORMAL_URL = TEST_PATH + SEPERATOR + TEST_FILENAME + TEST_EXTENSION;
	private static String EXCEPTION_URL = "No<Url";

	@Test
	public void testGetPathAndFileNormal() {
		try {
			String[] result = FileUtil.getPathAndFile(NORMAL_URL);
			assertEquals(result[0], TEST_PATH + SEPERATOR);
			assertEquals(result[1], TEST_FILENAME + TEST_EXTENSION);
		} catch (MalformedURLException e) {
			fail();
		}
	}

	@Test
	public void testGetPathAndFileException() {
		try {
			FileUtil.getPathAndFile(EXCEPTION_URL);
			fail();
		} catch (MalformedURLException e) {
			// this is what we expected
		}
	}

	@Test
	public void testEnsureDirectorySeparatorA() {
		try {
			assertEquals(FileUtil.ensureDirectorySeparator(TEST_PATH), TEST_PATH + SEPERATOR);
		} catch (MalformedURLException e) {
			fail();
		}
	}

	@Test
	public void testEnsureDirectorySeparatorB() {
		try {
			assertEquals(FileUtil.ensureDirectorySeparator(TEST_PATH + SEPERATOR), TEST_PATH + SEPERATOR);
		} catch (MalformedURLException e) {
			fail();
		}
	}

	@Test
	public void testEnsureDirectorySeparatorException() {
		try {
			FileUtil.ensureDirectorySeparator(EXCEPTION_URL);
			fail();
		} catch (MalformedURLException e) {
			// this is what we expected
		}
	}

	@Test
	public void testGetPureFileName() {
		assertEquals(FileUtil.getPureFileName(NORMAL_URL), TEST_FILENAME);
	}

	@Test
	public void testGetFileExtension() {
		assertEquals(FileUtil.getFileExtension(NORMAL_URL), TEST_EXTENSION.substring(1));
	}

	private static final int BUFFER_SIZE = 16;
	private static final int BYTES_TO_COPY = (int) (Math.random() * 100 + 100);

	private byte[] testData;

	private InputStream istream;
	private OutputStream ostream;

	private PipedInputStream input;

	@Before
	public void initStreams() {
		try {
			testData = new byte[BYTES_TO_COPY];
			for (int i = 0; i < testData.length; i++) {
				testData[i] = (byte) (Math.random() * 100);
			}
			PipedOutputStream output = new PipedOutputStream();
			istream = new PipedInputStream(output);
			output.write(testData);
			output.close();

			input = new PipedInputStream();
			ostream = new PipedOutputStream(input);
		} catch (IOException e) {
			System.out.println("Can't run FileUtil-Tests because the stream-initialisation failed");
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCopyStreamUnclosed() {
		try {
			FileUtil.copyStream(istream, ostream, BUFFER_SIZE, true);
			byte[] result = new byte[BYTES_TO_COPY];
			input.read(result);

			assertArrayEquals(testData, result);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	private static final String TEST_REPLACE_WITH = "NewLine";

	@Test
	public void testReplaceLineInFile() {
		TestUtil.initTestFile();

		try {
			FileUtil.replaceLineInFile(TestUtil.TEST_FILE, TestUtil.LINE_2, TEST_REPLACE_WITH);

			BufferedReader reader = new BufferedReader(new FileReader(TestUtil.TEST_FILE));
			String resultContent = "";
			while (reader.ready()) {
				resultContent += reader.readLine() + "\n";
			}
			reader.close();
			assertEquals(resultContent, TestUtil.LINE_1 + "\n" + TEST_REPLACE_WITH + "\n" + TestUtil.LINE_3 + "\n");
		} catch (IOException e) {
			fail(e.getMessage());
		}
		TestUtil.destroyTestFile();
	}

}
