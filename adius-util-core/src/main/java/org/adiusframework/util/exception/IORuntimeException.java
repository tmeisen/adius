package org.adiusframework.util.exception;

import java.io.IOException;

/**
 * Wraps an IO exception into a runtime exception which must therefore not
 * catched.
 */
public class IORuntimeException extends RuntimeException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -1166131626725840247L;

	/**
	 * Create a not IORuntimeException based on a given IOException.
	 * 
	 * @param e
	 *            The given IOException.
	 */
	public IORuntimeException(IOException e) {
		super(e);
	}
}
