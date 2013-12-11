package org.adiusframework.util.exception;

/**
 * Runtime exception that wraps (checked) exceptions that were raised
 * unexpectedly. This class is derived from RuntimeException in order not to
 * clutter the code with unexpected exceptions.
 */
public class UnexpectedException extends RuntimeException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -7216635264707009214L;

	/**
	 * Constructs a new unexpected exception with the specified detailed
	 * message.
	 * 
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 */
	public UnexpectedException(String message) {
		super(message);
	}

	/**
	 * Constructs a new unexpected exception using the given exception to create
	 * a message.
	 * 
	 * @param e
	 *            The exception raised that has been raised unexpected.
	 */
	public UnexpectedException(Exception e) {
		super(e);
	}

	/**
	 * Constructs a new unexpected exception with the specified cause and a
	 * detail message of <tt>(cause==null ? null : cause.toString())</tt> (which
	 * typically contains the class and detail message of <tt>cause</tt>). This
	 * constructor is useful for runtime exceptions that are little more than
	 * wrappers for other throwables.
	 * 
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            The cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A <tt>null</tt> value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public UnexpectedException(String message, Throwable cause) {
		super(message, cause);
	}
}
