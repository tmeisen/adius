package org.adiusframework.processmanager;

import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceRegistration;

/**
 * The ServiceExecutor is the gateway to service-processes, which are outside of
 * the ProcessManager.
 */
public interface ServiceExecutor {

	/**
	 * Executes a service-process which in described by a ServiceRegistration
	 * with the given parameters.
	 * 
	 * @param correlationId
	 *            The identifier to match the result.
	 * @param sr
	 *            The ServiceRegistration.
	 * @param input
	 *            The ServiceInput obejct which represents the parameters for
	 *            the service-process.
	 */
	public abstract void execute(String correlationId, ServiceRegistration sr, ServiceInput input);

}
