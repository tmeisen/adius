package org.adiusframework.resource;

import java.net.MalformedURLException;
import java.net.URL;

import org.adiusframework.util.datastructures.FtpConnectionContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpDirectoryResource extends DirectoryResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpDirectoryResource.class);

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -1589704830960059669L;

	private FtpServerResource ftpServerResource;

	public FtpDirectoryResource() {
		setProtocol(PROTOCOL_FTP);
	}

	public FtpDirectoryResource(String directory) {
		this();
		setDirectory(directory);
	}

	public void setFtpServerResource(FtpServerResource ftpServerResource) {
		this.ftpServerResource = ftpServerResource;
	}

	public FtpServerResource getFtpServerResource() {
		return ftpServerResource;
	}

	public FtpConnectionContainer getFtpConnection() {
		return ftpServerResource.getFtpConnection();
	}

	@Override
	public String getUrlForm() {
		try {
			
			// make use of url implementation
			return new URL(getFtpServerResource().getURL().toExternalForm() + getDirectory()).toExternalForm();
		} catch (MalformedURLException e) {
			LOGGER.error("Unsufficient data to generate URL");
			return null;
		}
	}

	@Override
	public String toString() {
		return new StringBuffer("FtpDirectoryResource ").append(getDirectory()).append(" using ")
				.append(getFtpServerResource()).toString();
	}

	@Override
	protected String getFileSeparator() {
		return "/";
	}

}
