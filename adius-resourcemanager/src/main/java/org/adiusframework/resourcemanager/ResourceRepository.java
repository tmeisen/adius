package org.adiusframework.resourcemanager;

import java.util.List;
import java.util.Map;

import org.adiusframework.resource.Resource;
import org.adiusframework.util.IsConfigured;

/**
 * A ResourceRepository is the gateway for organizing and saving
 * ResourceContainers which are currently managed by the resource-manager.
 */
public interface ResourceRepository extends IsConfigured {

	/**
	 * Returns the container that belongs to this uri. If no container exists
	 * the parent uris are checked until the root is reached, if and if only
	 * parent is set to true. Otherwise null is returned.
	 * 
	 * @param uri
	 *            the uri whose container has to be determined.
	 * @param parent
	 *            if set to true a parent container is searched if no container
	 *            is present at the given uri.
	 * @return null if no container exists and parent is set to false, otherwise
	 *         either the container is returned, or the first container that is
	 *         one the path of the given uri.
	 */
	public abstract ResourceContainer getContainer(String uri, final boolean parent);

	/**
	 * Sets the container of the uri. If a container is already assigned to this
	 * uri, the container is returned. Otherwise, a new container is created.
	 * 
	 * @param uri
	 *            the uri the container belongs to.
	 * @return if a container already exists this container is returned,
	 *         otherwise the newly created container.
	 */
	public abstract ResourceContainer setContainer(String uri);

	/**
	 * Adds a list of resources to the uris. The uris are the keys of a map,
	 * whereby each key is related to a list of resources. Using this method,
	 * the resources of the resource repository can be set easily.
	 * 
	 * @param resources
	 *            the map of resources, containing the uri as key and the
	 *            resources of the key as list.
	 */
	public abstract void setContainers(final Map<String, List<Resource>> resources);

	/**
	 * Removes the container that belongs to the uri. If containers of childs
	 * should be removed, too, the parameter has to be set to true.
	 * 
	 * @param uri
	 *            Container's uri that have to be removed
	 * @param removeChilds
	 *            true if all childs of the uri have to be removed, too
	 * @return a list of resources that have been stored in the removed
	 *         containers
	 */
	public abstract List<Resource> removeContainers(String uri, boolean removeChilds);

}