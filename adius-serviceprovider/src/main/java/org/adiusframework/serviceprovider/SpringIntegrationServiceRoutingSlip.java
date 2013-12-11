package org.adiusframework.serviceprovider;

import java.util.HashMap;
import java.util.Map;

import org.adiusframework.service.ErrorServiceResult;
import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.serviceactivator.ServiceActivator;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.Payload;

/**
 * Basic implementation of a {@link ServiceRoutingSlip} for spring integration.
 * 
 * @author tm807416
 * 
 */
public class SpringIntegrationServiceRoutingSlip implements ServiceRoutingSlip {

	/**
	 * Map used to store the mapping between route and activator
	 */
	private Map<String, ServiceActivator> activators;

	/**
	 * Default constructor
	 */
	public SpringIntegrationServiceRoutingSlip() {
		activators = new HashMap<String, ServiceActivator>();
	}

	@Override
	public void registerRoute(String route, ServiceActivator activator) {
		activators.put(route, activator);
	}

	@Override
	@org.springframework.integration.annotation.ServiceActivator
	public ServiceResult route(@Header(required = true, value = "route") String route, @Payload ServiceInput input) {
		ServiceActivator activator = activators.get(route);
		if (activator == null) {
			return new ErrorServiceResult("Activator for route " + route + " not registered");
		}
		return activator.activate(input);
	}

}
