package org.adiusframework.resource;

import java.util.Properties;

public class PropertiesResource extends ObjectResource<Properties> {

	private static final long serialVersionUID = -7221089684125271785L;

	public PropertiesResource() {
		super();
		setType(Resource.TYPE_PROPERTIES);
	}

	public Properties getProperties() {
		return getObject();
	}

	public void setProperties(Properties properties) {
		setObject(properties);
	}

}
