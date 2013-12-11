package org.adiusframework.serviceregistry;

import java.util.UUID;

import org.adiusframework.service.BasicServiceRegistration;
import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.adiusframework.util.datastructures.SystemData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BasicServiceManager implements the ServiceManager interface by accessing
 * a ServiceDefinitionParser and a ServiceIndexer.
 */
public class BasicServiceManager implements ServiceManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicServiceManager.class);

	/**
	 * This object is used to store the information about registered services.
	 */
	private ServiceIndexer indexer;

	/**
	 * Returns the ServiceIndexer which is currently used by the
	 * BasicServiceManager.
	 * 
	 * @return The current ServiceIndexer.
	 */
	public ServiceIndexer getIndexer() {
		return indexer;
	}

	/**
	 * Sets a new ServiceIndexer.
	 * 
	 * @param indexer
	 *            The new ServiceIndexer.
	 */
	public void setIndexer(ServiceIndexer indexer) {
		this.indexer = indexer;
	}

	@Override
	public boolean checkConfiguration() {
		if (indexer == null)
			return false;
		return indexer.checkConfiguration();
	}

	@Override
	public UUID register(ServiceProviderDefinition providerDefinition, ServiceDefinition serviceDefinition,
			SystemData systemData) {
		UUID uid = getIndexer().add(new BasicServiceRegistration(providerDefinition, serviceDefinition, systemData));
		LOGGER.debug("Returning registration data: " + uid);
		LOGGER.debug(serviceDefinition.toString());
		return uid;
	}

	@Override
	public void unregister(UUID uid) {
		getIndexer().remove(uid);
	}

	@Override
	public void unregisterAll() {
		getIndexer().removeAll();
	}

	@Override
	public void started(UUID uid) {
		getIndexer().started(uid);
	}

	@Override
	public void stopped(UUID uid) {
		getIndexer().stopped(uid);
	}

}
