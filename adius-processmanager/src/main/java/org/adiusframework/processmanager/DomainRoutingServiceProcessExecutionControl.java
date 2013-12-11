package org.adiusframework.processmanager;

import java.util.concurrent.ConcurrentMap;

import org.adiusframework.processmanager.AbstractServiceProcessExecutionControl;
import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.InvalidDomainException;

public class DomainRoutingServiceProcessExecutionControl extends AbstractServiceProcessExecutionControl {

	private ConcurrentMap<String, DomainSpecificServiceProcessHandler> mapping;

	public ConcurrentMap<String, DomainSpecificServiceProcessHandler> getMapping() {
		return mapping;
	}

	public void setMapping(ConcurrentMap<String, DomainSpecificServiceProcessHandler> mapping) {
		this.mapping = mapping;
	}

	@Override
	public void handleErrorResult(ServiceProcess serviceProcess) throws InvalidDomainException {
		if (!getMapping().containsKey(serviceProcess.getDomain()))
			throw new InvalidDomainException(serviceProcess.getDomain());
		getMapping().get(serviceProcess.getDomain()).handleError(serviceProcess.getEntityId());
	}

	@Override
	public void handleServiceProcessResult(ServiceProcess serviceProcess) throws InvalidDomainException {
		if (!getMapping().containsKey(serviceProcess.getDomain()))
			throw new InvalidDomainException(serviceProcess.getDomain());
		getMapping().get(serviceProcess.getDomain()).handleServiceProcessResult(serviceProcess.getEntityId());
	}

}
