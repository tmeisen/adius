package org.adiusframework.serviceactivator;

import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceResult;

public interface GenericServiceInstance {

	public ServiceResult execute(ServiceInput input) throws ServiceException;

}
