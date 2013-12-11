package org.adiusframework.resource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ClassPathFileResource extends FileResource {
	private static final long serialVersionUID = 1658988042241714633L;

	public static final String URL_CLASSPATH_PREFIX = Resource.PROTOCOL_CLASSPATH + ":";

	private ClassLoader cl;

	public ClassPathFileResource() {
		setProtocol(Resource.PROTOCOL_CLASSPATH);
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Exception e) {
			cl = ClassLoader.getSystemClassLoader();
		}
	}

	@Override
	public String getUrlForm() {
		return URL_CLASSPATH_PREFIX + getPath();
	}

	@Override
	public boolean supportsInputStream() {
		return true;
	}

	@Override
	public InputStream openInputStream() throws IOException {
		URL url = getClass().getResource(getPath());
		if (url == null) {
			url = cl.getResource(getPath());
		}
		if (url == null) {
			throw new FileNotFoundException("Stream creation for system resource " + getPath() + " failed");
		}
		return url.openStream();
	}

	@Override
	public String toString() {
		return new StringBuffer().append("Classpath (").append(URL_CLASSPATH_PREFIX).append(getPath()).append(")")
				.toString();
	}

}
