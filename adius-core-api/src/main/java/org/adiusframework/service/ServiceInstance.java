package org.adiusframework.service;

/**
 * Interface defining a service.
 * 
 * @author tmeisen
 * 
 */
public interface ServiceInstance {

	/**
	 * Executes the service.
	 * 
	 * @throws ServiceException
	 *             if an error occurs during execution.
	 * @return ServiceResult Result of the service
	 */
	public abstract ServiceResult execute() throws ServiceException;
}
