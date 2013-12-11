package org.adiusframework.serviceregistry;

import org.adiusframework.service.ServiceCapabilityRule;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.util.IsConfigured;

/**
 * The ServiceFinder interface provides a method which should evaluate the
 * best-fitting ServiceRegistration based on a given ServiceCapabilityRule.
 */
public interface ServiceFinder extends IsConfigured {

	/**
	 * Finds a service that fulfills the capability constraints defined by the
	 * denoted parameter. The method tries to find the best matching service
	 * that is available. If no service is available at the moment but busy such
	 * a service is returned. If no service exists null is returned.
	 * 
	 * @param rule
	 *            the capability rule that have to be fulfilled by the service
	 * @return the service definition of the found service, if no service could
	 *         be found null is returned
	 */
	public ServiceRegistration find(ServiceCapabilityRule rule);

}
