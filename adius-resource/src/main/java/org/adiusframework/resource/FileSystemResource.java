package org.adiusframework.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * The class is used for all resources that represent, in some way, a file
 * system resource like FTP, HTTP or local data objects. Such resource can be
 * identified by an URL, hence such resources must provide a method to get the
 * resource URL.
 * 
 * @author tm807416
 * 
 */
public abstract class FileSystemResource extends AbstractResource {
	private static final long serialVersionUID = 7001456148748905873L;

	public abstract String getUrlForm();

	public boolean isDirectory() {
		return TYPE_DIRECTORY.equals(getType());
	}

	public boolean isFile() {
		return TYPE_FILE.equals(getType());
	}

	public abstract boolean supportsInputStream();

	public abstract InputStream openInputStream() throws IOException;

}
