package org.adiusframework.util.exception;

/**
 * The UnserializableException should be thrown if something can not be
 * serialized although it should be.
 */
public class UnserializableException extends Exception {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 5404757615525701592L;

	/**
	 * Creates a new UnserializableException with a special message.
	 * 
	 * @param exception
	 *            The message which contains information about the exception.
	 */
	public UnserializableException(String exception) {
		super(exception);
	}
}
