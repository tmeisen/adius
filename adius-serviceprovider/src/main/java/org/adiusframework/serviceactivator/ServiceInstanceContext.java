package org.adiusframework.serviceactivator;

import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceResult;

public interface ServiceInstanceContext {

	public abstract ServiceResult run(ServiceInput input) throws ServiceException;

}