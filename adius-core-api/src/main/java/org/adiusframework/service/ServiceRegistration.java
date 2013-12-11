package org.adiusframework.service;

import java.io.Serializable;

import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.adiusframework.util.datastructures.SystemData;

public interface ServiceRegistration extends Serializable {

	public ServiceProviderDefinition getProviderDefinition();

	public ServiceDefinition getServiceDefinition();

	public SystemData getSystemData();

}
