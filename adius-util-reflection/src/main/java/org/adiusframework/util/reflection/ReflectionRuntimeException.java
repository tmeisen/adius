package org.adiusframework.util.reflection;

/**
 * This Exception is used to wrap general Exceptions, that occur while using the
 * java reflect api, as a RuntimeException that it need not be catched.
 * 
 * @see EntityNotFoundException
 */
public class ReflectionRuntimeException extends RuntimeException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -1531482538364915736L;

	/**
	 * The Exception to be wrapped.
	 */
	private Exception e;

	/**
	 * Creates a new ReflectionRuntimeException and stores the cause and a
	 * String with special information about this Exception.
	 * 
	 * @param e
	 *            The cause of this Exception.
	 * @param message
	 *            The special information.
	 */
	public ReflectionRuntimeException(Exception e, String message) {
		super(message);
		this.e = e;
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " (" + this.e.getClass().toString() + ": " + this.e.getMessage() + ")";
	}
}
