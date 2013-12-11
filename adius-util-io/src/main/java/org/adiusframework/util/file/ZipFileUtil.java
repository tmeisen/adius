package org.adiusframework.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Static class containing util methods to extract and build zip files.
 * 
 * @author tmeisen
 */
public class ZipFileUtil {
	/**
	 * Constant defining the default buffer size of input and output streams.
	 */
	private static final Integer BUFFER_SIZE = 524288;

	/**
	 * Extracts the given zip file to the given path.
	 * 
	 * @param file
	 *            File object containing a reference to the zip file.
	 * @param destPath
	 *            Destination path the zip file has to be extracted to.
	 * @param temporary
	 *            If the extracted folders and files should be deleted after the
	 *            java vm stops set to true.
	 * @return List of extracted files
	 * @throws ZipException
	 *             if a ZIP format error has occurred
	 * @throws IOException
	 *             if an I/O error has occured
	 */
	public static List<String> extract(File file, File destPath, Boolean temporary) throws ZipException, IOException {
		// create list for file storage
		List<String> files = new Vector<String>();

		// extract the zip file
		ZipFile zipFile = new ZipFile(file);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {

			// create the file for current file or directory in the ZIP-file
			ZipEntry entry = entries.nextElement();
			String compFileName = entry.getName();
			File entryDestination = new File(destPath, compFileName);

			// if temporary set delete on exit
			if (temporary)
				entryDestination.deleteOnExit();

			// the file may be in a sub-folder in the ZIP-bundle this line
			// ensures the parent folders are all
			// created.
			entryDestination.getParentFile().mkdirs();

			// if no directory is given extract the file from the archive and
			// add to files list
			if (!entry.isDirectory()) {
				generateFile(entryDestination, entry, zipFile);
				files.add(entryDestination.getAbsolutePath());
			}
		}
		return files;
	}

	public static void compress(List<String> filePaths, String destination) throws IOException {
		InputStream in = null;
		ZipOutputStream out = null;

		try {
			// create the destination file
			File destFile = new File(destination);
			if (!destFile.exists())
				destFile.createNewFile();

			// create stream to write data to zip file
			out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destFile)));
			out.setLevel(java.util.zip.Deflater.BEST_SPEED);

			// write each file into zip file
			for (String filePath : filePaths) {
				File inputFile = new File(filePath);
				in = new BufferedInputStream(new FileInputStream(inputFile));

				// create entry for the file and write into zip file
				ZipEntry entry = new ZipEntry(inputFile.getName());
				out.putNextEntry(entry);

				// pump data from file into zip file
				byte[] buf = new byte[BUFFER_SIZE];
				int len;
				while ((len = in.read(buf)) > 0)
					out.write(buf, 0, len);
			}
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}

	/**
	 * Extracts a given ZIP-file to the given path. Provides the creation of
	 * sub-directory.
	 * 
	 * @param zipFilePath
	 *            Path to the ZIP-file that has to be extracted.
	 * @param extractPath
	 *            Path the ZIP-file has to be extract to.
	 * @param createSubDir
	 *            If a sub-directory should be created this parameter has to be
	 *            true. The sub-directory is generated within the path set by
	 *            extractPath and has the name of the given ZIP-file.
	 * @param temporary
	 *            If the extracted folders and files should be deleted after the
	 *            java VM stops set to true.
	 * @return List of the extracted files with absolute path
	 * @throws ZipException
	 *             if a ZIP-format error has occurred
	 * @throws IOException
	 *             if an I/O error has occurred
	 */
	public static List<String> extract(String zipFilePath, String extractPath, Boolean createSubDir, Boolean temporary)
			throws ZipException, IOException {

		// create file
		File file = new File(zipFilePath);

		// if a sub-directory has to be created create it
		File destPath;
		if (createSubDir) {
			File zipDir = new File(extractPath, FileUtil.getPureFileName(file));
			zipDir.mkdir();
			zipDir.deleteOnExit();
			destPath = zipDir;
		} else
			destPath = new File(extractPath);

		// extract file into destination
		return ZipFileUtil.extract(file, destPath, temporary);
	}

	/**
	 * Helper method that extracts an entry of a ZIP-file. The ZIP-file, the
	 * entry and the destination have to be provided.
	 * 
	 * @param destination
	 *            Destination directory of the extract file, the file is named
	 *            as stored in the ZIP-file
	 * @param entry
	 *            Entry of the ZIP-file that has to be extracted
	 * @param owner
	 *            ZIP-file containing the entry *
	 * @throws IOException
	 *             if an I/O error has occurred
	 */
	private static void generateFile(File destination, ZipEntry entry, ZipFile owner) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(owner.getInputStream(entry));
			out = new BufferedOutputStream(new FileOutputStream(destination));

			// pump data from ZIP-file into new files
			byte[] buf = new byte[BUFFER_SIZE];
			int len;
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len);
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		}
	}
}
