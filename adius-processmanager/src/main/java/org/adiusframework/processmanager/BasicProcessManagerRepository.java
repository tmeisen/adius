package org.adiusframework.processmanager;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.ServiceSubTask;
import org.adiusframework.processmanager.domain.ServiceSubTaskProperty;
import org.adiusframework.processmanager.domain.ServiceTask;
import org.adiusframework.processmanager.domain.dao.ServiceProcessDao;
import org.adiusframework.processmanager.domain.dao.ServiceTaskDao;
import org.adiusframework.query.Query;
import org.adiusframework.resource.serviceplan.ServiceProcessPlan;
import org.adiusframework.resource.serviceplan.ServiceTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * The BasicProcessManagerRepository fulfills the demands, put by the
 * ProcessManagerRepository interface, by managing DAOs (Data-Access-Object)
 * which are defined in the org.adiusframework.processmanager.domain.dao
 * package.
 */
@Repository
public class BasicProcessManagerRepository implements ProcessManagerRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicProcessManagerRepository.class);

	private ServiceProcessDao serviceProcessDao;

	private ServiceTaskDao serviceTaskDao;

	@Autowired
	@Required
	public void setServiceProcessDao(ServiceProcessDao serviceProcessDao) {
		this.serviceProcessDao = serviceProcessDao;
	}

	public ServiceProcessDao getServiceProcessDao() {
		return serviceProcessDao;
	}

	@Autowired
	@Required
	public void setServiceTaskDao(ServiceTaskDao serviceTaskDao) {
		this.serviceTaskDao = serviceTaskDao;
	}

	public ServiceTaskDao getServiceTaskDao() {
		return serviceTaskDao;
	}

	@Override
	public boolean checkConfiguration() {
		if (serviceProcessDao == null || serviceTaskDao == null)
			return false;

		return serviceProcessDao.checkConfiguration() && serviceTaskDao.checkConfiguration();
	}

	@Override
	@Transactional(value = "pmTransactionManager")
	public ServiceProcess createServiceProcess(Query query, String internalId, int entityId) {
		ServiceProcess serviceProcess = new ServiceProcess();

		// create the internal id of this query that assures a unique
		// identification and maps the external id
		serviceProcess.setInternalId(internalId);
		serviceProcess.setExternalId(query.getId());
		serviceProcess.setEntityId(entityId);
		serviceProcess.setDomain(query.getDomain());
		serviceProcess.setType(query.getType());
		getServiceProcessDao().save(serviceProcess);
		return serviceProcess;
	}

	@Override
	@Transactional(value = "pmTransactionManager")
	public ServiceProcess findServiceProcessByInternalId(String internalId) {
		return getServiceProcessDao().findByInternalId(internalId);
	}

	@Override
	@Transactional(value = "pmTransactionManager")
	public void saveServiceTask(ServiceTask task) {
		LOGGER.debug("Saving service task: " + task);
		getServiceTaskDao().saveOrUpdate(task);
		LOGGER.debug("Service task saved: " + task);
	}

	@Override
	@Transactional(value = "pmTransactionManager")
	public void refreshServiceProcess(ServiceProcess process) {
		getServiceProcessDao().refresh(process);
	}

	@Override
	public void extendTaskByServiceProcessPlan(ServiceTask task, ServiceProcessPlan plan) {
		LOGGER.debug("Extending task by plan data...");
		for (int i = 0; i < plan.getLength(); i++) {

			// we have to create the subtask represented by this template
			ServiceTemplate template = plan.getServiceTemplate(i);
			ServiceSubTask subTask = new ServiceSubTask(task, template.getCategory(), i);
			if (template.getSubCategory() != null)
				subTask.setSubCategory(template.getSubCategory());

			// now we have to set the properties
			for (String property : template.getProperties().stringPropertyNames()) {
				ServiceSubTaskProperty subTaskProperty = new ServiceSubTaskProperty(subTask, property, template
						.getProperties().getProperty(property));
				subTask.getProperties().add(subTaskProperty);
			}
			LOGGER.debug("Adding subtask " + subTask + " to task " + task);
			task.getSubTasks().put(i, subTask);
		}

		// finally we can save the task
		LOGGER.debug("Saving extended task " + task);
		saveServiceTask(task);
	}

}
