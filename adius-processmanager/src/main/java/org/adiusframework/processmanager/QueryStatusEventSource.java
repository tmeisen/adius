package org.adiusframework.processmanager;

/**
 * The QueryStatusEventSource interface should be implemented by any class whose
 * instances control the execution of a query and therefore have the ability to
 * publish status-changes to registered QueryStatusListener
 */
public interface QueryStatusEventSource {
	/**
	 * Registers a QueryStatusListener which will receive QueryStatusEvent until
	 * it's unregistered
	 * 
	 * @param listener
	 *            The listener which should be registered
	 * @see #unregisterListener(QueryStatusListener)
	 */
	public void registerListener(QueryStatusListener listener);

	/**
	 * Unregisters a QueryStatusListener.
	 * 
	 * @param listener
	 *            The listener which should be unregistered
	 * @see #registerListener(QueryStatusListener)
	 */
	public void unregisterListener(QueryStatusListener listener);

}
