package org.adiusframework.service;

public class ServiceException extends Exception {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 8847727592356134214L;

	public ServiceException() {
		super();
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
