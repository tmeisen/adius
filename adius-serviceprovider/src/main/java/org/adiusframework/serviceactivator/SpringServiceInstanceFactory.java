package org.adiusframework.serviceactivator;

public class SpringServiceInstanceFactory implements ServiceInstanceFactory {

	private String[] configuration;

	private String serviceInstanceName;

	@Override
	public GenericServiceInstance create() {
		if (getServiceInstanceName() != null)
			return new SpringServiceInstance(getServiceInstanceName(), getConfiguration());
		return new SpringServiceInstance(getConfiguration());
	}

	public String getServiceInstanceName() {
		return serviceInstanceName;
	}

	public void setServiceInstanceName(String serviceInstanceName) {
		this.serviceInstanceName = serviceInstanceName;
	}

	private String[] getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String... configuration) {
		this.configuration = configuration;
	}

}
