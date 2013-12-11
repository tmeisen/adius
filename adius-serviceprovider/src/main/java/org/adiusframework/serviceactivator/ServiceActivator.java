package org.adiusframework.serviceactivator;

import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceResult;

public interface ServiceActivator {

	public ServiceResult activate(ServiceInput input);

}
