package org.adiusframework.resource.serviceplan;

import java.io.Serializable;
import java.util.Properties;

import org.adiusframework.service.xml.Category;

public interface ServiceTemplate extends Serializable {

	public Category getCategory();

	public String getSubCategory();

	public Properties getProperties();

}
