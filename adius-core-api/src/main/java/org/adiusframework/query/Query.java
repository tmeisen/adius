package org.adiusframework.query;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.adiusframework.resource.Resource;

public interface Query extends Serializable {

	public String getType();

	public String getDomain();

	public String getId();

	public List<Resource> getResources();

	public Properties getProperties();

}
