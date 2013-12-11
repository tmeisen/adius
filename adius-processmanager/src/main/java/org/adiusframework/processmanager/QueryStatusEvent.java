package org.adiusframework.processmanager;

/**
 * A QueryStatusEvent is a structure to store all data in one single object ,
 * which are related to an event, which is fired while executing a Query.
 */
public interface QueryStatusEvent {

	/**
	 * Return the internal identifier of the Query in which context this event
	 * was fired.
	 * 
	 * @return The internal identifier.
	 */
	public abstract String getInternalId();

	/**
	 * Return the external identifier of the Query in which context this event
	 * was fired.
	 * 
	 * @return The external identifier.
	 */
	public abstract String getExternalId();

	/**
	 * Return the (new) QueryStatus of the Query in which context this event was
	 * fired.
	 * 
	 * @return The (new) QueryStatus
	 */
	public abstract QueryStatus getStatus();

	/**
	 * Return a String which was attached to this event when it was created.
	 * 
	 * @return The attached String.
	 */
	public abstract String getAttachment();

}
