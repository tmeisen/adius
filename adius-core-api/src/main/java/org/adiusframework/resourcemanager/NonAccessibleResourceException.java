package org.adiusframework.resourcemanager;

/**
 * This exception is thrown when an error occurs while accessing a Resource.
 */
public class NonAccessibleResourceException extends Exception {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -3444839326718759726L;

	/**
	 * Creates a new NonAccessibleResourceException with a specific message.
	 * 
	 * @param msg
	 *            The message containing information about this exception.
	 */
	public NonAccessibleResourceException(String msg) {
		super(msg);
	}
}
