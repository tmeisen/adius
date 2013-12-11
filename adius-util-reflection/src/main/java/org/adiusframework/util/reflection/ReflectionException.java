package org.adiusframework.util.reflection;

/**
 * A ReflectionException is thrown when a exception occurs while using classes
 * and method of the java-reflect-api.
 */
public class ReflectionException extends Exception {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -5102106555495828717L;

	/**
	 * Creates a new ReflectionException with a String that contains specific
	 * information about this Exception.
	 * 
	 * @param message
	 *            The specific information.
	 */
	public ReflectionException(String message) {
		super(message);
	}

	/**
	 * Creates a new ReflectionException and adds the cause of it.
	 * 
	 * @param t
	 *            The cause of this Exception.
	 */
	public ReflectionException(Throwable t) {
		super(t);
	}

	/**
	 * Creates a new ReflectionException with a String that contains specific
	 * information about this Exception. Moreover the cause of this Exception is
	 * added.
	 * 
	 * @param message
	 *            The specific information.
	 * @param t
	 *            The cause of this Exception.
	 */
	public ReflectionException(String message, Throwable t) {
		super(message, t);
	}
}
