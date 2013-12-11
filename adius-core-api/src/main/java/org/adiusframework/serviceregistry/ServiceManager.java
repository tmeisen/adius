package org.adiusframework.serviceregistry;

import java.util.UUID;

import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.adiusframework.util.IsConfigured;
import org.adiusframework.util.datastructures.SystemData;

/**
 * The ServiceManager interface is the first component which processes a request
 * for the ServiceRegistry. It defines methods to register/unregister services
 * and for feedback when a service is started or stopped. This is used to
 * increase the performance, because the ServiceRegistry can favor services
 * which are running less than others.
 */
public interface ServiceManager extends IsConfigured {

	/**
	 * Unregisters a service which is identified by its UUID.
	 * 
	 * @param uid
	 *            The UUID of the service to be unregistered.
	 */
	public void unregister(UUID uid);

	/**
	 * Unregisters all services.
	 */
	public void unregisterAll();

	/**
	 * Should be invoke to indicate that a service was started.
	 * 
	 * @param uid
	 *            The UUID of the service which was started.
	 */
	public void started(UUID uid);

	/**
	 * Should be invoked to indicate that a service was stopped.
	 * 
	 * @param uid
	 *            The UUID of the service which was stopped.
	 */
	public void stopped(UUID uid);

	/**
	 * Registers a new service with specific parameters.
	 * 
	 * @param providerDefinition
	 *            The ServiceProviderDefinition which is related to the service.
	 * @param serviceDefinition
	 *            The ServiceDefinition which is related to the service.
	 * @param systemData
	 *            The SystemData which is related to the service.
	 * @return The UUId which identifies the new service by now.
	 */
	public UUID register(ServiceProviderDefinition providerDefinition, ServiceDefinition serviceDefinition,
			SystemData systemData);

}
