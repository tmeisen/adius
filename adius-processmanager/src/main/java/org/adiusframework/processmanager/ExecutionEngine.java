package org.adiusframework.processmanager;

import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;
import org.adiusframework.util.IsConfigured;

/**
 * The ExecutionEngine should act as gateway to the ProcessManager. It redirects
 * requests and answers from the ProcessManager interface to the suitable
 * internal components.
 */
public interface ExecutionEngine extends QueryStatusEventSource, IsConfigured {

	/**
	 * Redirects requests for executing a Query.
	 * 
	 * @param query
	 *            The Query which should be executed.
	 * @param internalId
	 *            The internal identification string for the query.
	 * @throws ProcessManagerException
	 *             If an internal exception occurs.
	 */
	public void handleQuery(Query query, String internalId) throws ProcessManagerException;

	/**
	 * Redirects answerers from service-process.
	 * 
	 * @param result
	 *            The result of the service-process.
	 * @throws ProcessManagerException
	 *             If an internal exception occurs.
	 */
	public void handleResult(ServiceResultData result) throws ProcessManagerException;

}
