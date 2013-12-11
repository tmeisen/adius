package org.adiusframework.resourcemanager;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.adiusframework.resource.ResourceCapabilityRule;
import org.adiusframework.resource.StringResourceCapabilityRule;
import org.adiusframework.service.xml.ResourceRequirement;
import org.adiusframework.util.datastructures.SystemData;

/**
 * The StandardResourceQuery is a superficial implementation of the
 * ResourceQuery interface. It provides the members to store the information
 * which is related to the ResourceQuery and a constructor for initializing.
 */
public class StandardResourceQuery implements ResourceQuery {

	private static final String SPLIT_CHAR = ",";

	private static final long serialVersionUID = -4600696761068975800L;

	private List<String> protocols;

	private List<String> types;

	private Properties queryDomainData;

	private ResourceCapabilityRule capabilityRule;

	private SystemData systemRequirement;

	public StandardResourceQuery() {
		setSystemRequirement(null);
	}

	/**
	 * Creates a new StandardResourceQuery with the given properties.
	 * 
	 * @param resourceRequirement
	 *            The requirements the Resource should fulfill.
	 * @param systemRequirement
	 *            The SystemData that the Resource must have.
	 */
	public StandardResourceQuery(ResourceRequirement resourceRequirement, SystemData systemRequirement) {
		setCapabilityRule(new StringResourceCapabilityRule(resourceRequirement.getCapabilityRule()));
		setTypes(Arrays.asList(resourceRequirement.getTypes().split(SPLIT_CHAR)));
		if (resourceRequirement.getProtocols() != null && !resourceRequirement.getProtocols().isEmpty())
			setProtocols(Arrays.asList(resourceRequirement.getProtocols().split(SPLIT_CHAR)));
		setSystemRequirement(systemRequirement);
		setQueryDomainData(new Properties());
	}

	@Override
	public SystemData getSystemRequirement() {
		return systemRequirement;
	}

	/**
	 * Sets new SystemData.
	 * 
	 * @param systemRequirement
	 *            The new SystemData.
	 */
	public void setSystemRequirement(SystemData systemRequirement) {
		this.systemRequirement = systemRequirement;
	}

	@Override
	public ResourceCapabilityRule getCapabilityRule() {
		return capabilityRule;
	}

	/**
	 * Sets a new ResourceCapabilityRule.
	 * 
	 * @param capabilityRule
	 *            The new ResourceCapabilityRule.
	 */
	public void setCapabilityRule(ResourceCapabilityRule capabilityRule) {
		this.capabilityRule = capabilityRule;
	}

	@Override
	public List<String> getProtocols() {
		return protocols;
	}

	@Override
	public boolean hasProtocols() {
		if (protocols == null)
			return false;
		return protocols.size() > 0;
	}

	/**
	 * Sets a new protocol-list.
	 * 
	 * @param protocols
	 *            The new list.
	 */
	public void setProtocols(List<String> protocols) {
		this.protocols = protocols;
	}

	@Override
	public List<String> getTypes() {
		return types;
	}

	/**
	 * Sets a new type-list.
	 * 
	 * @param types
	 *            The new list.
	 */
	public void setTypes(List<String> types) {
		this.types = types;
	}

	@Override
	public Properties getQueryDomainData() {
		return queryDomainData;
	}

	/**
	 * Sets new QueryDomainData.
	 * 
	 * @param queryDomainData
	 *            The new QueryDomainData.
	 */
	protected void setQueryDomainData(Properties queryDomainData) {
		this.queryDomainData = queryDomainData;
	}

	/**
	 * Adds a specific value with a specific key to the QueryDomainData.
	 * 
	 * @param key
	 *            The key for the value.
	 * @param value
	 *            The value to be added.
	 */
	public void addQueryDomainData(String key, String value) {
		queryDomainData.setProperty(key, value);
	}

}
