package org.adiusframework.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.adiusframework.resource.Resource;

public abstract class AbstractQuery implements Query {
	private static final long serialVersionUID = -6117597044615010624L;

	/**
	 * Member to hold the id
	 */
	private String id;

	/**
	 * Member to hold the domain
	 */
	private String domain;

	/**
	 * Member to hold the type
	 */
	private String type;

	/**
	 * List of resources
	 */
	private List<Resource> resources;

	/**
	 * List of properties
	 */
	private Properties properties;

	public AbstractQuery(String id, String type, String domain) {
		resources = new ArrayList<Resource>();
		properties = new Properties();
		setId(id);
		setType(type);
		setDomain(domain);
	}

	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	private void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the domain
	 */
	@Override
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain
	 *            the domain of the query
	 */
	private void setDomain(final String domain) {
		this.domain = domain;
	}

	/**
	 * @return the type
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type of the query
	 */
	private void setType(final String type) {
		this.type = type;
	}

	@Override
	public List<Resource> getResources() {
		return resources;
	}

	@Override
	public Properties getProperties() {
		return properties;
	}

	public void addResource(final Resource resource) {
		resources.add(resource);
	}

	public void initProperties(final Map<String, String> paramMap) {
		if (paramMap == null)
			return;
		for (Entry<String, String> entry : paramMap.entrySet()) {
			properties.setProperty(entry.getKey(), entry.getValue());
		}
	}

	public void initResources(final Map<String, String> paramMap) {
		if (paramMap == null)
			return;
		for (Entry<String, String> entry : paramMap.entrySet()) {

			// check if key or value is null
			if (entry.getKey() == null || entry.getValue() == null) {
				continue;
			}

			// else we can go on
			Resource r = null;
			if (QueryResourceFactory.isFileResource(entry.getValue())) {
				r = QueryResourceFactory.createFileSystemResource(entry.getValue(), entry.getKey());
			} else {
				r = QueryResourceFactory.createObjectResource(entry.getValue(), entry.getKey());
			}
			addResource(r);
		}
	}

	/**
	 * @return true if all necessary information have been set, otherwise false
	 */
	public abstract boolean isValid();

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ExecutionQuery [id=");
		builder.append(id);
		builder.append(", domain=");
		builder.append(domain);
		builder.append(", type=");
		builder.append(type);
		builder.append(", resources=");
		builder.append(resources);
		builder.append("]");
		return builder.toString();
	}

}
