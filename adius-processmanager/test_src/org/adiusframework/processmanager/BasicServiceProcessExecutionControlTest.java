package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.ServiceSubTask;
import org.adiusframework.processmanager.domain.ServiceSubTaskProperty;
import org.adiusframework.processmanager.domain.ServiceSubTaskStatus;
import org.adiusframework.processmanager.domain.ServiceTask;
import org.adiusframework.processmanager.domain.ServiceTaskStatus;
import org.adiusframework.processmanager.domain.SimulationProcess;
import org.adiusframework.processmanager.domain.SimulationStep;
import org.adiusframework.processmanager.domain.SimulationStepStatus;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.processmanager.exception.ServiceProcessAccessException;
import org.adiusframework.processmanager.exception.ServiceProcessFailedException;
import org.adiusframework.processmanager.xml.ServiceProcessType;
import org.adiusframework.processmanager.xml.ServiceTemplateType;
import org.adiusframework.processmanager.xml.TaskType;
import org.adiusframework.resource.ObjectResource;
import org.adiusframework.resource.Resource;
import org.adiusframework.resource.StringResourceCapability;
import org.adiusframework.resource.serviceplan.ServiceProcessPlan;
import org.adiusframework.service.CategoryServiceCapabilityRule;
import org.adiusframework.service.ErrorServiceResult;
import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.service.StandardServiceInput;
import org.adiusframework.service.StandardServiceResult;
import org.adiusframework.service.xml.Category;
import org.adiusframework.service.xml.ResourceRequirement;
import org.adiusframework.service.xml.ResourceRequirementList;
import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceregistry.ServiceFinder;
import org.adiusframework.util.datastructures.SystemData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractServiceProcessExecutionControl.class, Category.class })
public class BasicServiceProcessExecutionControlTest {
	// Some test-constants to avoid miss-spellings in the test-code
	private static final String TEST_INTERNAL_ID = "testId";
	private static final String TEST_PROCESS_TYPE = "testProcessType";
	private static final String OPEN_TASK_TYPE = "testTaskType";
	private static final String NEXT_TASK_TYPE_NAME = "testTaskTypeName";
	private static final String CAPABILITY_RULE = "service_process";
	private static final String TEST_SUB_CATEGORY = "testSubCategory";
	private static final String SUB_CATEGORY_REPLACED = "testSubCategory_Replaced";
	private static final String TEST_PROPERTY = "testProeprty";
	private static final String TEST_VALUE = "testValue";
	private static final String TEST_ERROR_MESSAGE = "Failed because of test reason!";

	// Related object which's method could be called by the
	// AbstractServiceProcessExecutionControl during the test
	private ProcessManagerRepository repository;
	private ServiceProcessDefinitionFinder definitionFinder;
	private ResourceHandler handler;
	private ServiceFinder serviceFinder;
	private ServiceExecutorFactory factory;

	// Dummy objects which doesn't need implemented behavior
	private SimulationProcess simulationProcess;
	private ServiceProcess dummyProcess;
	Category category;

	// "Data"-object which are processed by the
	// AbstractServiceProcessExecutionControl
	private ServiceTask openTask;
	private Map<Integer, ServiceTask> tasks;
	ServiceProcess process;
	private ServiceTemplateType templateType;
	private TaskType nextTaskType;
	private ServiceProcessDefinition definition;
	private SimulationStep step;

	// Test tested object
	private AbstractServiceProcessExecutionControl control;

	@Before
	public void init() throws ProcessManagerException {
		// Create dummy-objects
		simulationProcess = mock(SimulationProcess.class);
		dummyProcess = mock(ServiceProcess.class);
		category = mock(Category.class);

		// Create and initialize the data-object with mocking and stubbing
		step = mock(SimulationStep.class);
		when(step.getStatus()).thenReturn(SimulationStepStatus.UNPROCESSED);
		when(step.getProcess()).thenReturn(simulationProcess);

		openTask = mock(ServiceTask.class);
		when(openTask.getType()).thenReturn(OPEN_TASK_TYPE);

		tasks = new HashMap<Integer, ServiceTask>();
		tasks.put(1, openTask);

		process = mock(ServiceProcess.class);
		when(process.getInternalId()).thenReturn(TEST_INTERNAL_ID);
		when(process.getOpenTask()).thenReturn(openTask);
		when(process.getTasks()).thenReturn(tasks);
		when(process.getType()).thenReturn(TEST_PROCESS_TYPE);

		when(process.getStep()).thenReturn(step);
		when(openTask.getProcess()).thenReturn(process);

		templateType = mock(ServiceTemplateType.class);
		when(templateType.getCategory()).thenReturn(category);
		when(templateType.getSubcategory()).thenReturn(TEST_SUB_CATEGORY);

		nextTaskType = mock(TaskType.class);
		when(nextTaskType.getName()).thenReturn(NEXT_TASK_TYPE_NAME);

		definition = mock(ServiceProcessDefinition.class);
		when(definition.getNextTask(OPEN_TASK_TYPE)).thenReturn(nextTaskType);

		// Create the related objects
		repository = mock(ProcessManagerRepository.class);
		definitionFinder = mock(ServiceProcessDefinitionFinder.class);
		when(definitionFinder.find(TEST_PROCESS_TYPE)).thenReturn(definition);
		handler = mock(ResourceHandler.class);

		// Create and initialize the tested object
		control = spy(new AbstractServiceProcessExecutionControl());
		control.setRepository(repository);
		control.setServiceDefinitionFinder(definitionFinder);
		control.setResourceHandler(handler);
		doNothing().when(control).fireEvent(any(QueryStatusEvent.class));
		doReturn(dummyProcess).when(control).executeProcess(dummyProcess);
	}

	@Test
	public void testExecuteProcessCloseableFinal() throws ProcessManagerException {
		// Define test-specific mock-behavior
		when(openTask.isCloseable()).thenReturn(true);
		when(openTask.getStatus()).thenReturn(ServiceTaskStatus.CLOSED);
		when(definition.isFinalTask(OPEN_TASK_TYPE)).thenReturn(true);

		// Call tested method and verify the result
		assertEquals(process, control.executeProcess(process));

		// Verify if the related object are used as we want to
		verify(openTask, times(1)).setStatus(ServiceTaskStatus.CLOSED);
		verify(repository, times(1)).saveServiceTask(openTask);
		verify(handler, times(1)).updateResources(process);
		verify(handler, times(1)).releaseResources(process);

		// Verify if a StandardQueryStatusEvent with status equal "finished" was
		// fired
		verify(control, times(1)).fireEvent(argThat(new ArgumentMatcher<StandardQueryStatusEvent>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof StandardQueryStatusEvent) {
					StandardQueryStatusEvent event = (StandardQueryStatusEvent) argument;

					return event.getStatus().equals(QueryStatus.FINISHED);
				}
				return false;
			}

		}));
	}

	@Test
	public void testExecuteProcessCloseableNotFinal() throws ProcessManagerException {
		// Define test-specific mock-behavior
		when(openTask.isCloseable()).thenReturn(true);
		when(openTask.getStatus()).thenReturn(ServiceTaskStatus.CLOSED);
		when(definition.isFinalTask(OPEN_TASK_TYPE)).thenReturn(false);
		doReturn(true).when(control).executeServiceTask(nextTaskType, process);

		// Call tested method and verify the result
		assertEquals(process, control.executeProcess(process));

		// Verify if the related object are used as we want to
		verify(openTask, times(1)).setStatus(ServiceTaskStatus.CLOSED);
		verify(repository, times(1)).saveServiceTask(openTask);
	}

	@Test
	public void testExecuteProcessCloseableNotFinalReexecute() throws ProcessManagerException {
		// Define test-specific mock-behavior
		when(openTask.isCloseable()).thenReturn(true);
		when(openTask.getStatus()).thenReturn(ServiceTaskStatus.CLOSED);
		when(definition.isFinalTask(OPEN_TASK_TYPE)).thenReturn(false);
		when(repository.findServiceProcessByInternalId(TEST_INTERNAL_ID)).thenReturn(dummyProcess);
		doReturn(false).when(control).executeServiceTask(nextTaskType, process);

		// Call tested method and verify the result
		assertEquals(dummyProcess, control.executeProcess(process));

		// Verify if the related object are used as we want to
		verify(openTask, times(1)).setStatus(ServiceTaskStatus.CLOSED);
		verify(repository, times(1)).saveServiceTask(openTask);
		verify(control, times(1)).executeServiceTask(nextTaskType, process);
		verify(control, times(1)).executeProcess(dummyProcess);
	}

	@Test
	public void testExecuteProcessUncloseable() throws ProcessManagerException {
		// Define test-specific mock-behavior
		when(openTask.isCloseable()).thenReturn(false);
		when(openTask.getStatus()).thenReturn(ServiceTaskStatus.OPEN);
		when(openTask.closeSubTask()).thenReturn(true);
		doReturn(true).when(control).executeServiceSubTask(openTask);

		// Call tested method and verify the result
		assertEquals(process, control.executeProcess(process));

		// Verify if the related object are used as we want to
		verify(openTask, times(1)).closeSubTask();
		verify(repository, times(1)).saveServiceTask(openTask);
	}

	@Test
	public void testExecuteProcessUncloseableNoOpenSubTasks() throws ProcessManagerException {
		// Define test-specific mock-behavior
		when(openTask.isCloseable()).thenReturn(false);
		when(openTask.getStatus()).thenReturn(ServiceTaskStatus.OPEN);
		when(openTask.closeSubTask()).thenReturn(false);

		// The execution should end with a ServiceProcessFailedException
		try {
			control.executeProcess(process);
			fail();
		} catch (ServiceProcessFailedException exception) {
			// hence we are lucky if the exception occurs
		}

		// No interaction must be verified
	}

	@Test
	public void testExecuteProcessNoPreviousTask() throws ProcessManagerException {
		// Define test-specific mock-behavior
		when(process.getOpenTask()).thenReturn(null);

		// The execution should end with a ServiceProcessFailedException
		try {
			control.executeProcess(process);
			fail();
		} catch (ServiceProcessFailedException exception) {
			// hence we are lucky if the exception occurs
		}

		// No interaction must be verified
	}

	@Test
	public void testExecuteServiceTaskTemplate() throws ProcessManagerException {
		// Define test-specific mock-behavior
		when(nextTaskType.getServiceTemplate()).thenReturn(templateType);
		doReturn(true).when(control).executeServiceTemplate(process, templateType, null);

		// Call tested method and verify the result
		assertTrue(control.executeServiceTask(nextTaskType, process));

		// Verify if the ServiceTemplate was executed and if the next task was
		// saved in the repository
		verify(control, times(1)).executeServiceTemplate(process, templateType, null);
		verify(repository, times(1)).saveServiceTask(argThat(new ArgumentMatcher<ServiceTask>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof ServiceTask) {
					ServiceTask task = (ServiceTask) argument;

					return task.getProcess().equals(process) && task.getType().equals(NEXT_TASK_TYPE_NAME);
				}
				return false;
			}
		}));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExecuteServiceTaskProcess() throws Exception {
		// Create a ServiceTask-mock with is injected if the tested method
		// create a new instance of this class
		ServiceTask task = mock(ServiceTask.class);
		PowerMockito.whenNew(ServiceTask.class).withArguments(process, NEXT_TASK_TYPE_NAME).thenReturn(task);
		doReturn(true).when(control).executeServiceSubTask(task);

		// Create some test-specific mocks and connect them by stubbing certain
		// methods
		ServiceProcessType processType = mock(ServiceProcessType.class);
		when(nextTaskType.getServiceProcess()).thenReturn(processType);
		ServiceProcessPlan plan = mock(ServiceProcessPlan.class);
		ObjectResource<ServiceProcessPlan> resource = mock(ObjectResource.class);
		when(resource.getObject()).thenReturn(plan);
		when(handler.findResource(eq(process), argThat(new ArgumentMatcher<ResourceRequirement>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof ResourceRequirement) {
					ResourceRequirement requirement = (ResourceRequirement) argument;

					return requirement.getTypes().equals(Resource.TYPE_OBJECT) && requirement.getProtocols() == null
							&& requirement.getCapabilityRule().equals(CAPABILITY_RULE);
				}
				return false;
			}

		}), eq(new SystemData()))).thenReturn(resource);

		// Call tested method and verify the result
		assertTrue(control.executeServiceTask(nextTaskType, process));

		// Verify if the related object are used as we want to
		verify(repository, times(1)).saveServiceTask(task);
		verify(repository, times(1)).extendTaskByServiceProcessPlan(task, plan);
		verify(control, times(1)).executeServiceSubTask(task);
	}

	@Test
	public void testExecuteServiceTaskException() throws ProcessManagerException {
		// The execution should end in a ServiceProcessFailedException
		try {
			control.executeServiceTask(nextTaskType, process);
			fail();
		} catch (ServiceProcessFailedException exception) {
			// hence we are lucky if the exception occurs
		}
	}

	@Test
	public void testExecuteServiceSubTask() throws ProcessManagerException {
		// Create some test-specific mocks and connect them by stubbing certain
		// methods
		ServiceSubTaskProperty property = mock(ServiceSubTaskProperty.class);
		when(property.getName()).thenReturn(TEST_PROPERTY);
		when(property.getValue()).thenReturn(TEST_VALUE);
		Set<ServiceSubTaskProperty> properties = new HashSet<ServiceSubTaskProperty>();
		properties.add(property);
		ServiceSubTask nextSubTask = mock(ServiceSubTask.class);
		when(nextSubTask.getCategory()).thenReturn(category);
		when(nextSubTask.getSubCategory()).thenReturn(TEST_SUB_CATEGORY);
		when(nextSubTask.getProperties()).thenReturn(properties);
		when(openTask.getNextOpenSubTask()).thenReturn(nextSubTask);
		doReturn(true).when(control).executeServiceTemplate(eq(process), any(ServiceTemplateType.class),
				any(ServiceInput.class));

		// Call tested method and verify the result
		assertTrue(control.executeServiceSubTask(openTask));

		verify(nextSubTask, times(1)).setStatus(ServiceSubTaskStatus.OPEN);
		verify(repository, times(1)).saveServiceTask(openTask);
		// Verify if the ServiceTemplate and the ServiceInput was created
		// correct
		verify(control, times(1)).executeServiceTemplate(eq(process),
				argThat(new ArgumentMatcher<ServiceTemplateType>() {
					
					@Override
					public boolean matches(Object argument) {
						if (argument instanceof ServiceTemplateType) {
							ServiceTemplateType templateType = (ServiceTemplateType) argument;

							return templateType.getCategory().equals(category)
									&& templateType.getSubcategory().equals(TEST_SUB_CATEGORY);
						}
						return false;
					}

				}), argThat(new ArgumentMatcher<ServiceInput>() {
					
					@Override
					public boolean matches(Object argument) {
						if (argument instanceof StandardServiceInput) {
							StandardServiceInput input = (StandardServiceInput) argument;

							if (input.getKeys().size() == 1 && input.getKeys().iterator().next().equals(TEST_PROPERTY)) {
								Resource resource = input.get(TEST_PROPERTY);

								if (resource instanceof ObjectResource) {
									ObjectResource<?> objResource = ObjectResource.class.cast(resource);

									return objResource.getObject().equals(TEST_VALUE)
											&& objResource.getCapability().equals(
													new StringResourceCapability(TEST_PROPERTY));
								}
							}
						}
						return false;
					}

				}));
	}

	@Test
	public void testExecuteServiceTemplate() throws ProcessManagerException {
		// Create a dummy-object
		Resource resource = mock(Resource.class);

		// Create test-specific data-objects and connect them by stubbing
		// certain methods
		ServiceInput input = mock(ServiceInput.class);
		when(input.contains(CAPABILITY_RULE)).thenReturn(false);

		ResourceRequirement requirement = mock(ResourceRequirement.class);
		when(requirement.getCapabilityRule()).thenReturn(CAPABILITY_RULE);

		List<ResourceRequirement> list = new LinkedList<ResourceRequirement>();
		list.add(requirement);

		ResourceRequirementList requirementList = mock(ResourceRequirementList.class);
		when(requirementList.getResourceRequirement()).thenReturn(list);

		ServiceDefinition definition = mock(ServiceDefinition.class);
		when(definition.getCondition()).thenReturn(requirementList);

		ServiceRegistration registration = mock(ServiceRegistration.class);
		when(registration.getServiceDefinition()).thenReturn(definition);

		// Create test-specific related objects and stub their behavior
		serviceFinder = mock(ServiceFinder.class);
		when(serviceFinder.find(argThat(new ArgumentMatcher<CategoryServiceCapabilityRule>() {
			
			@Override
			public boolean matches(Object argument) {
				if (argument instanceof CategoryServiceCapabilityRule) {
					CategoryServiceCapabilityRule rule = (CategoryServiceCapabilityRule) argument;

					return rule.getCategory().equals(category) && rule.getSubcategory().equals(SUB_CATEGORY_REPLACED);
				}
				return false;
			}
		}))).thenReturn(registration);
		factory = mock(ServiceExecutorFactory.class);
		doNothing().when(factory).execute(TEST_INTERNAL_ID, registration, input);
		// Define the behavior of the related object with the new mocks
		when(handler.findResource(process, requirement, registration.getSystemData())).thenReturn(resource);
		when(handler.replaceRELExpression(process, TEST_SUB_CATEGORY)).thenReturn(SUB_CATEGORY_REPLACED);

		// Inject the new related object into our test-object
		control.setServiceFinder(serviceFinder);
		control.setServiceExecutorFactory(factory);

		// Call tested method and verify the result
		assertTrue(control.executeServiceTemplate(process, templateType, input));

		// Verify if the related object are used as we want to
		verify(input, times(1)).add(CAPABILITY_RULE, resource);
		verify(factory, times(1)).execute(TEST_INTERNAL_ID, registration, input);
	}

	@Test
	public void testExecuteServiceTemplateException() {
		// The execution should end in a ServiceProcessFailedException
		try {
			assertTrue(control.executeServiceTemplate(process, templateType, null));
			fail();
		} catch (ProcessManagerException exception) {
			// hence we are lucky if the exception occurs
		}
	}

	@Test
	public void testHandleResult() throws ProcessManagerException {
		// Set up some specific mocks and their behavior
		StandardServiceResult result = mock(StandardServiceResult.class);

		ServiceResultData data = mock(ServiceResultData.class);
		when(data.getResult()).thenReturn(result);
		when(data.getCorrelationId()).thenReturn(TEST_INTERNAL_ID);
		when(data.failed()).thenReturn(false);

		doReturn(process).when(control).executeProcess(process);
		when(process.hasOpenTask()).thenReturn(false);
		when(repository.findServiceProcessByInternalId(TEST_INTERNAL_ID)).thenReturn(process);

		// Execute the tested method
		control.handleResult(data);

		// Verify if the related object are used as we want to
		verify(handler, times(1)).registerResources(process, result);
		verify(control, times(1)).executeProcess(process);
		verify(step, times(1)).setStatus(SimulationStepStatus.PROCESSED);
		verify(repository, times(1)).updateSimulationProcess(simulationProcess);
	}

	@Test
	public void testHandleResultFailed() throws ProcessManagerException {
		// Set up some specific mocks and their behavior
		ErrorServiceResult result = mock(ErrorServiceResult.class);
		when(result.getErrorMessage()).thenReturn(TEST_ERROR_MESSAGE);

		ServiceResultData data = mock(ServiceResultData.class);
		when(data.getResult()).thenReturn(result);
		when(data.getCorrelationId()).thenReturn(TEST_INTERNAL_ID);
		when(data.failed()).thenReturn(true);

		when(repository.findServiceProcessByInternalId(TEST_INTERNAL_ID)).thenReturn(process);

		// The execution should end in a ServiceProcessFailedExeception
		try {
			control.handleResult(data);
			fail();
		} catch (ServiceProcessFailedException exception) {
			assertEquals(TEST_ERROR_MESSAGE, exception.getMessage());
			assertEquals(process, exception.getServiceProcess());
		}

		// Verify if the failure of the ServiceProcess is processed correct
		verify(step, times(1)).setStatus(SimulationStepStatus.ERROR);
		verify(repository, times(1)).updateSimulationProcess(simulationProcess);
		verify(handler, times(1)).releaseResources(process);
	}

	@Test
	public void testHandleResultWrongCorrelation() throws ProcessManagerException {
		ServiceResultData data = mock(ServiceResultData.class);
		when(data.getCorrelationId()).thenReturn("");

		// The execution should end in a ServiceProcessAccessException
		try {
			control.handleResult(data);
			fail();
		} catch (ServiceProcessAccessException exception) {
			// hence we are lucky if the exception occurs
		}
	}
}
