package org.adiusframework.resource;

import java.io.IOException;
import java.io.InputStream;

import org.adiusframework.util.file.FileUtil;

public abstract class DirectoryResource extends FileSystemResource {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 2991676844830197674L;

	private String directory;

	public DirectoryResource() {
		setType(TYPE_DIRECTORY);
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		if (directory.endsWith("\\") || directory.endsWith("/"))
			this.directory = directory;
		else
			this.directory = directory + getFileSeparator();
	}

	/**
	 * Method defines the file separator which is used for this directory
	 * resource. The default implementation uses
	 * System.getProperty("file.separator").
	 * 
	 * @return the used file separator
	 */
	protected String getFileSeparator() {
		return FileUtil.getFileSeparator();
	}

	@Override
	public boolean supportsInputStream() {
		return false;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		throw new UnsupportedOperationException("Inputstream not supported for this resource");
	}

}
