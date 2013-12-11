package org.adiusframework.resource;

import java.io.Serializable;
import java.util.UUID;

import org.adiusframework.util.datastructures.SystemData;

public interface Resource extends Serializable {

	public static final String TYPE_DIRECTORY = "directory", TYPE_FILE = "file", TYPE_DATABASE = "database",
			TYPE_FILESERVER = "file_server", TYPE_ENTITY = "entity", TYPE_OBJECT = "object",
			TYPE_PROPERTIES = "properties";

	public static final String PROTOCOL_FTP = "ftp", PROTOCOL_LOCAL = "local", PROTOCOL_MYSQL = "mysql",
			PROTOCOL_CLASSPATH = "classpath";

	public UUID getUUID();

	public String getType();

	public String getProtocol();

	public boolean hasProtocol();

	public ResourceCapability getCapability();

	public SystemData getSystemData();

}
