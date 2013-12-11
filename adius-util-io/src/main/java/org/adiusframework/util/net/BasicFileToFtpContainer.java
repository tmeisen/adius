package org.adiusframework.util.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.adiusframework.util.datastructures.FtpConnectionContainer;
import org.adiusframework.util.datastructures.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicFileToFtpContainer implements FileToFtpContainer {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicFileToFtpContainer.class);

	private ConcurrentMap<String, FileToFtpMapping> mapping;

	private FtpConnectionContainer ftpConnection;

	public BasicFileToFtpContainer() {
		this.mapping = new ConcurrentHashMap<String, FileToFtpMapping>();
	}

	@Override
	public Properties generate(String id, String ftpDir, Properties properties) {
		LOGGER.debug("Generating new ftp mapping for " + id);
		FileToFtpMapping container = new BasicFileToFtpMapping(this.getFtpConnection());
		this.mapping.put(id, container);
		return container.generate(ftpDir, properties);
	}

	@Override
	public void remove(String id) {
		if (!this.mapping.containsKey(id))
			return;
		LOGGER.debug("Removing ftp mapping of " + id);
		this.delete(id);
		this.mapping.remove(id);
	}

	@Override
	public void delete(String id) {
		if (!this.mapping.containsKey(id))
			return;
		LOGGER.debug("Deleting ftp files in mapping of " + id);
		FileToFtpMapping container = this.mapping.get(id);
		container.delete();
	}

	@Override
	public void revert(String id) {
		if (!this.mapping.containsKey(id))
			return;
		LOGGER.debug("Reverting ftp file transfer of " + id);
		FileToFtpMapping container = this.mapping.get(id);
		container.revert();
	}

	@Override
	public boolean exist(String id) {
		return this.mapping.containsKey(id);
	}

	/**
	 * @param ftpConnection
	 *            the ftpConnection to set
	 */
	public void setFtpConnection(FtpConnectionContainer ftpConnection) {
		this.ftpConnection = ftpConnection;
	}

	/**
	 * @return the ftpConnection
	 */
	public FtpConnectionContainer getFtpConnection() {
		return this.ftpConnection;
	}

}
