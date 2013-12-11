package org.adiusframework.processmanager;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.InvalidDomainException;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;
import org.adiusframework.resource.ObjectResource;
import org.adiusframework.resource.PropertiesResource;
import org.adiusframework.resource.Resource;
import org.adiusframework.resource.StringResourceCapability;
import org.adiusframework.resource.StringResourceCapabilityRule;
import org.adiusframework.resourcemanager.NonAccessibleResourceException;
import org.adiusframework.resourcemanager.ResourceManager;
import org.adiusframework.resourcemanager.StandardResourceQuery;
import org.adiusframework.service.StandardServiceResult;
import org.adiusframework.service.xml.ResourceRequirement;
import org.adiusframework.util.datastructures.SystemData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * The AbstractResourceHandler implements the ResourceHandler interface by
 * providing methods to create an uri for a ServiceProcess which is used to
 * store Resource objects with that id.
 */
public abstract class AbstractResourceHandler implements ResourceHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResourceHandler.class);

	public static final String RESOURCE_ENTITY_ID_NAME = "entity_identifier";

	private ResourceManager resourceManager;

	private RELParser relParser;

	/**
	 * @return The ResourceManager which manages all Resource objects.
	 */
	@Required
	public ResourceManager getResourceManager() {
		return resourceManager;
	}

	/**
	 * Sets a new ResourceManager.
	 * 
	 * @param resourceManager
	 *            The new ResourceManager.
	 */
	public void setResourceManager(ResourceManager resourceManager) {
		this.resourceManager = resourceManager;
	}

	/**
	 * @return The RELParser which is used to parse a RELExpression to valid
	 *         CapabilityRule.
	 */
	@Required
	public RELParser getRELParser() {
		return relParser;
	}

	/**
	 * Sets a new RELParser.
	 * 
	 * @param relParser
	 *            The new RELParser.
	 */
	public void setRELParser(RELParser relParser) {
		this.relParser = relParser;
	}

	@Override
	public boolean checkConfiguration() {
		if (resourceManager == null || relParser == null)
			return false;

		return resourceManager.checkConfiguration();
	}

	@Override
	public void registerResources(ServiceProcess process, StandardServiceResult result) throws ProcessManagerException {
		String processUri = generateResourceUri(process);
		for (Resource r : result.getResources())
			registerResource(processUri, r);
	}

	@Override
	public void registerResources(ServiceProcess process, Query query) throws ProcessManagerException {
		LOGGER.debug("Registering resources of service process [" + process + "] to " + query);

		// lets generate the uri of the resources
		String processUri = generateResourceUri(process);
		String entityUri = generateEntityResourceUri(process);

		// now lets check if we have to register the entity id as resource
		if (!getResourceManager().existResource(entityUri, new StringResourceCapabilityRule(RESOURCE_ENTITY_ID_NAME))) {
			ObjectResource<Integer> or = new ObjectResource<Integer>();
			or.setCapability(new StringResourceCapability(RESOURCE_ENTITY_ID_NAME));
			or.setObject(process.getEntityId());
			registerResource(entityUri, or);
		}

		// next we have to register the query specific resources
		if (query.getResources() != null) {
			for (Resource ar : query.getResources()) {
				registerResource(processUri, ar);
			}
		}

		// before we come to an end we have to register the common resources of
		// a query (namely the properties)
		if (query.getProperties() != null) {
			PropertiesResource pr = new PropertiesResource();
			pr.setProperties(query.getProperties());
			pr.setCapability(new StringResourceCapability("user_properties"));
			registerResource(processUri, pr);
		}
	}

	@Override
	public void releaseResources(ServiceProcess process) throws ProcessManagerException {
		String uri = generateResourceUri(process);
		getResourceManager().unregisterResources(uri);
	}

	@Override
	public Resource findResource(ServiceProcess process, ResourceRequirement rr, SystemData systemData)
			throws ProcessManagerException {

		// so lets find the a matching resource using the resource manager
		String uri = generateResourceUri(process);
		return getResourceManager().getResource(uri, new StandardResourceQuery(rr, systemData));
	}

	@Override
	public String replaceRELExpression(ServiceProcess process, String relExpr) throws ProcessManagerException {
		String resourceDescription = getRELParser().parse(relExpr);
		if (resourceDescription == null)
			return relExpr;

		// lets generate the resource query and find a resource
		String uri = generateResourceUri(process);
		ResourceRequirement rr = new ResourceRequirement();
		rr.setTypes(Resource.TYPE_OBJECT);
		rr.setProtocols(null);
		rr.setCapabilityRule(resourceDescription);
		Resource r = getResourceManager().getResource(uri, new StandardResourceQuery(rr, new SystemData()));
		if (r == null)
			return null;
		return ObjectResource.class.cast(r).getObject().toString();
	}

	/**
	 * Registers a given Resource with the specified uri in the internal
	 * ResourceManager.
	 * 
	 * @param uri
	 *            The uri which specifies the context in which teh Resource
	 *            should be registered.
	 * @param resource
	 *            The given Resource.
	 * @return True, if the registration is successfully, false, if an error
	 *         occurs.
	 */
	protected boolean registerResource(String uri, Resource resource) {
		try {
			LOGGER.debug("Registering resource " + resource + " in " + uri);
			return getResourceManager().registerResource(uri, resource);
		} catch (NonAccessibleResourceException e) {
			LOGGER.error("Resource cannot be registered, due to accessibility problems.");
			return false;
		}
	}

	/**
	 * Generates an uri for a given ServiceProcess. This uri has the format
	 * {@literal <domain>.<entity-uri>.<process-id>}
	 * 
	 * @param process
	 *            The ServiceProcess which uri is needed.
	 * @return The generated uri.
	 * @throws InvalidDomainException
	 *             if the identification of the domain specific information has
	 *             failed.
	 */
	public String generateResourceUri(ServiceProcess process) throws InvalidDomainException {
		return generateEntityResourceUri(process) + "." + process.getProcessId();
	}

	/**
	 * Generates an entity uri for a given ServiceProcess. This uri has the
	 * format {@literal <domain>.<entity-uri>}
	 * 
	 * @param process
	 *            The ServiceProcess which entity-uri is needed.
	 * @return The generated uri.
	 * @throws InvalidDomainException
	 *             if the identification of the domain specific information has
	 *             failed.
	 */
	public String generateEntityResourceUri(ServiceProcess process) throws InvalidDomainException {
		String entityResourceUri = generateEntityResourceUri(process.getDomain(), process.getEntityId());
		if (entityResourceUri == null || entityResourceUri.isEmpty())
			return process.getDomain();
		return process.getDomain() + "." + entityResourceUri;
	}

	/**
	 * Generates the domain specific part of the entity-uri.
	 * 
	 * @param entityId
	 *            Id of the entity which entity-uri has to be generated
	 * @return the generated domain-specific part of the entity-uri
	 * @throws InvalidDomainException
	 *             if the identification of the domain specific information has
	 *             failed.
	 */
	protected abstract String generateEntityResourceUri(String domain, Integer entityId) throws InvalidDomainException;

	@Override
	public void updateResources(ServiceProcess process) throws ProcessManagerException {
		String uri = generateResourceUri(process);
		getResourceManager().updateResources(uri, new StringResourceCapabilityRule("extraction_target"));
	}

}
