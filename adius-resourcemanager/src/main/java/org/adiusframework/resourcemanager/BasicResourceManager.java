package org.adiusframework.resourcemanager;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.adiusframework.resource.Converter;
import org.adiusframework.resource.ConverterManager;
import org.adiusframework.resource.FileResource;
import org.adiusframework.resource.FileResourceOperations;
import org.adiusframework.resource.FileSystemResource;
import org.adiusframework.resource.Generator;
import org.adiusframework.resource.GeneratorManager;
import org.adiusframework.resource.ResourceCapabilityRule;
import org.adiusframework.resource.Resource;
import org.adiusframework.resource.ResourceTypeMapper;
import org.adiusframework.resource.Transient;
import org.adiusframework.util.datastructures.SystemData;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * The BasicResourceManager implements the ResourceManager interface and is
 * therefore the central component of the whole ResourceManager.
 */
public class BasicResourceManager implements ResourceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicResourceManager.class);

	private ResourceRepository repository;

	private ConverterManager converterManager;

	private GeneratorManager generatorManager;

	private SystemData systemData;

	private ResourceTypeMapper resourceTypeMapper;

	private List<UUID> temporaryResources;

	/**
	 * Creates a new BasicResourceManager with current SystemData and a empty
	 * List of temporary Resources.
	 */
	public BasicResourceManager() {
		setSystemData(new SystemData());
		setTemporaryResources(new Vector<UUID>());
	}

	/**
	 * Returns the internal list of temporary Resources.
	 * 
	 * @return The list of Resources.
	 */
	protected List<UUID> getTemporaryResources() {
		return temporaryResources;
	}

	/**
	 * Sets a new list of temporary Resources.
	 * 
	 * @param temporaryResources
	 *            The new list.
	 */
	protected void setTemporaryResources(List<UUID> temporaryResources) {
		this.temporaryResources = temporaryResources;
	}

	/**
	 * Return the SystemData object which is used by this instance of the
	 * BasicResourceManager.
	 * 
	 * @return The current SystemData.
	 */
	public SystemData getSystemData() {
		return systemData;
	}

	/**
	 * Sets new SystemData.
	 * 
	 * @param systemData
	 *            The new SystemData.
	 */
	public void setSystemData(SystemData systemData) {
		this.systemData = systemData;
	}

	/**
	 * Sets a new ResourceTypeMapper.
	 * 
	 * @param resourceTypeMapper
	 *            The new ResourceTypeMapper.
	 */
	@Required
	public void setResourceTypeMapper(ResourceTypeMapper resourceTypeMapper) {
		this.resourceTypeMapper = resourceTypeMapper;
	}

	/**
	 * Returns the ResourceTypeMapper which is currently used by the
	 * BasicResourceManager.
	 * 
	 * @return The current ResourceTypeMapper.
	 */
	public ResourceTypeMapper getResourceTypeMapper() {
		return resourceTypeMapper;
	}

	/**
	 * Sets a new ResourceRepository.
	 * 
	 * @param repository
	 *            The new Repository.
	 */
	@Required
	public void setRepository(ResourceRepository repository) {
		this.repository = repository;
	}

	/**
	 * Return the ResourceRepository which is currently used by the
	 * BasicResourceManager.
	 * 
	 * @return The current ResourceRepository.
	 */
	public ResourceRepository getRepository() {
		return repository;
	}

	/**
	 * Sets a new ConverterManager.
	 * 
	 * @param converterManager
	 *            The new ConverterManager.
	 */
	@Required
	public void setConverterManager(ConverterManager converterManager) {
		this.converterManager = converterManager;
	}

	/**
	 * Returns the ConverterManager which is currently used by the
	 * BasciResourceManager.
	 * 
	 * @return The current ConverterManager.
	 */
	protected ConverterManager getConverterManager() {
		return converterManager;
	}

	/**
	 * Returns the GeneratorManager which is currently used by the
	 * BasciResourceManager.
	 * 
	 * @return The current GeneratorManager.
	 */
	public GeneratorManager getGeneratorManager() {
		return generatorManager;
	}

	/**
	 * Sets a new GeneratorManager.
	 * 
	 * @param generatorManager
	 *            The new GeneratorManager.
	 */
	@Required
	public void setGeneratorManager(GeneratorManager generatorManager) {
		this.generatorManager = generatorManager;
	}

	@Override
	public boolean checkConfiguration() {
		if (repository == null || converterManager == null || generatorManager == null || systemData == null
				|| resourceTypeMapper == null || temporaryResources == null)
			return false;

		// TODO add if implemented
		return repository.checkConfiguration()/*
											 * &&
											 * converterManager.checkConfiguration
											 * () &&
											 * generatorManager.checkConfiguration
											 * () &&
											 * resourceTypeMapper.checkConfiguration
											 * ()
											 */;
	}

	@Override
	public boolean existResource(String uri, ResourceCapabilityRule rule) {
		ResourceContainer rc = repository.getContainer(uri, true);
		if (rc == null)
			return false;
		return rc.getResources(rule, true).size() > 0;
	}

	@Override
	public Resource getResource(String uri, ResourceQuery query) {
		// Test if the given parameters are valid
		if (uri == null || query == null || query.getCapabilityRule() == null || query.getQueryDomainData() == null)
			return null;

		ResourceContainer c = repository.getContainer(uri, true);

		// first we identify all resources that has the searched capability,
		// using the execution conditions as boundary conditions for the search
		List<Resource> candidates = null;
		if (c != null)
			candidates = c.getResources(query.getCapabilityRule(), query.getQueryDomainData(), true);
		if (candidates == null) {
			LOGGER.warn("No candidates found satisfying criteria...");
			return null;
		}

		// second we check if the given query is satisfied by one candidate
		for (Resource candidate : candidates) {
			LOGGER.debug("Checking if " + query + " is satisfied by " + candidate);

			// we have to verify that the type and the protocol matches,
			// besides it has to be assured that the resource can be accessed
			if ((query.getTypes() == null || (query.getTypes() != null && query.getTypes()
					.contains(candidate.getType())))
					&& (query.getProtocols() == null || (query.getProtocols().contains(candidate.getProtocol())))
					&& validateResource(candidate, query.getSystemRequirement())) {
				LOGGER.debug("Identified resource satisfying query " + candidate);
				return candidate;
			}
		}

		// if we reach this point, no candidate matches the requirement hence we
		// have to convert a resource
		LOGGER.debug("No resource satisfies query, trying to convert existing one...");
		for (Resource candidate : candidates) {
			try {
				Resource r = convertResource(uri, candidate, query, true);
				if (r != null)
					return r;
			} catch (NonAccessibleResourceException e) {
				LOGGER.error("Unexpected exception " + e.getMessage()
						+ ". Normally, this exception should be impossible.");
			}
		}
		return null;
	}

	@Override
	public boolean registerResource(String uri, Resource resource) throws NonAccessibleResourceException {
		if (!validateResource(resource)) {
			LOGGER.error("Registered resource cannot be accessed by resource manager.");
			throw new NonAccessibleResourceException("Resource " + resource + " access violation.");
		}
		ResourceContainer c = repository.setContainer(uri);
		return c.addResource(resource);
	}

	@Override
	public boolean validateResource(Resource resource) {
		return validateResource(resource, getSystemData());
	}

	@Override
	public boolean validateResource(Resource resource, SystemData systemData) {
		if (resource.getClass().isAnnotationPresent(Transient.class))
			return systemData.validate(resource.getSystemData());
		else
			return true;
	}

	@Override
	public Resource convertResource(String uri, Resource from, ResourceQuery query, boolean temporary)
			throws NonAccessibleResourceException {

		// first we check if a conversion is required and can be processed
		if (!getSystemData().validate(from.getSystemData()))
			throw new NonAccessibleResourceException(
					"Resource is transient and cannot be accessed due to system boundaries.");

		// lets check if there are requirements that cannot be fulfilled
		// we have to identify resource types that are supported by the current
		// query
		List<Class<? extends Resource>> toClassesCandidates = new Vector<Class<? extends Resource>>();
		for (String type : query.getTypes()) {
			for (String protocol : query.getProtocols()) {
				Class<? extends Resource> toClass = getResourceTypeMapper().getClass(type, protocol);
				if (toClass != null)
					toClassesCandidates.add(toClass);
			}
		}

		if (toClassesCandidates.size() == 0) {
			LOGGER.error("Invalid mapping of type and protocol.");
			throw new NonAccessibleResourceException("No class representing " + query.getTypes() + " and "
					+ query.getProtocols() + " found.");
		}

		// now we have to identify all resource classes that can be used
		// regarding transient configuration
		List<Class<? extends Resource>> toClasses = null;
		boolean isTransientPossible = getSystemData().validate(query.getSystemRequirement());
		if (isTransientPossible)
			toClasses = toClassesCandidates;
		else {
			toClasses = new Vector<Class<? extends Resource>>();
			for (Class<? extends Resource> toClass : toClassesCandidates) {
				if (!toClass.isAnnotationPresent(Transient.class))
					toClasses.add(toClass);
			}
		}
		if (toClasses.size() == 0) {
			LOGGER.error("Incompatible systems found for transient resource.");
			throw new NonAccessibleResourceException(
					"Cannot convert into a transient resource, if systems are unequal.");
		}

		// next we are going to find a matching converter
		LOGGER.debug("Looking for converters " + from.getClass() + " to one of " + toClasses
				+ " with transient setting " + isTransientPossible);
		for (Class<? extends Resource> toClass : toClasses) {
			List<Converter<? extends Resource, ? extends Resource>> clist = getConverterManager().getConverters(
					from.getClass(), toClass, !isTransientPossible);
			if (clist.size() == 0)
				continue;

			// lets find a suitable generator
			LOGGER.debug("Searching for generator for one of " + toClasses);
			Generator<?> generator = getGeneratorManager().get(toClass);
			if (generator == null) {
				LOGGER.debug("No suitable generator found for conversion into " + toClass);
				continue;
			}
			Resource to = generator.generate(from);

			// at least one converter has been found and the target resource has
			// been generated, now we have to get the
			// converter running
			for (Converter<?, ?> c : clist) {
				Resource result = c.convert(from, to);
				if (result != null) {
					if (registerResource(uri, result)) {
						if (temporary) {
							LOGGER.debug("Registering conversion result as temporary resource: " + result.getUUID());
							getTemporaryResources().add(result.getUUID());
						}
						return result;
					}
					LOGGER.debug("Registration of created resource failed...");
					removeResource(to);
					continue;
				}
			}
		}

		// if we reach this point the conversion failed
		LOGGER.error("Conversion of resource from " + from.getClass() + " to one of " + toClasses + " failed");
		return null;
	}

	@Override
	public String toString() {
		return repository.toString();
	}

	/**
	 * Removes a given Resource from the list of temporary Resources.
	 * 
	 * @param resource
	 *            The Resource to be removed.
	 */
	protected void removeResource(Resource resource) {
		if (getTemporaryResources().contains(resource.getUUID())) {
			LOGGER.debug("Removing temporary resource: " + resource.getUUID() + "|" + resource);
			if (FileSystemResource.class.isInstance(resource))
				FileResourceOperations.remove(FileSystemResource.class.cast(resource));
			getTemporaryResources().remove(resource.getUUID());
		}
	}

	@Override
	public void unregisterResources(String uri) {
		LOGGER.debug("Unregistering resources of uri " + uri);
		List<Resource> removedResources = repository.removeContainers(uri, true);
		LOGGER.debug(removedResources.size() + " resources have been unregistered, checking temporary status...");
		for (Resource removedResource : removedResources) {
			removeResource(removedResource);
		}
	}

	@Override
	public void updateResources(String uri, ResourceCapabilityRule rule) {
		ResourceContainer c = repository.getContainer(uri, true);

		// identifying the resources, if a container exists, otherwise no
		// resources can satisfy the rule
		LOGGER.debug("Updating resources of uri " + uri);
		List<Resource> resources = null;
		if (c != null)
			resources = c.getResources(rule, true);

		if (resources == null || resources.size() < 2)
			return;

		// now we have to find the base resource, thereby we only check file
		// resources
		FileResource baseResource = null;
		long lastChange = Long.MAX_VALUE;
		List<FileResource> fileResources = new Vector<FileResource>();
		for (Resource resource : resources) {
			if (FileResource.class.isInstance(resource)) {
				FileResource fileResource = FileResource.class.cast(resource);
				fileResources.add(fileResource);
				try {
					FileObject fileObject = FileResourceOperations.resolve(fileResource);
					long change = fileObject.getContent().getLastModifiedTime();
					if (fileObject.exists() && change < lastChange) {
						baseResource = fileResource;
						lastChange = change;
					}
				} catch (FileSystemException e) {
					LOGGER.warn("Resource " + fileResource + " could not be updated: " + e.getMessage());
				}
			}
		}
		if (baseResource == null) {
			LOGGER.warn("Identification of base resource failed...");
			return;
		}
		LOGGER.debug("Base resource " + baseResource + " found.");

		// after we have identified the base resource, we have to update the
		// other resources
		for (FileResource fileResource : fileResources) {
			if (fileResource.equals(baseResource))
				continue;

			// so lets update the resource
			List<Converter<? extends Resource, ? extends Resource>> converters = getConverterManager().getConverters(
					baseResource.getClass(), fileResource.getClass(), false);
			if (converters.size() > 0) {
				LOGGER.debug("Updating " + fileResource);
				converters.get(0).convert(baseResource, fileResource);
			} else
				LOGGER.error("Resource " + fileResource + " could not be updated: No matching converter found.");
		}
	}

}
