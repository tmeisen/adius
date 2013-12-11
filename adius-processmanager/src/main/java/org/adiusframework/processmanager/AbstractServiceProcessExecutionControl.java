package org.adiusframework.processmanager;

import java.io.Serializable;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.ServiceSubTask;
import org.adiusframework.processmanager.domain.ServiceSubTaskProperty;
import org.adiusframework.processmanager.domain.ServiceSubTaskStatus;
import org.adiusframework.processmanager.domain.ServiceTask;
import org.adiusframework.processmanager.domain.ServiceTaskStatus;
import org.adiusframework.processmanager.exception.InvalidDomainException;
import org.adiusframework.processmanager.exception.InvalidProcessTypeException;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.processmanager.exception.ServiceProcessFailedException;
import org.adiusframework.processmanager.xml.ServiceTemplateType;
import org.adiusframework.processmanager.xml.TaskType;
import org.adiusframework.resource.ObjectResource;
import org.adiusframework.resource.Resource;
import org.adiusframework.resource.StringResourceCapability;
import org.adiusframework.resource.serviceplan.ServiceProcessPlan;
import org.adiusframework.service.CategoryServiceCapabilityRule;
import org.adiusframework.service.ErrorServiceResult;
import org.adiusframework.service.ServiceCapabilityRule;
import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.service.StandardServiceInput;
import org.adiusframework.service.StandardServiceResult;
import org.adiusframework.service.xml.ResourceRequirement;
import org.adiusframework.serviceregistry.ServiceFinder;
import org.adiusframework.util.datastructures.SystemData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AbstractServiceProcessExecutionControl provides a basic implementation of
 * the ServiceProcessExecutionControl interface.
 */
public abstract class AbstractServiceProcessExecutionControl extends AbstractQueryStatusEventSource implements
		ServiceProcessExecutionControl {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractServiceProcessExecutionControl.class);

	private ServiceProcessDefinitionFinder serviceDefinitionFinder;

	private ServiceFinder serviceFinder;

	private ResourceHandler resourceHandler;

	private ServiceExecutorFactory serviceExecutorFactory;

	private ProcessManagerRepository repository;

	public abstract void handleErrorResult(ServiceProcess serviceProcess) throws InvalidDomainException;

	public abstract void handleServiceProcessResult(ServiceProcess serviceProcess) throws InvalidDomainException;

	public ProcessManagerRepository getRepository() {
		return repository;
	}

	public void setRepository(ProcessManagerRepository repository) {
		this.repository = repository;
	}

	/**
	 * Returns the ServiceProcessDefinitionFinder which is currently used by the
	 * AbstractServiceProcessExecutionControl.
	 * 
	 * @return The current ServiceProcessDefinitionFinder.
	 */
	public ServiceProcessDefinitionFinder getServiceDefinitionFinder() {
		return serviceDefinitionFinder;
	}

	/**
	 * Sets a new ServiceProcessDefinitionFinder.
	 * 
	 * @param serviceDefinitionFinder
	 *            The new ServiceProcessDefinitionFinder.
	 */
	public void setServiceDefinitionFinder(ServiceProcessDefinitionFinder serviceDefinitionFinder) {
		this.serviceDefinitionFinder = serviceDefinitionFinder;
	}

	/**
	 * Returns the ResourceHandler which is currently used by the
	 * AbstractServiceProcessExecutionControl.
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
	 * Returns the ServiceFinder which is currently used by the
	 * AbstractServiceProcessExecutionControl.
	 * 
	 * @return The current ServcieFinder.
	 */
	public ServiceFinder getServiceFinder() {
		return serviceFinder;
	}

	/**
	 * Sets a new ServiceFinder.
	 * 
	 * @param serviceFinder
	 *            The new ServcieFinder.
	 */
	public void setServiceFinder(ServiceFinder serviceFinder) {
		this.serviceFinder = serviceFinder;
	}

	/**
	 * Returns the ServiceExecutorFactory which is currently used by the
	 * AbstractServiceProcessExecutionControl.
	 * 
	 * @return The current ServiceExecutorFactory.
	 */
	public ServiceExecutorFactory getServiceExecutorFactory() {
		return serviceExecutorFactory;
	}

	/**
	 * Sets a new ServiceExecutorFactory.
	 * 
	 * @param serviceExecutorFactory
	 *            The current ServiceExecutorFactory.
	 */
	public void setServiceExecutorFactory(ServiceExecutorFactory serviceExecutorFactory) {
		this.serviceExecutorFactory = serviceExecutorFactory;
	}

	@Override
	public boolean checkConfiguration() {
		if (serviceDefinitionFinder == null || serviceFinder == null || resourceHandler == null
				|| serviceExecutorFactory == null || repository == null)
			return false;

		return serviceDefinitionFinder.checkConfiguration() && serviceFinder.checkConfiguration()
				&& resourceHandler.checkConfiguration() && serviceExecutorFactory.checkConfiguration()
				&& repository.checkConfiguration();
	}

	@Override
	public ServiceProcess executeProcess(ServiceProcess process) throws ProcessManagerException {

		// first we have to identify the task of the service process
		LOGGER.debug("Searching for open task in process " + process);
		ServiceTask prevTask = process.getOpenTask();
		if (prevTask == null && process.getTasks().size() > 0)
			throw new ServiceProcessFailedException("No previous task found, but " + process.getTasks().size()
					+ " tasks exists in service process.", process);
		LOGGER.debug("Identified open task as " + prevTask);

		// second we have to check if this task can be closed
		if (prevTask != null && prevTask.isCloseable()) {
			LOGGER.info("Closing task " + prevTask);
			prevTask.setStatus(ServiceTaskStatus.CLOSED);
			getRepository().saveServiceTask(prevTask);
		}

		// if the task has been closed or no one exists, there are two
		// possibilities, first an additional task within the process has to be
		// processed, or the process is finished
		boolean reexecute = false;
		if (prevTask == null || prevTask.getStatus().equals(ServiceTaskStatus.CLOSED)) {
			LOGGER.debug("Determining next task...");
			ServiceProcessDefinition spd = findServiceDefinition(process.getType(), process.getDomain());
			if (spd == null)
				throw new InvalidProcessTypeException("No ServiceProcessDefinition found for process-type "
						+ process.getType(), process);

			// no more task have to be processed
			if (prevTask != null && spd.isFinalTask(prevTask.getType())) {

				// lets check if we have to update any resources
				LOGGER.debug("Updating resources...");
				getResourceHandler().updateResources(process);

				// after the update we can release the resources, because the
				// process manager does not need them anymore
				LOGGER.debug("Releasing resources...");
				getResourceHandler().releaseResources(process);

				// that also means that the query is finished
				fireEvent(new StandardQueryStatusEvent(process.getInternalId(), process.getExternalId(),
						QueryStatus.FINISHED));
			}

			// otherwise we have to execute the next task
			else {
				TaskType nextTaskType = spd.getNextTask(prevTask == null ? null : prevTask.getType());
				LOGGER.debug("Identified next task type as " + nextTaskType);
				boolean result = executeServiceTask(nextTaskType, process);

				// if false is returned there is no further action triggered
				// within this task, hence we have to proceed with the process
				if (!result) {
					LOGGER.debug("No execution triggered by service task, proceeding with service process...");
					reexecute = true;
				}
			}
		}

		// if the task cannot be closed there is at least one subtask that have
		// to be processed
		else {

			// first we have to close the current sub task
			if (!prevTask.closeSubTask())
				throw new ServiceProcessFailedException("Open sub task expected, but no one found", process);
			getRepository().saveServiceTask(prevTask);

			// second we can execute the task, which is represented by the
			// previous task, if no further processing is required false is
			// returned
			if (!executeServiceSubTask(prevTask)) {
				LOGGER.debug("No execution triggered by service task, because no more sub tasks found. Proceeding with service process...");
				reexecute = true;
			}
		}

		// if set we have to re-execute the process, because nothing has
		// happened since the last execution
		if (reexecute) {
			LOGGER.debug("Preparing the re-execution of process...");

			process = getRepository().findServiceProcessByInternalId(process.getInternalId());
			return executeProcess(process);
		}
		return process;
	}

	@Override
	public ServiceProcessDefinition findServiceDefinition(String type, String domain) {
		return getServiceDefinitionFinder().find(type, domain);
	}

	/**
	 * This method is called by {link
	 * {@link AbstractServiceProcessExecutionControl#executeProcess(ServiceProcess)}
	 * when it has determined the TaskType which should be executed now.
	 * 
	 * @param taskType
	 *            The determined TaskType.
	 * @param process
	 *            The related ServiceProcess.
	 * @return True if the processing is successfully, false otherwise.
	 * @throws ProcessManagerException
	 *             If an internal exception occurs.
	 */
	protected boolean executeServiceTask(TaskType taskType, ServiceProcess process) throws ProcessManagerException {
		LOGGER.debug("Executing service task with name " + taskType.getName() + " in process " + process);
		ServiceTask task = new ServiceTask(process, taskType.getName());
		LOGGER.debug("Service task created successfully " + task);

		// depending on the task type we have to proceed
		if (taskType.getServiceTemplate() != null) {

			// if a service template have to be executed we just have to
			// store the task data
			getRepository().saveServiceTask(task);
			return executeServiceTemplate(process, taskType.getServiceTemplate(), null);
		} else if (taskType.getServiceProcess() != null) {

			// in case of a service process it is necessary to persist
			// the subtask process
			// first we have to verify that a service process plan exists
			ResourceRequirement rr = new ResourceRequirement();
			rr.setTypes(Resource.TYPE_OBJECT);
			rr.setProtocols(null);
			rr.setCapabilityRule("service_process");
			getRepository().saveServiceTask(task);
			@SuppressWarnings("unchecked")
			ObjectResource<Serializable> r = ObjectResource.class.cast(getResourceHandler().findResource(process, rr,
					new SystemData()));
			if (r == null || !ServiceProcessPlan.class.isInstance(r.getObject()))
				throw new ServiceProcessFailedException("Resource requirement " + rr + " could not be satisfied",
						process);
			ServiceProcessPlan plan = ServiceProcessPlan.class.cast(r.getObject());

			// second we persist the plan
			LOGGER.debug("Extending task " + task + " using " + plan);
			getRepository().extendTaskByServiceProcessPlan(task, plan);
			return executeServiceSubTask(task);
		} else
			throw new ServiceProcessFailedException("Unsupported task type found", process);
	}

	/**
	 * This method is called by {link
	 * {@link AbstractServiceProcessExecutionControl#executeServiceTask(TaskType, ServiceProcess)}
	 * when it has determined the ServiceTask which is related to the processed
	 * TaskType.
	 * 
	 * @param task
	 *            The determined ServiceTask
	 * @return True if the processing is successfully, false otherwise.
	 * @throws ProcessManagerException
	 *             If an internal exception occurs.
	 */
	protected boolean executeServiceSubTask(ServiceTask task) throws ProcessManagerException {

		// first lets identify the next task and open it
		LOGGER.debug("Executing next service sub task in task " + task);
		ServiceSubTask subTask = task.getNextOpenSubTask();
		if (subTask == null)
			return false;
		subTask.setStatus(ServiceSubTaskStatus.OPEN);
		getRepository().saveServiceTask(task);

		// second we can execute the service template represented by this
		// subtask
		ServiceTemplateType serviceTemplateType = new ServiceTemplateType();
		serviceTemplateType.setCategory(subTask.getCategory());
		serviceTemplateType.setSubcategory(subTask.getSubCategory());

		// now we have to build the service input
		LOGGER.debug("Preparing preset service input...");
		ServiceInput si = new StandardServiceInput();
		for (ServiceSubTaskProperty property : subTask.getProperties()) {
			ObjectResource<String> or = new ObjectResource<String>(property.getValue());
			or.setCapability(new StringResourceCapability(property.getName()));
			LOGGER.debug("Adding resource to input: " + or);
			si.add(property.getName(), or);
		}
		return executeServiceTemplate(task.getProcess(), serviceTemplateType, si);
	}

	/**
	 * Executes a service template, by searching for a matching service and the
	 * needed resources. If resources should be provided, a service input can be
	 * set as parameter. In such a case these resources are used if the satisfy
	 * the requirements. Otherwise the resource handler is requested.
	 * 
	 * @param process
	 *            Concrete service process where the concrete service will be
	 *            executed
	 * @param serviceTemplateType
	 *            Service template describing the requirements of the service
	 * @param input
	 *            Service input which can be used to set initial input data, if
	 *            no initialization is required the parameter should be null
	 * @throws ProcessManagerException
	 *             If either a resource requirement cannot be fulfilled or no
	 *             service can be identified that fulfills the given
	 *             requirements
	 */
	protected boolean executeServiceTemplate(ServiceProcess process, ServiceTemplateType serviceTemplateType,
			ServiceInput input) throws ProcessManagerException {

		// first we have to search for the service, therefore we check if a REL
		// is given
		String subcategory = serviceTemplateType.getSubcategory();
		if (subcategory != null) {
			subcategory = getResourceHandler().replaceRELExpression(process, subcategory);
			if (subcategory == null) {
				LOGGER.error("Failed to resolve REL " + serviceTemplateType.getSubcategory());
				throw new ServiceProcessFailedException("Failed to resolve REL expression", process);
			}
		}
		LOGGER.debug("Searching for service fulfilling " + serviceTemplateType.getCategory() + "|" + subcategory);
		ServiceCapabilityRule cscr = new CategoryServiceCapabilityRule(serviceTemplateType.getCategory(), subcategory);
		ServiceRegistration sr = getServiceFinder().find(cscr);
		if (sr == null)
			throw new ServiceProcessFailedException("No matching service for " + cscr + " found", process);
		LOGGER.debug("Found service registration " + sr);

		// second we can build the service input using the resource requirements
		LOGGER.debug("Building service input...");
		ServiceInput si = input;
		if (si == null)
			si = new StandardServiceInput();
		for (ResourceRequirement rr : sr.getServiceDefinition().getCondition().getResourceRequirement()) {

			// we have to check if a matching resource is already added to the
			// service input
			if (!si.contains(rr.getCapabilityRule())) {

				// search for a matching resource, if no resource can be found
				// throw an exception
				LOGGER.debug("Searching for resource " + rr);
				Resource r = getResourceHandler().findResource(process, rr, sr.getSystemData());
				LOGGER.debug("Resource identified " + r);
				if (r == null)
					throw new ServiceProcessFailedException("Resource requirement " + rr + " could not be satisfied",
							process);
				LOGGER.debug("Adding " + r + " for " + rr.getCapabilityRule());
				si.add(rr.getCapabilityRule(), r);
			}
		}
		LOGGER.debug("Service input successfully generated...");

		// after everything is done we can execute the service
		getServiceExecutorFactory().execute(process.getInternalId(), sr, si);
		LOGGER.debug("Service successfully triggered...");
		return true;
	}

	@Override
	public void handleResult(ServiceResultData data) throws ProcessManagerException {

		// we search for the service represented by the result data
		LOGGER.debug("Checking existance of service process " + data.getCorrelationId());
		ServiceProcess process = getRepository().findServiceProcessByInternalId(data.getCorrelationId());
		if (process == null) {
			throw new NullPointerException("Data process with id " + data.getCorrelationId()
					+ " does not exists. Processing impossible.");
		}

		// now we check the result of the service
		if (data.failed()) {
			ErrorServiceResult errorResult = ErrorServiceResult.class.cast(data.getResult());

			// we have to update the status of the related entities
			handleErrorResult(process);

			// and release all resources
			getResourceHandler().releaseResources(process);

			// now we can communicate the error
			throw new ServiceProcessFailedException(errorResult.getErrorMessage(), process);
		}
		StandardServiceResult standardResult = StandardServiceResult.class.cast(data.getResult());
		getResourceHandler().registerResources(process, standardResult);

		// and finally we can proceed with the process
		process = executeProcess(process);
		if (!process.hasOpenTask())
			handleServiceProcessResult(process);
	}

}
