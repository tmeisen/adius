package org.adiusframework.service;

import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.adiusframework.util.datastructures.SystemData;

public class BasicServiceRegistration implements ServiceRegistration {

	private static final long serialVersionUID = 4133853590006795504L;

	private ServiceProviderDefinition providerDefinition;

	private ServiceDefinition serviceDefinition;

	private SystemData systemData;

	public BasicServiceRegistration() {
	}

	public BasicServiceRegistration(ServiceProviderDefinition providerDefinition, ServiceDefinition serviceDefinition,
			SystemData systemData) {
		setProviderDefinition(providerDefinition);
		setServiceDefinition(serviceDefinition);
		setSystemData(systemData);
	}

	@Override
	public ServiceProviderDefinition getProviderDefinition() {
		return this.providerDefinition;
	}

	public void setProviderDefinition(ServiceProviderDefinition providerDefinition) {
		this.providerDefinition = providerDefinition;
	}

	@Override
	public ServiceDefinition getServiceDefinition() {
		return this.serviceDefinition;
	}

	public void setServiceDefinition(ServiceDefinition serviceDefinition) {
		this.serviceDefinition = serviceDefinition;
	}

	@Override
	public SystemData getSystemData() {
		return systemData;
	}

	public void setSystemData(SystemData systemData) {
		this.systemData = systemData;
	}

}
