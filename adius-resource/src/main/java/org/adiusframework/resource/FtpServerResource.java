package org.adiusframework.resource;

import java.net.MalformedURLException;
import java.net.URL;

import org.adiusframework.util.datastructures.FtpConnectionContainer;
import org.adiusframework.util.file.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpServerResource extends AbstractResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpServerResource.class);

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 7414019614999261476L;

	private FtpConnectionContainer ftpConnection;

	private String defaultPath;

	public FtpServerResource() {
		setType(Resource.TYPE_FILESERVER);
		setProtocol(Resource.PROTOCOL_FTP);
		setDefaultPath("/");
	}

	public void setFtpConnection(FtpConnectionContainer ftpConnection) {
		this.ftpConnection = ftpConnection;
	}

	public FtpConnectionContainer getFtpConnection() {
		return ftpConnection;
	}

	public void setDefaultPath(String defaultPath) {
		try {
			this.defaultPath = FileUtil.ensureDirectorySeparator(defaultPath);
		} catch (MalformedURLException e) {
			LOGGER.error("Malformed directory " + defaultPath);
			// do nothing
		}
	}

	public String getDefaultPath() {
		return defaultPath;
	}

	public URL getURL() {
		return getFtpConnection().getURL();
	}

	@Override
	public String toString() {
		return getFtpConnection().toString() + ", default path = " + getDefaultPath();
	}
}
