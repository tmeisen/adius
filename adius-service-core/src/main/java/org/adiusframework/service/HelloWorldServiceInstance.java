package org.adiusframework.service;

public class HelloWorldServiceInstance implements ServiceInstance {

	@Override
	public ServiceResult execute() throws ServiceException {
		System.out.println("Hello World this service instance works...");
		return new StandardServiceResult();
	}

}
