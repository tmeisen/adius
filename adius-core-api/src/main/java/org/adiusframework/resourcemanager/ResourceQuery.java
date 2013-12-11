package org.adiusframework.resourcemanager;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.adiusframework.resource.ResourceCapabilityRule;
import org.adiusframework.util.datastructures.SystemData;

/**
 * The ResourceQuery class is used to combine all data which are necessary to
 * find a Resource.
 */
public interface ResourceQuery extends Serializable {

	/**
	 * Returns a ResourceCapabilityRule which should be fulfilled by the
	 * searched Resource.
	 * 
	 * @return The ResourceCapabilityRule.
	 */
	public ResourceCapabilityRule getCapabilityRule();

	/**
	 * Return a List of Strings which represents protocols that are accepted for
	 * the searched Resource.
	 * 
	 * @return The List of protocols.
	 */
	public List<String> getProtocols();

	/**
	 * Return a List of Strings which represents types that are accepted for the
	 * searched Resource.
	 * 
	 * @return The List of resource-types.
	 */
	public List<String> getTypes();

	/**
	 * Tests if a set of protocols was defined for this query.
	 * 
	 * @return True if protocols were restricted, false otherwise.
	 */
	public boolean hasProtocols();

	public SystemData getSystemRequirement();

	public Properties getQueryDomainData();

}
