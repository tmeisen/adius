package org.adiusframework.util.file;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class.getName());

	public static final String REGEXP_FILENAME = "((?:[A-Za-z]+:)?(?:[/\\\\][^\\/\\\\:*?\"<>|]+)*[/\\\\])([^\\/\\\\:*?\"<>|]+)";

	public static final String REGEXP_DIRECTORY = "((?:[A-Za-z]+:)?(?:[/\\\\][^\\/\\\\:*?\"<>|]+)*[/\\\\]?)";

	public static final Pattern REGEXP_PATTERN_FILENAME = Pattern.compile(REGEXP_FILENAME);

	public static final Pattern REGEXP_PATTERN_DIRECTORY = Pattern.compile(REGEXP_DIRECTORY);

	/**
	 * Default suffix used for temporary files
	 */
	public static final String TMP_FILE_SUFFIX = ".tmp";

	/**
	 * Default prefix used for temporary files
	 */
	public static final String TMP_FILE_PREFIX = "tmp";

	/**
	 * Default buffer size, if no buffer size is specified
	 */
	public static final int DEFAULT_BUFFER_SIZE = 1048576;

	/**
	 * @return the file separator of the running OS, normally this should be
	 *         "\\" on windows and "/" on Unix
	 */
	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}

	/**
	 * The method can be used to extract the path and the filename information
	 * from a given absolute path. For example the input "C:\\test.txt" results
	 * in "C:\\" and "test.txt"
	 * 
	 * @param absolutePath
	 *            the absolute path to a file that have to be scanned
	 * @return an array containing the path information at index 0 and the
	 *         filename at index 1
	 * @throws MalformedURLException
	 *             if the input string does not contain a path and filename
	 *             information that could be extracted.
	 */
	public static String[] getPathAndFile(String absolutePath) throws MalformedURLException {
		String[] fileData = new String[2];

		// lets check the input, first if it is valid
		LOGGER.debug("Parsing " + absolutePath);
		Matcher matcher = REGEXP_PATTERN_FILENAME.matcher(absolutePath);
		if (matcher.matches() && matcher.groupCount() == 2) {
			fileData[0] = matcher.group(1);
			fileData[1] = matcher.group(2);
		} else {
			throw new MalformedURLException("Parsing of input string failed.");
		}
		return fileData;
	}

	/**
	 * Checks if the given string is a valid path and file description.
	 * 
	 * @param absolutePath
	 *            the path that have to be checked
	 * @return true if the path is a valid absolute path otherwise false.
	 */
	public static boolean isPathAndFile(String absolutePath) {

		// lets check the input is valid
		LOGGER.debug("Parsing " + absolutePath);
		Matcher matcher = REGEXP_PATTERN_FILENAME.matcher(absolutePath);
		return matcher.matches() && matcher.groupCount() == 2;
	}

	/**
	 * The method checks if the given string matches a directory, if not an
	 * exception is thrown. Otherwise the method returns the given directory and
	 * ensures that the string ends with the correct file separator.
	 * 
	 * @return the directory name, so that it ends with the file separator of
	 *         the current system
	 * @throws MalformedURLException
	 *             if no directory is given
	 */
	public static String ensureDirectorySeparator(String directory) throws MalformedURLException {
		Matcher matcher = REGEXP_PATTERN_DIRECTORY.matcher(directory);
		if (matcher.matches()) {
			if (directory.endsWith("/") || directory.endsWith("\\"))
				return directory;
			return directory + getFileSeparator();
		}
		throw new MalformedURLException(directory + " does not represent an directory");
	}

	public static void copyStream(InputStream istream, OutputStream ostream, int bufferSize, boolean close)
			throws IOException {

		// create buffer
		try {
			byte[] buffer = new byte[bufferSize];
			int currentBytes = 0;
			while ((currentBytes = istream.read(buffer)) != -1) {
				ostream.write(buffer, 0, currentBytes);
			}
		} finally {
			if (close) {
				istream.close();
				ostream.close();
			}
		}
	}

	/**
	 * Replaces a line (lineToMatch) in a file by the line contained in
	 * lineToWrite.
	 * 
	 * @param file
	 *            File that have to be checked.
	 * @param lineToMatch
	 *            Line that have to be matched, the equality will be checked
	 *            after a trim operation has been processed.
	 * @param lineToWrite
	 *            Line that have to be written instead of the given line.
	 * @return Number of changes in the file.
	 * @throws IOException
	 *             if an error occurs during the replacement operation.
	 */
	public static int replaceLineInFile(File file, String lineToMatch, String lineToWrite) throws IOException {
		File temp = new File(FileUtil.generateTemporaryFile());
		int count = 0;
		Scanner input = null;
		PrintWriter output = null;
		try {

			// create input and output stream
			input = new Scanner(new FileInputStream(file));
			output = new PrintWriter(new FileWriter(temp));

			// get each line of the input and write it to the output
			while (input.hasNextLine()) {
				String line = input.nextLine();
				if (line.trim().equals(lineToMatch)) {
					output.println(lineToWrite);
					count++;
				} else
					output.println(line);
			}
		} finally {
			if (input != null)
				input.close();
			if (output != null)
				output.close();
		}

		// rename output
		if (!file.delete())
			throw new IOException("Deletion of file failed.");
		if (!temp.renameTo(file))
			throw new IOException("Rename of file " + temp.getName() + " to " + file.getName() + " failed.");
		return count;
	}

	public static String getPureFileName(File file) {
		return FileUtil.getPureFileName(file.getName());
	}

	/**
	 * The method extracts the pure file name that means without extension and
	 * optional path information, thereby it doesn't matter if "/" or "\" is
	 * used as separator.
	 * 
	 * @param file
	 *            the filename to parse
	 * @return the passed filename without path or extension information
	 */
	public static String getPureFileName(String file) {
		int pos = file.lastIndexOf("\\");
		if (pos == -1)
			pos = file.lastIndexOf("/");
		return file.substring(pos + 1, file.lastIndexOf("."));
	}

	/**
	 * The method determines the file extension of the given file
	 * 
	 * @param file
	 *            the file name whose extension has to be determined
	 * @return the extension of the file
	 */
	public static String getFileExtension(String file) {
		return file.substring(file.lastIndexOf(".") + 1, file.length());
	}

	/**
	 * The method determines the file extension of the given file
	 * 
	 * @param file
	 *            the file whose extension has to be determined
	 * @return the extension of the file
	 */
	public static String getFileExtension(File file) {
		return getFileExtension(file.getName());
	}

	/**
	 * Generates a file in the denoted path, using the supplied name. If the
	 * file should be deleted when the virtual machine terminates, the parameter
	 * temporary has to be set to true.
	 * 
	 * @param path
	 *            the path of the file to be generated
	 * @param name
	 *            name of the file
	 * @param temporary
	 *            set to true if the file should be deleted when the virtual
	 *            machine terminates
	 * @return file object representing the generated file
	 */
	public static File generateFile(String path, String name, boolean temporary) {
		File file = new File(path, name);
		if (temporary)
			file.deleteOnExit();
		LOGGER.debug("File created: " + file.getAbsolutePath());
		return file;
	}

	/**
	 * Generates a directory in the denoted path using the supplied name. If the
	 * folder should be deleted when the virtual machine terminates, , the
	 * parameter temporary has to be set to true.
	 * 
	 * @param path
	 *            path in which the folder has to be generated
	 * @param folder
	 *            name of the folder
	 * @param temporary
	 *            true if the folder should be deleted when the virtual machine
	 *            terminates
	 * @return file object representing the generated folder
	 */
	public static File generateDirectory(String path, String folder, boolean temporary) {
		File directory = new File(path, folder);
		directory.mkdir();
		if (temporary)
			directory.deleteOnExit();
		LOGGER.debug("Directory created: " + directory.getAbsolutePath());
		return directory;
	}

	public static File generateTemporaryDirectory() {
		String path = getTemporaryDirectory();
		File tmpDir = new File(path, UUID.randomUUID().toString());
		tmpDir.mkdir();
		tmpDir.deleteOnExit();
		LOGGER.debug("Directory created: " + tmpDir.getAbsolutePath());
		return tmpDir;
	}

	public static String generateTemporaryFile(String directory) {
		return generateTemporaryFile(FileUtil.TMP_FILE_PREFIX, FileUtil.TMP_FILE_SUFFIX, directory);
	}

	/**
	 * Generates a temporary file, in the default temporary file directory of
	 * the current OS.
	 * 
	 * @return Path to the created temporary file.
	 */
	public static String generateTemporaryFile() {
		String directory = getTemporaryDirectory();
		return generateTemporaryFile(FileUtil.TMP_FILE_PREFIX, FileUtil.TMP_FILE_SUFFIX, directory);
	}

	public static String generateTemporaryFile(String prefix, String suffix) {
		String directory = getTemporaryDirectory();
		return generateTemporaryFile(prefix, suffix, directory);
	}

	public static String generateTemporaryFile(String prefix, String suffix, String directory) {

		// generate temporary file
		File file = null;
		try {
			if (directory != null)
				file = File.createTempFile(prefix, suffix, new File(directory));
			else
				file = File.createTempFile(prefix, suffix);
			file.deleteOnExit();
			return file.getAbsolutePath();
		} catch (IOException e) {
			LOGGER.error("Error creating temporary file in " + directory);
			return null;
		}
	}

	public static String copyStreamToString(InputStream stream) {
		if (stream != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[DEFAULT_BUFFER_SIZE];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
				int n = 0;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	public static String copyURLContentToString(URL url) {
		try {
			return copyStreamToString(url.openStream());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			return "";
		}
	}

	public static String copyFileContentToString(File file) {
		try {
			return copyURLContentToString(file.toURI().toURL());
		} catch (MalformedURLException e) {
			LOGGER.error(e.getMessage());
			return "";
		}
	}

	/**
	 * Copy a resource from the class loader to the specified file.
	 * 
	 * @param loader
	 *            Class loader used to locate the resource
	 * @param source
	 *            Name of the resource
	 * @param prefix
	 *            Prefix of the temporary file
	 * @param suffix
	 *            Suffix of the temporary file
	 * @return File object of the created temporary file
	 * @throws IOException
	 *             if an error occurs during the creation of the temporary file
	 */
	public static File copyResourceToTemporaryFile(ClassLoader loader, String source, String prefix, String suffix)
			throws IOException {
		File tmpFile = File.createTempFile(prefix, suffix);
		FileUtil.copyFromResource(loader, source, tmpFile);
		return tmpFile;
	}

	public static void copyFromResource(ClassLoader loader, String source, File destination) throws IOException {

		// load the resource and copy it to the given file
		InputStream istream = loader.getResourceAsStream(source);
		if (istream == null)
			throw new IOException("The resource " + source + " has not been found.");
		FileUtil.copyStream(istream, new BufferedOutputStream(new FileOutputStream(destination)), DEFAULT_BUFFER_SIZE,
				true);
	}

	public static String writeToTemporaryFile(String directory, String data) throws IOException {

		// create temporary file, if an error occurred return null
		String filePath = FileUtil.generateTemporaryFile(directory);
		if (filePath == null)
			return null;

		// use buffering
		Writer output = new BufferedWriter(new FileWriter(filePath));
		try {
			output.write(data);
		} finally {
			output.close();
		}
		return filePath;
	}

	public static void copyFile(File in, File out) throws IOException {

		FileInputStream inStream = new FileInputStream(in);
		FileOutputStream outStream = new FileOutputStream(out);
		FileChannel inChannel = inStream.getChannel();
		FileChannel outChannel = outStream.getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw e;
		} finally {

			// release resources
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
			if (inStream != null)
				inStream.close();
			if (outStream != null)
				outStream.close();
		}
	}

	/**
	 * Generates a unique file name with the given prefix and the passed
	 * extension.
	 * 
	 * @param prefix
	 *            prefix of the file
	 * @param extension
	 *            extension of the file if it does not start with a point it is
	 *            automatically extended
	 * @return the generated file
	 */
	public static String generateUniqueFileName(String prefix, String extension) {
		if (prefix == null || extension == null)
			throw new NullPointerException("Prefix or extension cannot be null");
		return new StringBuilder().append(prefix).append(UUID.randomUUID())
				.append(extension.startsWith(".") ? "" : ".").append(extension).toString();
	}

	public static String getTemporaryDirectory() {
		return System.getProperty("java.io.tmpdir");
	}

	/**
	 * Generates a unique directory name with the given prefix.
	 * 
	 * @param prefix
	 *            the prefix of the unique directory name
	 * @return the generated unique directory name
	 */
	public static String generateUniqueDirectoryName(String prefix) {
		if (prefix == null)
			throw new NullPointerException("Prefix or extension cannot be null");
		return new StringBuilder().append(prefix).append(UUID.randomUUID()).toString();
	}

}
