package org.adiusframework.processmanager.domain.dao;

import java.util.List;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.ServiceProcessLog;
import org.adiusframework.processmanager.domain.ServiceTask;
import org.adiusframework.util.db.GenericDao;

/**
 * The ServiceProcessLogDao stores ServiceProcessLogs that can be accessed by
 * the corresponding ServiceProcess or ServiceTask.
 */
public interface ServiceProcessLogDao extends GenericDao<ServiceProcessLog, Integer> {

	/**
	 * Finds all ServiceProcessLogs which correspond to a given ServiceProcess.
	 * 
	 * @param process
	 *            The given ServiceProcess.
	 * @return A List with all corresponding ServiceProcessLogs.
	 */
	public List<ServiceProcessLog> findLogs(ServiceProcess process);

	/**
	 * Finds all ServiceProcessLogs which correspond to a given ServiceTask.
	 * 
	 * @param task
	 *            The given ServiceTask.
	 * @return A List with all corresponding ServiceProcessLogs.
	 */
	public List<ServiceProcessLog> findLogs(ServiceTask task);
}