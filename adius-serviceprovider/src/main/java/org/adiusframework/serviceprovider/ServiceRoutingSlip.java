package org.adiusframework.serviceprovider;

import org.adiusframework.service.ErrorServiceResult;
import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceInstance;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.serviceactivator.ServiceActivator;

/**
 * A service routing slip used to determine the corresponding
 * {@link ServiceActivator} at runtime.
 * 
 * @author tm807416
 * 
 */
public interface ServiceRoutingSlip {

	/**
	 * Activates the underlying service activator bound to the given route. If
	 * no activator has been bound to the route, an {@link ErrorServiceResult}
	 * is returned.
	 * 
	 * @param route
	 *            the name of the route
	 * @param input
	 *            the input passed to the created {@link ServiceInstance}
	 * @return the {@link ServiceResult} if the service could be executed or an
	 *         {@link ErrorServiceResult} if no service activator has been bound
	 *         to the route
	 */
	public ServiceResult route(String route, ServiceInput input);

	/**
	 * Registers a new route to the routing slip.
	 * 
	 * @param route
	 *            the name of the route
	 * @param activator
	 *            the activator triggered when a message of this route occurs
	 */
	void registerRoute(String route, ServiceActivator activator);

}
