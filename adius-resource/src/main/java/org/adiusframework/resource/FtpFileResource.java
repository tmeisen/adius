package org.adiusframework.resource;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.adiusframework.util.datastructures.FtpConnectionContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpFileResource extends FileResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpFileResource.class);

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 3710229757254169747L;

	private FtpServerResource ftpServerResource;

	public FtpFileResource() {
		setProtocol(Resource.PROTOCOL_FTP);
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
	public boolean supportsInputStream() {
		return false;
	}

	@Override
	public InputStream openInputStream() {
		throw new UnsupportedOperationException("Inputstream not supported for this resource");
	}

	@Override
	public String getUrlForm() {
		try {

			// make use of url implementation
			return new URL(getFtpServerResource().getURL().toExternalForm() + getPath()).toExternalForm();
		} catch (MalformedURLException e) {
			LOGGER.error("Unsufficient data to generate URL");
			return null;
		}
	}

	@Override
	public String toString() {
		return new StringBuffer().append("Ftp File Resource (").append(getUrlForm()).append(")")
				.toString();
	}

}
