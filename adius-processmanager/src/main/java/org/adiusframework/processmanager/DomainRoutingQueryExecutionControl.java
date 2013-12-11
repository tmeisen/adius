package org.adiusframework.processmanager;

import java.util.concurrent.ConcurrentMap;

import org.adiusframework.processmanager.AbstractQueueQueryExecutionControl;
import org.adiusframework.processmanager.ServiceProcessDefinition;
import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.InvalidDomainException;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;

public class DomainRoutingQueryExecutionControl extends AbstractQueueQueryExecutionControl {

	private ConcurrentMap<String, DomainSpecificQueryHandler> mapping;

	public ConcurrentMap<String, DomainSpecificQueryHandler> getMapping() {
		return mapping;
	}

	public void setMapping(ConcurrentMap<String, DomainSpecificQueryHandler> mapping) {
		this.mapping = mapping;
	}

	@Override
	protected boolean isUnprocessed(ServiceProcess serviceProcess) throws InvalidDomainException {
		if (!getMapping().containsKey(serviceProcess.getDomain()))
			throw new InvalidDomainException(serviceProcess.getDomain());
		return getMapping().get(serviceProcess.getDomain()).isUnprocessed(serviceProcess);
	}

	@Override
	protected boolean isError(ServiceProcess serviceProcess) throws InvalidDomainException {
		if (!getMapping().containsKey(serviceProcess.getDomain()))
			throw new InvalidDomainException(serviceProcess.getDomain());
		return getMapping().get(serviceProcess.getDomain()).isError(serviceProcess);
	}

	@Override
	protected int identifyDomainEntityId(ServiceProcessDefinition processDefinition, Query query)
			throws ProcessManagerException {
		if (!getMapping().containsKey(query.getDomain()))
			throw new InvalidDomainException(query.getDomain());
		return getMapping().get(query.getDomain()).identifyDomainEntityId(query, processDefinition);
	}

	@Override
	protected void handleError(Query query, int entityId) throws ProcessManagerException {
		if (!getMapping().containsKey(query.getDomain()))
			throw new InvalidDomainException(query.getDomain());
		getMapping().get(query.getDomain()).handleError(entityId);
	}

}
