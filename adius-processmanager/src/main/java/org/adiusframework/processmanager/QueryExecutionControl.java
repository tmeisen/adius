package org.adiusframework.processmanager;

import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;
import org.adiusframework.util.IsConfigured;

/**
 * The QueryExecutionControl is the first instance which processes Queries. It
 * generates a suitable ServiceProcess and starts it's execution.
 */
public interface QueryExecutionControl extends QueryStatusEventSource, IsConfigured {

	/**
	 * Starts the execution of the given Query.
	 * 
	 * @param query
	 *            The given Query.
	 * @param internalId
	 *            The internal identification string for the query.
	 * @throws ProcessManagerException
	 *             If an internal exception occurs.
	 */
	public void handleQuery(Query query, String internalId) throws ProcessManagerException;

}
