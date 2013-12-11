package org.adiusframework.processmanager.exception;

import org.adiusframework.processmanager.domain.ServiceProcess;

/**
 * The ServiceProcessAccessException is thrown when there is a problem with
 * accessing a special ServiceProcess.
 */
public class ServiceProcessAccessException extends ServiceProcessRelatedException {
	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -6443651068622451794L;

	/**
	 * Creates a new ProcessManagerException with a message that contain special
	 * information about the exception and the ServiceProcess that caused this
	 * exception.
	 * 
	 * @param message
	 *            The special information.
	 * @param process
	 *            The related ServiceProcess.
	 */
	public ServiceProcessAccessException(String message, ServiceProcess process) {
		super(message, process);
		setServiceProcess(process);
	}

}
