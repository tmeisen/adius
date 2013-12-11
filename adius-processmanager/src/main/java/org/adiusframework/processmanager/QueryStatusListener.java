package org.adiusframework.processmanager;

/**
 * A QueryStatusListener receives a notification when something happens in the
 * execution of a query. Therefore it must be registered at a
 * QueryStatusEventSource.
 * 
 * @see QueryStatusEventSource#registerListener(QueryStatusListener)
 */
public interface QueryStatusListener {

	/**
	 * This method is called when the status of a query is changed.
	 * 
	 * @param event
	 *            An venet-object that contains all data that are related to the
	 *            change.
	 */
	public void queryStatusChanged(QueryStatusEvent event);

}
