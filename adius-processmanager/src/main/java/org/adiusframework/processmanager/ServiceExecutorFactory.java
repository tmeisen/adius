package org.adiusframework.processmanager;

import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.util.IsConfigured;

/**
 * The ServiceExecutorFactory manages the ServiceExecutor objects, which are
 * needed when a new service-process should be started.
 */
public interface ServiceExecutorFactory extends IsConfigured {

	/**
	 * Starts the execution of a new service-process.
	 * 
	 * @param correlationId
	 *            The String which identifies the related ServiceProcess.
	 * @param sr
	 *            The object which identifier the service-process which should
	 *            be executed.
	 * @param input
	 *            The parameters for the service-process.
	 */
	public abstract void execute(String correlationId, ServiceRegistration sr, ServiceInput input);

}