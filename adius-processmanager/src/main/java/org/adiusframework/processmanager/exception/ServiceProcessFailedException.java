package org.adiusframework.processmanager.exception;

import org.adiusframework.processmanager.domain.ServiceProcess;

/**
 * The ServiceProcessFailedException is thrown when the processing of a
 * ServiceProcess fails.
 */
public class ServiceProcessFailedException extends ServiceProcessRelatedException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -5309906708794487693L;

	/**
	 * Creates a new ServiceProcessFailedException with a message that contain
	 * special information about the exception and the ServiceProcess what
	 * execution failed.
	 * 
	 * @param message
	 *            The special information.
	 * @param process
	 *            The failed ServiceProcess.
	 */
	public ServiceProcessFailedException(String message, ServiceProcess process) {
		super(message, process);
	}

}
