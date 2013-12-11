package org.adiusframework.processmanager;


import org.adiusframework.query.Query;
import org.adiusframework.util.IsConfigured;
import org.adiusframework.util.net.Callback;

/**
 * The ProcessManager interface defines methods which allows other components to
 * interact with the whole ProcessManager.
 */
public interface ProcessManager extends IsConfigured {

	/**
	 * This method should be called by services when they finished their work,
	 * whether or with an error or success.
	 * 
	 * @param result
	 *            The objects which represents the result of the service.
	 */
	public void handleResult(ServiceResultData result);

	/**
	 * This method should be called by components which want to have a Query
	 * executed by the ProcessManager.
	 * 
	 * @param query
	 *            The query which should be executed.
	 * @param callback
	 *            The object which will be notified by the ProcessManager if
	 *            something happens with the Query.
	 */
	public void handleQuery(Query query, Callback callback);

}
