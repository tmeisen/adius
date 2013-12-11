package org.adiusframework.processmanager;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.util.IsConfigured;

/**
 * The ServiceProcessExecutionControl is the main component of the
 * ProcessManager, it manages the execution of a ServiceProcess. It's key
 * functionality is to determine the next ServiceTask and initiate it's
 * executing and to process the results, what are returned by service-processes.
 */
public interface ServiceProcessExecutionControl extends QueryStatusEventSource, IsConfigured {

	/**
	 * Processes the result of a service-process and starts the continuous
	 * execution of the ServiceProcess.
	 * 
	 * @param result
	 *            The object which contains the result-data.
	 * @throws ProcessManagerException
	 *             If an internal exception occurs or if the service- process
	 *             failed and the ServiceProcess can't be executed further.
	 */
	public void handleResult(ServiceResultData result) throws ProcessManagerException;

	/**
	 * Executes a given ServiceProcess.
	 * 
	 * @param process
	 *            The ServiceProcess which should be executed.
	 * @return The updated ServiceProcess after the execution has been
	 *         triggered.
	 * @throws ProcessManagerException
	 *             If an internal exception occurs.
	 */
	public ServiceProcess executeProcess(ServiceProcess process) throws ProcessManagerException;

	/**
	 * Searches for a service definition that matches the given type and domain.
	 * 
	 * @param type
	 *            the type
	 * @param domain
	 *            the domain
	 * @return the definition of the service process that matches the type and
	 *         domain, <code>null</code> if no definition has been found
	 */
	public ServiceProcessDefinition findServiceDefinition(String type, String domain);

}
