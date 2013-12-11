package org.adiusframework.processmanager.domain.dao;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.util.db.GenericDao;

/**
 * The ServiceProcessDao stores ServiceProcesses that are accessed by their
 * internal identification number.
 */
public interface ServiceProcessDao extends GenericDao<ServiceProcess, Integer> {

	/**
	 * Finds a ServiceProcess based on his ID.
	 * 
	 * @param id
	 *            The ID of the ServiceProcess.
	 * @return The ServiceProcess or null if no process could be found.
	 */
	public abstract ServiceProcess findByInternalId(String id);
}
