package org.adiusframework.processmanager;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.ServiceTask;
import org.adiusframework.query.Query;
import org.adiusframework.resource.serviceplan.ServiceProcessPlan;
import org.adiusframework.util.IsConfigured;

/**
 * A ProcessManagerRepository is the gateway for organizing and saving
 * information about running services and simulations.
 */
public interface ProcessManagerRepository extends IsConfigured {

	/**
	 * Creates a ServiceProcess and initializes basic data.
	 * 
	 * @param query
	 *            The corresponding Query.
	 * @param internalId
	 *            The internal identification string for the query.
	 * @param entityId
	 *            The domain specific entity id the query has to be attached to.
	 * @return The new ServcieProcess.
	 */
	public ServiceProcess createServiceProcess(Query query, String internalId, int entityId);

	/**
	 * Tries to find a ServiceProcess by its internalId.
	 * 
	 * @param internalId
	 *            The identifier for a ServiceProcess
	 * @return The ServiceProcess which is represented by the given id.
	 */
	public ServiceProcess findServiceProcessByInternalId(String internalId);

	/**
	 * Saves a new ServiceTask.
	 * 
	 * @param task
	 *            The new ServiceTask.
	 */
	public void saveServiceTask(ServiceTask task);

	/**
	 * Refreshes all data which are related to a given ServiceProcess.
	 * 
	 * @param process
	 *            The given ServiceProcess.
	 */
	public void refreshServiceProcess(ServiceProcess process);

	/**
	 * Extends a ServiceTask with a ServiceProcessPlan by adding a
	 * ServiceSubTask for every ServiceTemplate which is defined in a
	 * ServiceProcessPlan.
	 * 
	 * @param task
	 *            The ServiceTask which should be extended.
	 * @param plan
	 *            The ServiceProcessPlan.
	 */
	public void extendTaskByServiceProcessPlan(ServiceTask task, ServiceProcessPlan plan);

}