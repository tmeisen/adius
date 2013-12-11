package org.adiusframework.processmanager;

import org.adiusframework.processmanager.ServiceProcessDefinition;
import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;

public interface DomainSpecificQueryHandler {

	public boolean isUnprocessed(ServiceProcess serviceProcess);

	public boolean isError(ServiceProcess serviceProcess);

	public int identifyDomainEntityId(Query query, ServiceProcessDefinition processDefinition)
			throws ProcessManagerException;

	public void handleError(int entityId);

}
