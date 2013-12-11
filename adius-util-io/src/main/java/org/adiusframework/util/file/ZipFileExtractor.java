package org.adiusframework.util.file;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.adiusframework.util.exception.IORuntimeException;

public class ZipFileExtractor {

	private String directory;
	private String zipFile;

	/**
	 * @param directory
	 *            the directory to set
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/**
	 * @return the directory
	 */
	public String getDirectory() {
		return this.directory;
	}

	/**
	 * @param zipFile
	 *            the zipFile to set
	 */
	public void setZipFile(String zipFile) {
		this.zipFile = zipFile;
	}

	/**
	 * @return the zipFile
	 */
	public String getZipFile() {
		return this.zipFile;
	}

	/**
	 * Constructor
	 */
	public ZipFileExtractor() {
		this(null);
	}

	public ZipFileExtractor(String zipFile) {
		this(zipFile, System.getProperty("java.io.tmpDir"));
	}

	public ZipFileExtractor(String zipFile, String directory) {
		this.setDirectory(directory);
		this.setZipFile(zipFile);
	}

	public List<String> extract() {
		List<String> filePaths = new Vector<String>();

		// check if a compressed file is given
		if (MimeTypeParser.isZipFile(this.getZipFile())) {

			// extract all files into temporary directory
			try {
				filePaths = ZipFileUtil.extract(this.getZipFile(), this.getDirectory(), true, true);
			} catch (IOException e) {
				throw new IORuntimeException(e);
			}
		} else {
			// no compressed file given, therefore use given input file instead
			filePaths.add(this.getZipFile());
		}
		return filePaths;
	}
}
