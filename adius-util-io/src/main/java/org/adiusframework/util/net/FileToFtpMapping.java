package org.adiusframework.util.net;

import org.adiusframework.util.datastructures.Properties;

public interface FileToFtpMapping {

	public Properties generate(String ftpDir, Properties properties);

	public void revert();

	public void delete();

}
