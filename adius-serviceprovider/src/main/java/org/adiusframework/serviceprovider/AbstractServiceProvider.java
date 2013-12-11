package org.adiusframework.serviceprovider;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.adiusframework.service.xml.Activator;
import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceactivator.ServiceActivator;
import org.adiusframework.serviceactivator.SpringIntegrationServiceActivator;
import org.adiusframework.serviceactivator.SpringServiceInstanceFactory;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.adiusframework.serviceregistry.ServiceManager;
import org.adiusframework.util.datastructures.SystemData;
import org.adiusframework.util.exception.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

public abstract class AbstractServiceProvider implements ServiceProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceProvider.class);

	private ServiceManager serviceManager;

	private ServiceProviderDefinition definition;

	private ServiceRoutingSlip routingSlip;

	private List<UUID> uidList;

	public AbstractServiceProvider() {
		setUidList(new Vector<UUID>());
	}

	protected List<UUID> getUidList() {
		return uidList;
	}

	protected void setUidList(List<UUID> uidList) {
		this.uidList = uidList;
	}

	@Required
	public ServiceProviderDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ServiceProviderDefinition definition) {
		this.definition = definition;
	}

	@Required
	public ServiceRoutingSlip getRoutingSlip() {
		return routingSlip;
	}

	public void setRoutingSlip(ServiceRoutingSlip routingSlip) {
		this.routingSlip = routingSlip;
	}

	@Required
	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	public void setServiceManager(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	protected void registerWithRouter(ServiceDefinition serviceDefinition) {

		// create the service activator
		ServiceActivator activator = null;
		if (serviceDefinition.getBinding().getConfiguration().getActivator().equals(Activator.SPRING)) {
			SpringServiceInstanceFactory factory = new SpringServiceInstanceFactory();
			factory.setConfiguration(serviceDefinition.getBinding().getConfiguration().getParameter());
			activator = new SpringIntegrationServiceActivator();
			((SpringIntegrationServiceActivator) activator).setServiceInstanceFactory(factory);
		} else {
			LOGGER.error("Unexpected activator configuration: "
					+ serviceDefinition.getBinding().getConfiguration().getActivator());
			throw new UnexpectedException("Unexpected activator configuration: "
					+ serviceDefinition.getBinding().getConfiguration().getActivator());
		}

		// register it
		LOGGER.debug("Registering " + serviceDefinition.getBinding().getRoute() + " with routing slip");
		getRoutingSlip().registerRoute(serviceDefinition.getBinding().getRoute(), activator);
	}

	protected void registerWithManager(ServiceDefinition serviceDefinition) {
		if (getDefinition() == null) {
			LOGGER.error("Registration of service impossible, missing service provider definition");
			return;
		} else if (serviceDefinition == null) {
			LOGGER.error("Registration of null cannot be processed");
			return;
		}
		LOGGER.debug("Registration of service triggered");
		UUID uid = getServiceManager().register(getDefinition(), serviceDefinition, new SystemData());
		getUidList().add(uid);
		LOGGER.debug("Service " + serviceDefinition.getCapability().getCategory() + " successfully registered as "
				+ uid);
	}

	public void registerAll(List<ServiceDefinition> definitions) {
		for (ServiceDefinition definition : definitions) {
			if (definition != null) {
				registerWithRouter(definition);
				registerWithManager(definition);
			}
		}
	}

	public void unregisterAll() {
		LOGGER.debug("Unregistering all services");
		for (UUID uid : getUidList()) {
			getServiceManager().unregister(uid);
			LOGGER.debug("Service " + uid + " successfully unregistered");
		}
	}

}
