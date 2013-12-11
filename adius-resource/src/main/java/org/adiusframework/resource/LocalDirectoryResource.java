package org.adiusframework.resource;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalDirectoryResource extends DirectoryResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalDirectoryResource.class);

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 502860633565853266L;

	public LocalDirectoryResource() {
		setProtocol(Resource.PROTOCOL_LOCAL);
	}

	public LocalDirectoryResource(String directory) {
		this();
		setDirectory(directory);
	}

	@Override
	public String getUrlForm() {
		try {
			// make use of url implementation
			return new URL("file://" + getDirectory()).toExternalForm();
		} catch (MalformedURLException e) {
			LOGGER.error("Unsufficient data to generate URL");
			return null;
		}
	}

	@Override
	public String toString() {
		return "Local directory (" + getUrlForm() + ")";
	}

}
