package org.adiusframework.processmanager.exception;

import org.adiusframework.query.Query;

/**
 * The InvalidServiceProcessTypeException is thrown when a Query has no or more
 * than allow aspects.
 */
public class InvalidServiceProcessTypeException extends QueryRelatedException {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -2316162801218685871L;

	/**
	 * Creates a new InvalidServiceProcessTypeException with a message that
	 * contain special information about the exception and the Query that caused
	 * the exception.
	 * 
	 * @param message
	 *            The special information.
	 * @param query
	 *            The causing Query.
	 */
	public InvalidServiceProcessTypeException(String message, Query query) {
		super(message, query);
	}

}
