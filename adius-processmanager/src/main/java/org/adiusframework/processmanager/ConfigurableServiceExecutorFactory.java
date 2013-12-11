package org.adiusframework.processmanager;

import java.util.HashMap;
import java.util.Map;

import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.serviceprovider.xml.Protocol;

/**
 * The ConfigurableServiceExecutorFactory implements a mapping, where a every
 * Protocol is mapped to a ServiceExecutor.
 */
public class ConfigurableServiceExecutorFactory implements ServiceExecutorFactory {

	private Map<Protocol, ServiceExecutor> protocolMapping;

	/**
	 * Creates a new ConfigurableServiceExecutorFactory with no mapping.
	 */
	public ConfigurableServiceExecutorFactory() {
		protocolMapping = new HashMap<Protocol, ServiceExecutor>();
	}

	/**
	 * Return the Map which is currently used to map Protocols to
	 * ServiceExecutors.
	 * 
	 * @return The current Map.
	 */
	public Map<Protocol, ServiceExecutor> getProtocolMapping() {
		return protocolMapping;
	}

	/**
	 * Sets a new Map.
	 * 
	 * @param protocolMapping
	 *            The new Map.
	 */
	public void setProtocolMapping(Map<Protocol, ServiceExecutor> protocolMapping) {
		this.protocolMapping = protocolMapping;
	}

	@Override
	public boolean checkConfiguration() {
		return protocolMapping != null;
	}

	@Override
	public void execute(String correlationId, ServiceRegistration sr, ServiceInput input) {

		// first we have to validate if a service executor is registered
		Protocol protocol = sr.getProviderDefinition().getBinding().getProtocol();
		if (!getProtocolMapping().containsKey(protocol))
			throw new UnsupportedOperationException("No service executor class registered for protocol " + protocol);

		// now we can use the executor to process
		ServiceExecutor executor = getProtocolMapping().get(protocol);
		executor.execute(correlationId, sr, input);
	}

}
