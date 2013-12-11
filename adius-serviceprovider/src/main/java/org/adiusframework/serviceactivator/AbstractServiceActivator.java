package org.adiusframework.serviceactivator;

import org.adiusframework.service.ErrorServiceResult;
import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServiceActivator implements ServiceActivator {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceActivator.class);

	private ServiceInstanceFactory serviceInstanceFactory;

	public ServiceInstanceFactory getServiceInstanceFactory() {
		return this.serviceInstanceFactory;
	}

	public void setServiceInstanceFactory(ServiceInstanceFactory serviceInstanceFactory) {
		this.serviceInstanceFactory = serviceInstanceFactory;
	}

	@Override
	public ServiceResult activate(ServiceInput input) {
		LOGGER.debug("Creating a new service instance");
		GenericServiceInstance instance = getServiceInstanceFactory().create();
		try {
			LOGGER.debug("Executing the newly created service instance");
			ServiceResult result = instance.execute(input);
			LOGGER.debug("Execution of service finished returning " + result);
			return result;
		} catch (ServiceException e) {
			LOGGER.error("An service exception occured during the execution of the service instance: " + e.getMessage());
			return new ErrorServiceResult(e.getMessage());
		} catch (Exception e) {
			LOGGER.error("An unexpected not handled exception occured during the execution of the service instance. "
					+ e.getMessage());
			e.printStackTrace();
			return new ErrorServiceResult(e.getMessage());
		}
	}

}
