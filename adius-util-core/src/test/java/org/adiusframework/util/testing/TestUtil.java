package org.adiusframework.util.testing;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.adiusframework.util.datastructures.Properties;

/**
 * The TestUtil class provides method that are only needed for test cases and
 * not for runtime.
 */
public class TestUtil {

	/** A file that is used for testing. */
	public static final File TEST_FILE = new File("testFile.txt");

	/** The content of the first line of the test-file. */
	public static final String LINE_1 = "Line1";

	/** The content of the second line of the test-file. */
	public static final String LINE_2 = "Line2";

	/** The content of the third line of the test-file. */
	public static final String LINE_3 = "Line3";

	/**
	 * The default constructor is the only constructor and private, because this
	 * class should not be instantiated. It only contains static methods.
	 */
	private TestUtil() {
	}

	/**
	 * Create the test-file and adds the default content.
	 */
	public static void initTestFile() {
		try {
			FileWriter writer = new FileWriter(TEST_FILE);
			writer.append(LINE_1 + "\n" + LINE_2 + "\n" + LINE_3 + "\n");
			writer.close();
		} catch (IOException e) {
			System.out.println("Can't run FileUtil-Tests because the file-initialisation failed");
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Removes the test-file and outputs an error if it fails.
	 */
	public static void destroyTestFile() {
		if (!TEST_FILE.delete()) {
			System.err.println("Can't delete the testfile (\"" + TEST_FILE.getAbsolutePath() + "\")!");
		}
	}

	/**
	 * Creates a mock of a the Properties class and stub the behavior for the
	 * given properties.
	 * 
	 * @param args
	 *            The properties which the mocks simulation should contain.
	 * @return The created mock.
	 */
	public static Properties mockProperties(final String... args) {
		Properties mock = mock(Properties.class);
		when(mock.stringPropertyNames()).thenReturn(new HashSet<String>() {
			private static final long serialVersionUID = 2038029797087737258L;

			{
				for (int i = 0; i < args.length; i += 2) {
					add(args[i]);
				}
			}
		});
		for (int i = 0; i < args.length; i += 2) {
			when(mock.getProperty(args[i])).thenReturn(args[i + 1]);
		}

		return mock;
	}
}
