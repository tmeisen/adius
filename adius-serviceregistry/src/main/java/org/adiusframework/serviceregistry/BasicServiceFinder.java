package org.adiusframework.serviceregistry;

import java.util.List;
import java.util.UUID;

import org.adiusframework.service.ServiceCapabilityRule;
import org.adiusframework.service.ServiceRegistration;

/**
 * The BasicServiceFinder implements the given method of the ServiceFinder
 * interface by accessing a ServiceIndexer object.
 */
public class BasicServiceFinder implements ServiceFinder {

	private ServiceIndexer indexer;

	/**
	 * Return the ServiceIndex which is currently used by the
	 * BasicServiceFinder.
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

	/**
	 * Find a service that fulfills the capability rule and is currently
	 * available and the one with the fewest jobs.
	 * 
	 * @rule Rule that have to be fulfilled.
	 * @return The service registration that fulfills the rule and is currently
	 *         available.
	 */
	@Override
	public ServiceRegistration find(ServiceCapabilityRule rule) {
		List<UUID> uids = getIndexer().find(rule);
		ServiceRegistration result = null;
		int min = Integer.MAX_VALUE;
		for (UUID uid : uids) {
			if (result == null) {
				result = getIndexer().get(uid);
				min = getIndexer().count(uid);
			} else {
				int count = getIndexer().count(uid);
				if (count < min) {
					min = count;
					result = getIndexer().get(uid);
				}
			}
		}
		return result;
	}

}
