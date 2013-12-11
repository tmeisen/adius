package org.adiusframework.resource.serviceplan;

import java.util.Properties;

import org.adiusframework.service.xml.Category;

public class DefaultServiceTemplate implements ServiceTemplate {
	private static final long serialVersionUID = -8473775905434215184L;

	private Category category;

	private String subCategory;

	private Properties properties;

	public DefaultServiceTemplate(Category category, String subCategory) {
		setCategory(category);
		setSubCategory(subCategory);
		setProperties(new Properties());
	}

	protected void setProperties(Properties properties) {
		this.properties = properties;
	}

	@Override
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Override
	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	public void addProperty(String property, String value) {
		properties.setProperty(property, value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder().append(getCategory().toString()).append("|")
				.append(getSubCategory()).append(": ");
		for (String property : getProperties().stringPropertyNames()) {
			builder.append(property).append(":").append(getProperties().getProperty(property)).append(",");

		}
		if (getProperties().size() > 0)
			builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

}
