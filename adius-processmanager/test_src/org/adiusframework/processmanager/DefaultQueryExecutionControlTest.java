package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.SimulationProcess;
import org.adiusframework.processmanager.domain.SimulationProcessStatus;
import org.adiusframework.processmanager.domain.SimulationStep;
import org.adiusframework.processmanager.domain.SimulationStepStatus;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.processmanager.exception.SimulationProcessAccessException;
import org.adiusframework.processmanager.exception.SimulationProcessCorruptedException;
import org.adiusframework.query.Query;
import org.adiusframework.query.QueryAspect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

public class DefaultQueryExecutionControlTest {
	protected static final String TEST_PROCESS_ID = "testQueryId";
	protected static final String TEST_INTERNAL_SERVICE_ID = "testServiceId-INTERNAL";
	protected static final String TEST_EXTERNAL_SERVICE_ID = "testServiceId-EXTERNAL";
	protected static final String TEST_STEP_ID = "testStepId";
	protected static final String TEST_DOMAIN = "testDomain";
	
	// Related objects, which's methods could by called by the AbstractQueryExecutionControl
	protected ProcessManagerRepository repository;
	protected ResourceHandler handler;
	protected ServiceProcessExecutionControl executionControl;
	
	// "Data"-object Mocks
	protected Query query;
	protected SimulationStep step;
	protected SimulationProcess simulationProcess;
	protected ServiceProcess serviceProcess;
	
	// The tested AbstractQueryExecutionControl
	protected AbstractQueryExecutionControl control;
	
	@Before
	public void init() throws ProcessManagerException {
		// Create a AbstractQueryExecutionControl-test-object and initialize it
		AbstractQueryExecutionControl control = spy(new AbstractQueryExecutionControl());
		init(control);
	}
	
	// Initializes the test for a specific QueryExecutionControl
	protected void init(AbstractQueryExecutionControl control) throws ProcessManagerException {
		// Create and initialize data-mocks which are processed by the AbstractQueryExecutionControl
		query = mock(Query.class);
		when(query.getProcessIdentifier()).thenReturn(TEST_PROCESS_ID);
		when(query.getStepIdentifier()).thenReturn(TEST_STEP_ID);
		when(query.getDomain()).thenReturn(TEST_DOMAIN);
		step = mock(SimulationStep.class);
		when(step.getStatus()).thenReturn(SimulationStepStatus.PROCESSED);
		simulationProcess = mock(SimulationProcess.class);
		when(simulationProcess.getStepByIdentifier(TEST_STEP_ID)).thenReturn(step);
		serviceProcess = mock(ServiceProcess.class);
		when(serviceProcess.getInternalId()).thenReturn(TEST_INTERNAL_SERVICE_ID);
		when(serviceProcess.getExternalId()).thenReturn(TEST_EXTERNAL_SERVICE_ID);
		
		// Create the related objects and stub they needed behavior
		repository = mock(ProcessManagerRepository.class);
		when(repository.createServiceProcess(step, query)).thenReturn(serviceProcess);
		when(repository.findSimulationProcessByIdentifier(TEST_PROCESS_ID)).thenReturn(simulationProcess);
		when(repository.createSimulationProcess(TEST_PROCESS_ID, TEST_STEP_ID, TEST_DOMAIN)).thenReturn(simulationProcess);
		handler = mock(ResourceHandler.class);
		executionControl = mock(ServiceProcessExecutionControl.class);
		when(executionControl.executeProcess(serviceProcess)).thenReturn(serviceProcess);

		// Initialize and inject the test control
		control.setRepository(repository);
		control.setResourceHandler(handler);
		control.setServiceProcessExecutionControl(executionControl);
		doNothing().when(control).fireEvent(any(QueryStatusEvent.class));
		
		this.control = control;
	}
	
	@Test
	public void testHandleQuery() throws ProcessManagerException {
		// Define test-specific mock-behavior
		doReturn(simulationProcess).when(control).identifySimulationProcess(query);
		doReturn(step).when(control).identifySimulationStep(simulationProcess, query);
		
		// Call tested method and verify the result
		assertEquals(TEST_INTERNAL_SERVICE_ID, control.handleQuery(query));
		
		// Verify if the related object are used as we want to
		verify(handler, times(1)).registerResources(serviceProcess, query);
		verify(control, times(1)).executeServiceProcess(serviceProcess);
		
		// Verify if a StandardQueryStatusEvent with the proper parameters was fired
		verify(control, times(1)).fireEvent(argThat(new ArgumentMatcher<QueryStatusEvent>() {
			
			@Override
			public boolean matches(Object argument) {
				if(argument instanceof QueryStatusEvent) {
					QueryStatusEvent event = (QueryStatusEvent)argument;
					
					return event.getInternalId().equals(TEST_INTERNAL_SERVICE_ID) && 
							event.getExternalId().equals(TEST_EXTERNAL_SERVICE_ID) && event.getStatus().equals(QueryStatus.EXECUTION);
				}
				return false;
			}
		}));
	}
	
	@Test
	public void testIdentifySimulationProcess() throws SimulationProcessCorruptedException {
		when(simulationProcess.getStatus()).thenReturn(SimulationProcessStatus.PROCESSED);
		
		assertEquals(simulationProcess, control.identifySimulationProcess(query));
	}
	
	@Test
	public void testIdentifySimulationProcessException() {
		when(simulationProcess.getStatus()).thenReturn(SimulationProcessStatus.ERROR);
		
		// The execution should end in a SimulationProcessCorruptedException
		try {
			control.identifySimulationProcess(query);
			fail();
		} catch(SimulationProcessCorruptedException exception) {
			// hence we are lucky if the exception occurs
		}
	}
	
	@Test
	public void testIdentifySimulationStepException() {
		when(query.hasAspect(QueryAspect.DATA_GENERATOR)).thenReturn(false);
		when(query.hasAspect(QueryAspect.DATA_ACCESSOR)).thenReturn(false);
		
		// The execution should end in a ProcessManagerException
		try {
			control.identifySimulationStep(simulationProcess, query);
			fail();
		} catch(ProcessManagerException exception) {
			// hence we are lucky if the exception occurs
		}
	}
	
	@Test
	public void testHandleDataGeneratorQuery() throws SimulationProcessAccessException {
		assertEquals(step, control.handleDataGeneratorQuery(null, query));
	}
	
	@Test
	public void testHandleDataGeneratorQueryException() throws SimulationProcessAccessException {
		when(simulationProcess.getStepByIdentifier(TEST_STEP_ID)).thenReturn(null);
		
		// The execution should end in a SimulationProcessCorruptedException
		try {
			control.handleDataGeneratorQuery(null, query);
			fail();
		} catch(SimulationProcessAccessException exception) {
			// hence we are lucky if the exception occurs
		}
	}
	
	@Test
	public void testHandleDataAccessorQuery() throws SimulationProcessAccessException {
		assertEquals(step, control.handleDataAccessorQuery(simulationProcess, query));
	}
	
	@Test
	public void testHandleAccessorQueryException() {
		when(simulationProcess.getStepByIdentifier(TEST_STEP_ID)).thenReturn(null);
		
		// The execution should end in a SimulationProcessCorruptedException
		try {
			control.handleDataAccessorQuery(simulationProcess, query);
			fail();
		} catch(SimulationProcessAccessException exception) {
			// hence we are lucky if the exception occurs
		}
	}
	
	@Test
	public void testHandleDataAccessorQueryUnprocessed() throws SimulationProcessAccessException {
		when(step.getStatus()).thenReturn(SimulationStepStatus.UNPROCESSED);
	
		// The execution should end in a SimulationProcessCorruptedException
		try {
			control.handleDataAccessorQuery(simulationProcess, query);
			fail();
		} catch(SimulationProcessAccessException exception) {
			// hence we are lucky if the exception occurs
		}
	}
}
