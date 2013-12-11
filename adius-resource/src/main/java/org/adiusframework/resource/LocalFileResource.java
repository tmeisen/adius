package org.adiusframework.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Transient
public class LocalFileResource extends FileResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileResource.class);

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -5806576920754722886L;

	public LocalFileResource() {
		setProtocol(Resource.PROTOCOL_LOCAL);
	}

	public LocalFileResource(String path) {
		this();
		setPath(path);
	}

	public File getFileInstance() {
		return new File(getPath());
	}

	@Override
	public String getUrlForm() {
		try {
			
			// make use of url implementation
			return new URL("file://" + getPath()).toExternalForm();
		} catch (MalformedURLException e) {
			LOGGER.error("Unsufficient data to generate URL");
			return null;
		}
	}

	@Override
	public String toString() {
		return new StringBuffer().append("Local file resource (").append(getPath()).append(")").toString();
	}

	@Override
	public boolean supportsInputStream() {
		return true;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		return new FileInputStream(getPath());
	}

}
