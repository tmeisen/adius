package org.adiusframework.processmanager;

import java.util.UUID;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.IsServiceProcessRelated;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.ErrorQueryResult;
import org.adiusframework.query.Query;
import org.adiusframework.query.QueryResult;
import org.adiusframework.query.StandardQueryResult;
import org.adiusframework.util.net.Callback;
import org.adiusframework.util.net.CallbackExecutor;
import org.adiusframework.util.net.CallbackRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * The BasicProcessManager is the first instance which executes events from
 * outside the whole ProcessManager, such as handling requests for executing a
 * new Query or the feedback of execution service-processes. Therefore it can be
 * seen as the interface to other adius-projects.
 */
public class BasicProcessManager implements ProcessManager, QueryStatusListener {

	private static Logger LOGGER = LoggerFactory.getLogger(BasicProcessManager.class);

	private ExecutionEngine executionEngine;

	private CallbackRepository callbackRepository;

	private CallbackExecutor callbackExecutor;

	/**
	 * @return The current CallbackExecutor which is notified if something
	 *         happens with a Query.
	 */
	@Required
	public CallbackExecutor getCallbackExecutor() {
		return callbackExecutor;
	}

	/**
	 * Sets a new CallbackExecutor.
	 * 
	 * @param callbackExecutor
	 *            The new CallbackExecutor.
	 */
	public void setCallbackExecutor(CallbackExecutor callbackExecutor) {
		this.callbackExecutor = callbackExecutor;
	}

	/**
	 * @return The current ExecutionEngine which process Query objects and
	 *         responses of service-processes.
	 */
	@Required
	public ExecutionEngine getExecutionEngine() {
		return executionEngine;
	}

	/**
	 * Sets a new ExecutionEngine.
	 * 
	 * @param executionEngine
	 *            The new ExecutionEngine.
	 */
	public void setExecutionEngine(ExecutionEngine executionEngine) {
		this.executionEngine = executionEngine;
		getExecutionEngine().registerListener(this);
	}

	/**
	 * @return The CallbackRepository in which the BasicProcessManager stores
	 *         for every Query the corresponding Callback object, if one is
	 *         given.
	 */
	@Required
	public CallbackRepository getCallbackRepository() {
		return callbackRepository;
	}

	/**
	 * Sets a new CallbackRepository.
	 * 
	 * @param callbackRepository
	 *            The new CallbackRepository.
	 */
	public void setCallbackRepository(CallbackRepository callbackRepository) {
		this.callbackRepository = callbackRepository;
	}

	@Override
	public boolean checkConfiguration() {
		if (executionEngine == null || callbackRepository == null || callbackExecutor == null)
			return false;

		// TODO Add if implemented
		return executionEngine.checkConfiguration(); // &&
														// callbackRepository.checkConfiguration()
														// &&
														// callbackExecutor.checkConfiguration();
	}

	@Override
	public void handleQuery(Query query, Callback callback) {
		try {

			// validate query input
			if (query == null || callback == null || query.getId() == null || query.getId().isEmpty()) {
				LOGGER.error("Unsufficient query received");
				return;
			}

			// create an internal identifier an register the callback
			String internalId = UUID.randomUUID().toString();
			if (callback != null)
				getCallbackRepository().register(internalId, callback);
			getExecutionEngine().handleQuery(query, internalId);
		} catch (ProcessManagerException e) {
			LOGGER.error("An error occured during query handling: " + e.getMessage());
			if (callback != null)
				getCallbackExecutor().execute(callback, new ErrorQueryResult(query.getId(), e.getMessage()));
		} catch (Exception e) {
			LOGGER.error("An unexpected error occured during service processing: " + e.getMessage());
			if (callback != null && query != null)
				getCallbackExecutor().execute(callback, new ErrorQueryResult(query.getId(), e.getMessage()));
		}
	}

	@Override
	public void handleResult(ServiceResultData result) {
		try {
			getExecutionEngine().handleResult(result);
		} catch (ProcessManagerException e) {
			LOGGER.error("An error occured during result handling: " + e.getMessage());

			// lets check if we can execute a callback
			if (IsServiceProcessRelated.class.isInstance(e)) {
				ServiceProcess process = ((IsServiceProcessRelated) e).getServiceProcess();
				executeCallback(process.getInternalId(), new ErrorQueryResult(process.getExternalId(), e.getMessage()));
			} else
				LOGGER.error("Additional callback information could not be found.");
		} catch (Exception e) {
			LOGGER.error("An unexpected error occured during service processing: " + e.getMessage() + " ("
					+ e.getClass() + ")");
			LOGGER.error("No callback could be executed!");
		}
	}

	@Override
	public void queryStatusChanged(QueryStatusEvent event) {

		// first we check if the new status is relevant and if a callback is
		// registered
		LOGGER.debug("Query status changed event " + event.getStatus());
		if (event.getStatus().equals(QueryStatus.QUEUED) || event.getStatus().equals(QueryStatus.EXECUTION))
			return;
		if (getCallbackRepository().find(event.getInternalId()) == null)
			return;
		LOGGER.debug("Event related callback found...");

		// now we have to react according to the given status
		if (event.getStatus().equals(QueryStatus.QUEUETIMEDOUT))
			executeCallback(event.getInternalId(), new ErrorQueryResult(event.getExternalId(), "Queue timedout"));
		else if (event.getStatus().equals(QueryStatus.QUEUEFAILED))
			executeCallback(event.getInternalId(), new ErrorQueryResult(event.getExternalId(), event.getAttachment()));
		else if (event.getStatus().equals(QueryStatus.FINISHED))
			executeCallback(event.getInternalId(), new StandardQueryResult(event.getExternalId()));
	}

	/**
	 * Internal method to simplify finding, executing and deleting of a Callback
	 * in the CallbackRepository.
	 * 
	 * @param internalId
	 *            The ID, which identifies the Callback.
	 * @param queryResult
	 *            The result of the Query which should be published to the
	 *            Callback.
	 */
	protected synchronized void executeCallback(String internalId, QueryResult queryResult) {
		Callback callback = getCallbackRepository().find(internalId);
		if (callback == null)
			LOGGER.debug("No callback registered for internal id " + internalId);
		else {
			getCallbackExecutor().execute(callback, queryResult);
			getCallbackRepository().unregister(internalId);
		}
	}

}
