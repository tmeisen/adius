package org.adiusframework.util.db;

public class NonUniqueIndexException extends Exception {
	/**
     * 
     */
	private static final long serialVersionUID = -3124430728968206544L;

	public NonUniqueIndexException() {
		super();
	}

	public NonUniqueIndexException(String message) {
		super(message);
	}

	public NonUniqueIndexException(Throwable cause) {
		super(cause);
	}

	public NonUniqueIndexException(String message, Throwable cause) {
		super(message, cause);
	}
}
