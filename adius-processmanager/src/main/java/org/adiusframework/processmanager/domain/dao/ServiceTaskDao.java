package org.adiusframework.processmanager.domain.dao;

import java.util.List;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.ServiceTask;
import org.adiusframework.util.db.GenericDao;

/**
 * The ServiceTaskDao stores ServiceTasks that are accessed by their
 * corresponding ServiceProcess.
 */
public interface ServiceTaskDao extends GenericDao<ServiceTask, Integer> {

	/**
	 * Finds all ServiceTasks which correspond to a given ServiceProcess.
	 * 
	 * @param process
	 *            The given ServiceProcess.
	 * @return A List with all corresponding ServiceTasks.
	 */
	public List<ServiceTask> findByProcess(ServiceProcess process);
}
