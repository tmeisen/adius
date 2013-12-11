package org.adiusframework.processmanager;

import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;

/**
 * The BasicExecutionEngine is the first executing unit in the process manager
 * and separates different request-objects. Query objects are forwarded to a
 * QueryExecutionControl object, and ServiceResultData to a
 * ServiceProcessExecutionProtocol object. Request on the QueryStatusEventSource
 * are forwarded to both objects.
 */
public class BasicExecutionEngine implements ExecutionEngine {

	private QueryExecutionControl queryExecutionControl;

	private ServiceProcessExecutionControl serviceProcessExecutionControl;

	/**
	 * Sets a new ServiceProcessExecutionControl.
	 * 
	 * @param serviceProcessExecutionControl
	 *            The new ServiceProcessExecutionControl.
	 */
	public void setServiceProcessExecutionControl(ServiceProcessExecutionControl serviceProcessExecutionControl) {
		this.serviceProcessExecutionControl = serviceProcessExecutionControl;
	}

	/**
	 * Returns the ServiceProcessExecutionControl which currently is used by the
	 * BasicExecutionEngine.
	 * 
	 * @return The current ServiceProcessExecutionControl.
	 */
	public ServiceProcessExecutionControl getServiceProcessExecutionControl() {
		return serviceProcessExecutionControl;
	}

	/**
	 * Sets a new QueryExecutionControl.
	 * 
	 * @param queryExecutionControl
	 *            The new QueryExecutionControl.
	 */
	public void setQueryExecutionControl(QueryExecutionControl queryExecutionControl) {
		this.queryExecutionControl = queryExecutionControl;
	}

	/**
	 * Return the QueryExecutionControl which is currently used by the
	 * BasicExecutionEngine.
	 * 
	 * @return The current QueryExecutionControl.
	 */
	public QueryExecutionControl getQueryExecutionControl() {
		return queryExecutionControl;
	}

	@Override
	public boolean checkConfiguration() {
		if (queryExecutionControl == null || serviceProcessExecutionControl == null)
			return false;

		return queryExecutionControl.checkConfiguration() && serviceProcessExecutionControl.checkConfiguration();
	}

	@Override
	public void handleQuery(Query query, String internalId) throws ProcessManagerException {
		getQueryExecutionControl().handleQuery(query, internalId);
	}

	@Override
	public void handleResult(ServiceResultData result) throws ProcessManagerException {
		getServiceProcessExecutionControl().handleResult(result);
	}

	@Override
	public void registerListener(QueryStatusListener listener) {
		getQueryExecutionControl().registerListener(listener);
		getServiceProcessExecutionControl().registerListener(listener);
	}

	@Override
	public void unregisterListener(QueryStatusListener listener) {
		getQueryExecutionControl().unregisterListener(listener);
		getServiceProcessExecutionControl().unregisterListener(listener);
	}

}
