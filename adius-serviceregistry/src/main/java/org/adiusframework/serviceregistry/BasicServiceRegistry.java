package org.adiusframework.serviceregistry;

import java.util.UUID;

import org.adiusframework.service.ServiceCapabilityRule;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.adiusframework.util.datastructures.SystemData;

/**
 * The BasicServiceRegistry implements two objects and redirects method-calls to
 * the respectively one in order to fulfill the means of the combined
 * ServiceRegistry interface.
 */
public class BasicServiceRegistry implements ServiceRegistry {

	private ServiceFinder finder;

	private ServiceManager manager;

	/**
	 * Return the ServiceManager which is currently used by the
	 * BasicServiceRegistry.
	 * 
	 * @return The current ServiceManager.
	 */
	public ServiceManager getManager() {
		return manager;
	}

	/**
	 * Sets a new ServiceManager.
	 * 
	 * @param manager
	 *            The new ServiceManager.
	 */
	public void setManager(ServiceManager manager) {
		this.manager = manager;
	}

	/**
	 * Return the ServiceFinder which is currently used by the
	 * BasicServiceRegistry.
	 * 
	 * @return The current ServiceFinder.
	 */
	public ServiceFinder getFinder() {
		return finder;
	}

	/**
	 * Sets a new ServiceFinder.
	 * 
	 * @param finder
	 *            The new ServiceFinder.
	 */
	public void setFinder(ServiceFinder finder) {
		this.finder = finder;
	}

	@Override
	public boolean checkConfiguration() {
		if (finder == null || manager == null)
			return false;

		return finder.checkConfiguration() || manager.checkConfiguration();
	}

	@Override
	public UUID register(ServiceProviderDefinition providerDefinition, ServiceDefinition serviceDefinition,
			SystemData systemData) {
		return getManager().register(providerDefinition, serviceDefinition, systemData);
	}

	@Override
	public void unregister(UUID uid) {
		getManager().unregister(uid);
	}

	@Override
	public void unregisterAll() {
		getManager().unregisterAll();
	}

	@Override
	public void started(UUID uid) {
		getManager().started(uid);
	}

	@Override
	public void stopped(UUID uid) {
		getManager().stopped(uid);
	}

	@Override
	public ServiceRegistration find(ServiceCapabilityRule capability) {
		return getFinder().find(capability);
	}

}
