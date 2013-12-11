package org.adiusframework.util.reflection;

/**
 * This Exception is used to wrap the Exceptions, that occur when a member does
 * not exist or can't be accessed with the java reflect api, as a
 * RuntimeException that it need not be catched.
 */
public class EntityNotFoundException extends RuntimeException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 4927470651824635462L;

	/**
	 * Creates a new EntityNotFoundException with a String that contains special
	 * information about this Exception.
	 * 
	 * @param message
	 *            The special information.
	 */
	public EntityNotFoundException(String message) {
		super(message);
	}
}
