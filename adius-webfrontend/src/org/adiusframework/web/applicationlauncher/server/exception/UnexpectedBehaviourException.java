package org.adiusframework.web.applicationlauncher.server.exception;

/**
 * This runtime exception should be thrown when an unexpected error 
 * happens, while the creation or operation of process or streams.
 *
 * @author Tobias Meisen
 */
public class UnexpectedBehaviourException extends RuntimeException {
	
	/** Unique identifier for this exception */
	private static final long serialVersionUID = 2197080865360404365L;

	/** 
	 * Constructor that will receive an exception to detailed the
	 * problematic situation. 
	 * 
	 * @param e Exception to complement this one.
	 */
	public UnexpectedBehaviourException(Exception e) {
		super(e);
	} // end : constructor

	/** 
	 * Constructor that will use a text, to complement the 
	 * problematic scenario.
	 * 
	 * @param msg Text with detailed information about the exception.
	 */	
	public UnexpectedBehaviourException(String msg) {
		super(msg);
	} // end : constructor
	
}