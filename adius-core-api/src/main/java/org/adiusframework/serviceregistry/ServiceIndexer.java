package org.adiusframework.serviceregistry;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

import org.adiusframework.service.ServiceCapabilityRule;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.util.IsConfigured;

/**
 * The ServiceIndexer manages the ServiceRegistrations and stores the
 * information how often the every service is running.
 */
public interface ServiceIndexer extends IsConfigured {

	/**
	 * Return all ServiceRegistrations which are managed by the ServiceIndexer
	 * mapped by it's UUIDs.
	 * 
	 * @return A Map with all registrations.
	 */
	public ConcurrentMap<UUID, ServiceRegistration> getAll();

	/**
	 * Returns a ServiceRegistration based on a given UUID.
	 * 
	 * @param uid
	 *            The given UUID.
	 * @return The registration.
	 */
	public ServiceRegistration get(UUID uid);

	/**
	 * Determines all ServiceRegistration which fulfill a given
	 * ServiceCapabilityRule and return their UUIDs.
	 * 
	 * @param rule
	 *            The ServiceCapabilityRule which should be fulfilled.
	 * @return A List with the matching UUIDs.
	 */
	public List<UUID> find(ServiceCapabilityRule rule);

	/**
	 * Should be called when a Service is started.
	 * 
	 * @param uid
	 *            The UUID of the started service.
	 */
	public void started(UUID uid);

	/**
	 * Should be called when a Service is stopped.
	 * 
	 * @param uid
	 *            The UUID of the stopped service.
	 */
	public void stopped(UUID uid);

	/**
	 * Return the number how often a service is running.
	 * 
	 * @param uid
	 *            The UUID which identifies the requested service.
	 * @return The number of instances.
	 */
	public Integer count(UUID uid);

	/**
	 * Adds a ServiceRegistration to the indexer and manages it with a generated
	 * UUID.
	 * 
	 * @param definition
	 *            The registration to be added.
	 * @return The generated UUID.
	 */
	public UUID add(ServiceRegistration definition);

	/**
	 * Removes a registration from the indexer.
	 * 
	 * @param uid
	 *            The UUID of the service.
	 */
	public void remove(UUID uid);

	/**
	 * Removes all services from the indexer. The indexer is cleared.
	 */
	public void removeAll();

}
