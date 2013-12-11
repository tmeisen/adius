package org.adiusframework.serviceregistry;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.adiusframework.service.ServiceCapabilityRule;
import org.adiusframework.service.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BasicServiceIndexer implements the ServiceIndexer interface and manages
 * the ServiceRegistrations by implementing two maps for the registrations
 * itself and for the related number fo instances.
 */
public class BasicServiceIndexer implements ServiceIndexer {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicServiceIndexer.class);

	private ConcurrentMap<UUID, ServiceRegistration> listing;

	private ConcurrentMap<UUID, Integer> counter;

	/**
	 * Creates a new BasicServiceIndexer with default properties.
	 */
	public BasicServiceIndexer() {
		listing = new ConcurrentHashMap<UUID, ServiceRegistration>();
		counter = new ConcurrentHashMap<UUID, Integer>();
	}

	@Override
	public ServiceRegistration get(UUID uid) {
		return listing.get(uid);
	}

	@Override
	public synchronized boolean checkConfiguration() {
		if (listing == null || counter == null)
			return false;

		return listing.keySet().equals(counter.keySet());
	}

	@Override
	public List<UUID> find(ServiceCapabilityRule rule) {
		LOGGER.debug("Searching for service with rule " + rule);
		List<UUID> result = new Vector<UUID>();
		for (Entry<UUID, ServiceRegistration> e : listing.entrySet()) {
			if (rule.satisfiedBy(e.getValue().getServiceDefinition().getCapability()))
				result.add(e.getKey());
		}
		LOGGER.debug("Service found " + result);
		return result;
	}

	@Override
	public UUID add(ServiceRegistration registration) {
		UUID uid = UUID.randomUUID();
		listing.put(uid, registration);
		counter.put(uid, 0);
		LOGGER.debug("Service " + registration + " added, stored using uid " + uid);
		return uid;
	}

	@Override
	public synchronized void remove(UUID uid) {
		listing.remove(uid);
		counter.remove(uid);
		LOGGER.debug("Service with uid " + uid + " has been removed");
	}

	@Override
	public void removeAll() {
		listing.clear();
		counter.clear();
	}

	@Override
	public void started(UUID uid) {
		Integer count = counter.get(uid);
		if (count != null)
			counter.put(uid, count + 1);
	}

	@Override
	public void stopped(UUID uid) {
		Integer count = counter.get(uid);
		if (count != null && count > 0)
			counter.put(uid, count - 1);
	}

	@Override
	public Integer count(UUID uid) {
		return counter.get(uid);
	}

	@Override
	public ConcurrentMap<UUID, ServiceRegistration> getAll() {
		return listing;
	}

}
