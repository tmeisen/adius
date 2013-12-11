package org.adiusframework.processmanager.exception;

public class InvalidDomainException extends ProcessManagerException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -2316162801218685871L;

	/**
	 * Creates a new InvalidDomainException with a message that contain special
	 * information about the invalid domain that caused the exception.
	 * 
	 * @param domain
	 *            the name of the invalid domain.
	 */
	public InvalidDomainException(String domain) {
		super("Invalid domain " + domain + " is not supported");
	}

}
