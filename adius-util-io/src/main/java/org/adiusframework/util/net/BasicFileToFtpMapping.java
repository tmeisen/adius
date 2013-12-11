package org.adiusframework.util.net;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.adiusframework.util.datastructures.FtpConnectionContainer;
import org.adiusframework.util.datastructures.Pair;
import org.adiusframework.util.datastructures.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicFileToFtpMapping implements FileToFtpMapping {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicFileToFtpMapping.class);

	private ConcurrentMap<String, Pair<String, String>> mapping;

	private FtpConnectionContainer ftpConnection;

	public BasicFileToFtpMapping(FtpConnectionContainer ftpConnection) {
		this.ftpConnection = ftpConnection;
	}

	@Override
	public Properties generate(String ftpDir, Properties preProps) {
		this.mapping = new ConcurrentHashMap<String, Pair<String, String>>();
		Properties postProps = new Properties();

		// check for each key if a locale file is given, if so copy it to the
		// ftp server, and store the change in the mapping
		for (String key : preProps.stringPropertyNames()) {
			String preValue = preProps.getProperty(key);
			File file = new File(preValue);
			if (file.isFile()) {
				LOGGER.debug("Starting copy of " + file + " to " + ftpDir);
				String postValue = FtpUtil.copyToFtpServer(this.ftpConnection, file, ftpDir, null);
				LOGGER.debug("Copy finished");
				this.mapping.put(key, new Pair<String, String>(preValue, postValue));
				postProps.setProperty(key, postValue);
			} else
				postProps.setProperty(key, preValue);
		}
		return postProps;
	}

	@Override
	public void revert() {
		for (String key : this.mapping.keySet()) {
			Pair<String, String> valuePair = this.mapping.get(key);
			LOGGER.debug("Starting copy of " + valuePair.getValue2() + " to " + valuePair.getValue1());
			FtpUtil.copyToFile(this.ftpConnection, valuePair.getValue2(), valuePair.getValue1());
		}
		this.delete();
	}

	@Override
	public void delete() {
		for (String key : this.mapping.keySet()) {
			Pair<String, String> valuePair = this.mapping.get(key);
			FtpUtil.delete(this.ftpConnection, valuePair.getValue2(), true);
		}
	}

}
