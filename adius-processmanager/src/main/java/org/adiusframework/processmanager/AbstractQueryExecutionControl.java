package org.adiusframework.processmanager;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.InvalidServiceProcessTypeException;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The AbstractQueryExecutionControl handles Queries by searching related
 * objects and, if it's possible and needed, by creating them in case they don't
 * exist.
 */
@Component
public abstract class AbstractQueryExecutionControl extends AbstractQueryStatusEventSource implements
		QueryExecutionControl {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQueryExecutionControl.class);

	private ProcessManagerRepository repository;

	private ServiceProcessExecutionControl serviceProcessExecutionControl;

	private ResourceHandler resourceHandler;

	/**
	 * Return the ResourceHandler which is currently used by the
	 * AbstractQueryExecutionControl.
	 * 
	 * @return The current ResourceHandler.
	 */
	public ResourceHandler getResourceHandler() {
		return resourceHandler;
	}

	/**
	 * Sets a new ResourceHandler.
	 * 
	 * @param resourceHandler
	 *            The new ResourceHandler.
	 */
	public void setResourceHandler(ResourceHandler resourceHandler) {
		this.resourceHandler = resourceHandler;
	}

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
	 * Return the ServiceProcessExecutionControl which is currently used by the
	 * AbstractQueryExecutionControl.
	 * 
	 * @return The current ServiceProcessExecutionControl.
	 */
	public ServiceProcessExecutionControl getServiceProcessExecutionControl() {
		return serviceProcessExecutionControl;
	}

	/**
	 * Return the ProcessManagerRepository which is used by the
	 * AbstractQueryExecutionControl.
	 * 
	 * @return The new ProcessManagerRepository.
	 */
	public ProcessManagerRepository getRepository() {
		return repository;
	}

	/**
	 * Sets the used repository
	 * 
	 * @param repository
	 */
	public void setRepository(ProcessManagerRepository repository) {
		this.repository = repository;
	}

	@Override
	public boolean checkConfiguration() {
		if (repository == null || serviceProcessExecutionControl == null || resourceHandler == null)
			return false;

		return repository.checkConfiguration() && serviceProcessExecutionControl.checkConfiguration()
				&& resourceHandler.checkConfiguration();
	}

	@Override
	public void handleQuery(Query query, String internalId) throws ProcessManagerException {
		ServiceProcess serviceProcess = null;
		int entityId = -1;

		// try to do all necessary preparations, if an exception occurs inform
		// about the error (if the entity id has been passed)
		try {

			// first we have to identify the relevant entity data, which can
			// result in an exception
			LOGGER.debug("Handling query call " + query);
			ServiceProcessDefinition spd = getServiceProcessExecutionControl().findServiceDefinition(query.getType(),
					query.getDomain());
			if (spd == null) {
				throw new InvalidServiceProcessTypeException("Type " + query.getType() + " and domain "
						+ query.getDomain() + " invalid, no service process definition found.", query);
			}
			entityId = identifyDomainEntityId(spd, query);
			LOGGER.debug("Domain dependent entity has been identified as " + entityId);

			// next we can create the service process that represents this query
			// and persist it
			serviceProcess = getRepository().createServiceProcess(query, internalId, entityId);
			if (serviceProcess == null) {
				LOGGER.debug("Query mapping failed, type cannot be mapped.");
				fireEvent(new StandardQueryStatusEvent(internalId, query.getId(),
						"Query is unmapped, can't resolve it's type!", QueryStatus.QUEUEFAILED));
				return;
			}

			// before the real execution can begin, we have to setup the
			// resources of the service process
			LOGGER.debug("Registering query related information.");
			getResourceHandler().registerResources(serviceProcess, query);
			LOGGER.debug("Firing execution event status changed");
			fireEvent(new StandardQueryStatusEvent(serviceProcess.getInternalId(), serviceProcess.getExternalId(),
					QueryStatus.EXECUTION));
		} catch (ProcessManagerException e) {
			if (entityId > -1)
				handleError(query, entityId);
			throw e;
		} catch (RuntimeException e) {
			if (entityId > -1)
				handleError(query, entityId);
			throw e;
		}

		// at this point either an exception has been thrown and handled, or the
		// service process exists and it can be executed, error handling is done
		// by service process execution control
		if (serviceProcess != null)
			serviceProcess = executeServiceProcess(serviceProcess);
	}

	/**
	 * Executes a ServiceProcess by calling executeProcess of the
	 * ServiceProcessExecutionControl.
	 * 
	 * @see ServiceProcessExecutionControl#executeProcess(ServiceProcess)
	 * @param serviceProcess
	 *            The ServiceProcess which should be executed.
	 * @return The result of the method-call.
	 * @throws ProcessManagerException
	 *             If an internal error occurs.
	 */
	protected ServiceProcess executeServiceProcess(ServiceProcess serviceProcess) throws ProcessManagerException {
		return getServiceProcessExecutionControl().executeProcess(serviceProcess);
	}

	/**
	 * Determines the related domain specific entity id to a given Query.
	 * Because this method is mainly domain dependent it has to be implemented
	 * by a concrete query execution control.
	 * 
	 * @param processDefinition
	 *            the corresponding process definition
	 * @param query
	 *            The given Query.
	 * @return The determined entity id
	 * @throws ProcessManagerException
	 *             If a further processing of the query cannot be accomplished.
	 */
	protected abstract int identifyDomainEntityId(ServiceProcessDefinition processDefinition, Query query)
			throws ProcessManagerException;

	/**
	 * Method used to inform super classes that an error for the given entity id
	 * has been occurred, before the service process could be executed.
	 * 
	 * @param query
	 *            the query that has raised the error
	 * @param entityId
	 *            the entity id that is affected
	 * @throws ProcessManagerException
	 *             if an error occurs during the exception handling
	 */
	protected abstract void handleError(Query query, int entityId) throws ProcessManagerException;

}
