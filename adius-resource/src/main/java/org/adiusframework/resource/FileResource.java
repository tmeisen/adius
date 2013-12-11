package org.adiusframework.resource;

import java.net.MalformedURLException;

import org.adiusframework.util.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileResource extends FileSystemResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(FileResource.class);

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -8543147995909648445L;

	private String directory;
	
	private String file;

	public FileResource() {
		setType(Resource.TYPE_FILE);
	}

	public String getPath() {
		return this.getDirectory() + this.getFile();
	}

	public void setPath(String path) {
		try {
			String[] fileData = FileUtil.getPathAndFile(path);
			setDirectory(fileData[0]);
			setFile(fileData[1]);
		} catch (MalformedURLException e) {
			LOGGER.error("MalformedURLException occured: " + e.getMessage());
		}
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFile() {
		return file;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}
	
}
