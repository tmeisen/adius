package org.adiusframework.resourcemanager;

import org.adiusframework.resource.ConverterManager;
import org.adiusframework.resource.Resource;
import org.adiusframework.resource.ResourceCapabilityRule;
import org.adiusframework.util.IsConfigured;
import org.adiusframework.util.datastructures.SystemData;

/**
 * The interface defining the resource manager service of the ADIuS framework.
 * 
 * @author tm807416
 * 
 */
public interface ResourceManager extends IsConfigured {

	/**
	 * Checks if the capability is provided by any resource in the given URI.
	 * 
	 * @param uri
	 *            the URI defining the container used to search for the resource
	 * @param rule
	 *            capability rule describing the requirements that have to be
	 *            fulfilled by the searched resource
	 * @return true if and only if at least one resource fulfilling the rule
	 *         exists in the given URI
	 */
	public abstract boolean existResource(String uri, ResourceCapabilityRule rule);

	/**
	 * Searches for a resource on the given URI satisfying the query.
	 * 
	 * @param uri
	 *            the URI defining the container used to search for a resource
	 * @param query
	 *            query describing the requirements that have to be fulfilled by
	 *            the resource
	 * @return a list of the found resources fulfilling the query.
	 */
	public abstract Resource getResource(String uri, ResourceQuery query);

	/**
	 * Registers a new resource in the given URI. Thereby it is checked if this
	 * resource can be accessed by the resource manager. If not an exception is
	 * thrown. Use {@link ResourceManager#validateResource(Resource)} to check
	 * the accessibility.
	 * 
	 * @param uri
	 *            the URI defining the path to the resource
	 * @param resource
	 *            the resource that have to be registered
	 * @return true if the resource has been successfully registered
	 * @throws NonAccessibleResourceException
	 *             if the resource cannot be accessed by the resource manager
	 */
	public abstract boolean registerResource(String uri, Resource resource) throws NonAccessibleResourceException;

	/**
	 * All resources that are part of the URI (or any child) are removed.
	 * 
	 * @param uri
	 *            the URI whose resources should be removed
	 */
	public abstract void unregisterResources(String uri);

	/**
	 * Validates if a resource can be registered in this resource manager.
	 * 
	 * @param resource
	 *            Resource that has to be validated.
	 * @return true if the resource can be accessed by the resource manager when
	 *         this method has been called, otherwise false
	 */
	public abstract boolean validateResource(Resource resource);

	/**
	 * Validates if the given resource can be used by the system represented by
	 * the system data.
	 * 
	 * @param resource
	 *            Resource that has to be validated.
	 * @param systemData
	 *            Target system which wants to use the resource.
	 * @return true if the resource can be accessed by the target system,
	 *         otherwise false
	 */
	public abstract boolean validateResource(Resource resource, SystemData systemData);

	/**
	 * Converts the given resource into a resource of the class specified by the
	 * 'to' parameter. Therefore, the method tries to find a compatible
	 * converter using the registered {@link ConverterManager}.
	 * 
	 * @param uri
	 *            URI used to register the newly created resource.
	 * @param from
	 *            Resource that have to be converted.
	 * @param query
	 *            Query that have to be fulfilled by the converted resource.
	 * @param temporary
	 *            if set to true the converted resource is added to the list of
	 *            temporary resources that will be removed, when the resource is
	 *            released
	 * @return null if no conversion has been possible, otherwise the result of
	 *         the conversion.
	 * @throws NonAccessibleResourceException
	 *             if the given resource cannot be accessed by the resource
	 *             manager.
	 */
	public abstract Resource convertResource(String uri, Resource from, ResourceQuery query, boolean temporary)
			throws NonAccessibleResourceException;

	/**
	 * Updates all resources within the URI that satisfies the rule.
	 * 
	 * @param uri
	 *            URI used to identify the resources.
	 * @param rule
	 *            Rule that have to be satisfied.
	 */
	public abstract void updateResources(String uri, ResourceCapabilityRule rule);

}
