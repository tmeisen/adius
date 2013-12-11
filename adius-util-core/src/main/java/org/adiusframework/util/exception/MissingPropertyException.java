package org.adiusframework.util.exception;

/**
 * Exception to be thrown if there is something missing in the configuration for
 * a configurable tool.
 */
public class MissingPropertyException extends Exception {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -1440395302139312888L;

	/**
	 * Creates a MissingPropertyException with a specific message.
	 * 
	 * @param message
	 *            The message which contaisn information about this exception.
	 */
	public MissingPropertyException(String message) {
		super(message);
	}

}
