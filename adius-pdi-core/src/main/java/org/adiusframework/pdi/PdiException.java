package org.adiusframework.pdi;

public class PdiException extends RuntimeException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 4570868010995785479L;

	public PdiException(String message) {
		super(message);
	}

	public PdiException(Exception e) {
		super(e);
	}

}
