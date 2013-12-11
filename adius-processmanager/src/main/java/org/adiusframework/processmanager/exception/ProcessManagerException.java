package org.adiusframework.processmanager.exception;

/**
 * The ProcessManagerException is thrown when a general exception occurs while
 * running the process-manager.
 */
public abstract class ProcessManagerException extends Exception {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -6443651068622451794L;

	/**
	 * Creates a new ProcessManagerException with a message that contain special
	 * information about the exception.
	 * 
	 * @param message
	 *            The special information.
	 */
	public ProcessManagerException(String message) {
		super(message);
	}

}
