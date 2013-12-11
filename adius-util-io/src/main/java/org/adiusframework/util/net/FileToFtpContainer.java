package org.adiusframework.util.net;

import org.adiusframework.util.datastructures.Properties;

public interface FileToFtpContainer {

	public Properties generate(String id, String ftpDir, Properties properties);

	public void remove(String id);

	public void delete(String id);

	public void revert(String id);

	public boolean exist(String id);
}
