package org.adiusframework.resourcemanager;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.adiusframework.resource.Resource;
import org.adiusframework.resource.ResourceCapabilityRule;
import org.adiusframework.util.IsConfigured;

/**
 * The ResourceContainer manages Resources which are connected to this
 * container. It provides methods to find Resources with special conditions.
 */
public interface ResourceContainer extends Serializable, IsConfigured {

	/**
	 * Method to acquire all resources that belongs to this container.
	 * 
	 * @return the list of resources that are stored inside the container.
	 */
	public List<Resource> getAllResources();

	/**
	 * Goes through all Resources which belong to this ResourceContainer and
	 * determines all of them, which fulfill a specific rule.
	 * 
	 * @param rule
	 *            The rule which should be satisfied.
	 * @param fullScan
	 *            Indicates if Resources which belong to a parental resource
	 *            container should be included.
	 * @return A List with all Resources which satisfy the given rule.
	 */
	public List<Resource> getResources(ResourceCapabilityRule rule, boolean fullScan);

	/**
	 * Goes through all Resources which belong to this ResourceContainer and
	 * determines all of them, which fulfill a specific rule and specific
	 * conditions.
	 * 
	 * @param rule
	 *            The rule which should be satisfied.
	 * @param execConditions
	 *            The conditions which should be fulfilled.
	 * @param fullScan
	 *            Indicates if Resources which belong to a parental resource
	 *            container should be included.
	 * @return A List with all Resources which satisfy the given rule.
	 */
	public List<Resource> getResources(ResourceCapabilityRule rule, Properties execConditions, boolean fullScan);

	/**
	 * Goes through all Resources which belong to this ResourceContainer and
	 * determines all of them, which have a specific type.
	 * 
	 * @param c
	 *            The specific type.
	 * @param fullScan
	 *            Indicates if Resources which belong to a parental resource
	 *            container should be included.
	 * @return A List with all Resources which have to specified type.
	 */
	public List<Resource> getResourcesByClass(Class<? extends Resource> c, boolean fullScan);

	/**
	 * Adds the given resource to the container. If the operation fails and the
	 * container is not changed by this operation, false is returned.
	 * 
	 * @param resource
	 *            Resource that have to be added to the container
	 * @return true if the container has been changed by this operation.
	 */
	public boolean addResource(Resource resource);

	/**
	 * Removes the given resource from the container.
	 * 
	 * @param resource
	 *            Resource that have to be removed
	 * @return true if a resource have been removed, otherwise false.
	 */
	public boolean removeResource(Resource resource);

}
