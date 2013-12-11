package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;

import org.adiusframework.processmanager.AbstractQueueQueryExecutionControl.ServiceProcessQueue;
import org.adiusframework.processmanager.AbstractQueueQueryExecutionControl.ServiceProcessQueue.DelayedServiceProcess;
import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.SimulationStep;
import org.adiusframework.processmanager.domain.SimulationStepStatus;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.processmanager.exception.ServiceProcessFailedException;
import org.adiusframework.processmanager.exception.SimulationProcessAccessException;
import org.adiusframework.query.ExtractionQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

public class QueueQueryExecutionControlTest extends DefaultQueryExecutionControlTest {
	private static final String TEST_PROCESS_TYPE = "testType";
	private static final String TEST_EXCEPTION = "Exception because of test reason";

	private static final int TEST_COUNT = 1;
	private static final int TEST_MAX_COUNT = 2;

	// The object which are needed to test the QueueQueryExecutioNControl
	private ServiceProcessQueue queue;
	private SimulationStep simulationStep;

	// The new test-object
	private AbstractQueueQueryExecutionControl control;

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void init() throws ProcessManagerException {
		// Create the test-object and initialize it in the context of the
		// "super"-test
		control = spy(new AbstractQueueQueryExecutionControl());
		init(control);

		// Create the mocks which are needed in the extended tests
		queue = mock(ServiceProcessQueue.class);
		simulationStep = mock(SimulationStep.class);
		when(serviceProcess.getStep()).thenReturn(simulationStep);
		when(serviceProcess.getType()).thenReturn(TEST_PROCESS_TYPE);

		// Create and initialize the related-object
		Class queryClass = ExtractionQuery.class;
		QueryTypeMapper mapper = mock(QueryTypeMapper.class);
		when(mapper.getQueryByType(TEST_PROCESS_TYPE)).thenReturn(queryClass);

		// Make extend-specific configuration of the test-object
		control.setRetryCount(TEST_MAX_COUNT);
		control.setQueue(queue);
		control.setQueryTypeMapper(mapper);
	}

	// Override because the tested-object must be reconfigured in order to run
	// this test
	@Override
	@Test
	public void testHandleQuery() throws ProcessManagerException {
		doReturn(serviceProcess).when(control).executeServiceProcess(serviceProcess);

		super.testHandleQuery();
	}

	// Override because this method call should not end in an Exception
	@Override
	@Test
	public void testHandleDataAccessorQueryUnprocessed() throws SimulationProcessAccessException {
		when(simulationStep.getStatus()).thenReturn(SimulationStepStatus.UNPROCESSED);

		assertEquals(step, control.handleDataAccessorQuery(simulationProcess, query));
	}

	@Test
	public void testExecuteServiceProcessProcessed() throws ProcessManagerException {
		when(simulationStep.getStatus()).thenReturn(SimulationStepStatus.PROCESSED);

		assertEquals(serviceProcess, control.executeServiceProcess(serviceProcess));

		verify(repository, times(1)).refreshServiceProcess(serviceProcess);
	}

	@Test
	public void testExecuteServiceProcessUnprocessed() throws ProcessManagerException {
		when(simulationStep.getStatus()).thenReturn(SimulationStepStatus.UNPROCESSED);
		doNothing().when(control).queueServiceProcess(serviceProcess);

		assertEquals(serviceProcess, control.executeServiceProcess(serviceProcess));

		verify(repository, times(1)).refreshServiceProcess(serviceProcess);
		verify(control, times(1)).queueServiceProcess(serviceProcess);
	}

	@Test
	public void testExecuteServiceProcessError() throws ProcessManagerException {
		when(simulationStep.getStatus()).thenReturn(SimulationStepStatus.ERROR);

		// The execution should end in a ServiceProcessFailesException
		try {
			control.executeServiceProcess(serviceProcess);
			fail();
		} catch (ServiceProcessFailedException exception) {
			// hence we are lucky if the exception occurs
		}

		verify(repository, times(1)).refreshServiceProcess(serviceProcess);
	}

	@Test
	public void testExecutedQueuedServiceProcessException() throws ProcessManagerException {
		// Create an Exception which is thrown when the serviceProcess should be
		// executed
		ProcessManagerException exception = mock(ProcessManagerException.class);
		when(exception.getMessage()).thenReturn(TEST_EXCEPTION);
		doThrow(exception).when(control).executeServiceProcess(serviceProcess);

		// Call the test method
		control.executedQueuedServiceProcess(serviceProcess);

		// Verify that a QueryStatusEvent with the correct parameters
		// (exception) was fired
		verify(control, times(1)).fireEvent(argThat(new ArgumentMatcher<QueryStatusEvent>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof QueryStatusEvent) {
					QueryStatusEvent event = (QueryStatusEvent) argument;

					return event.getInternalId().equals(TEST_INTERNAL_SERVICE_ID)
							&& event.getExternalId().equals(TEST_EXTERNAL_SERVICE_ID)
							&& event.getStatus().equals(QueryStatus.QUEUEFAILED)
							&& event.getAttachment().equals(TEST_EXCEPTION);
				}
				return false;
			}
		}));
	}

	@Test
	public void testQueueServiceProcess() {
		when(queue.getCounter(serviceProcess)).thenReturn(TEST_COUNT);

		control.queueServiceProcess(serviceProcess);

		verify(queue, times(1)).add(serviceProcess);
	}

	@Test
	public void testQueueServiceProcessExceeded() {
		when(queue.getCounter(serviceProcess)).thenReturn(TEST_MAX_COUNT + 1);

		control.queueServiceProcess(serviceProcess);

		// Verify that a QueryStatusEvent with the correct parameters (timeout)
		// was fired
		verify(control, times(1)).fireEvent(argThat(new ArgumentMatcher<QueryStatusEvent>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof QueryStatusEvent) {
					QueryStatusEvent event = (QueryStatusEvent) argument;

					return event.getInternalId().equals(TEST_INTERNAL_SERVICE_ID)
							&& event.getExternalId().equals(TEST_EXTERNAL_SERVICE_ID)
							&& event.getStatus().equals(QueryStatus.QUEUETIMEDOUT);
				}
				return false;
			}
		}));
	}

	/**
	 * The Test for the inner type ServiceProcessQueue.
	 */
	@RunWith(PowerMockRunner.class)
	@PrepareForTest(ServiceProcessQueue.class)
	public static class ServiceProcessQueueTest {
		private static final long TEST_DELAY_PERIOD = 1;
		private static final int TEST_VALUE = 42;

		// The objects which are necessary for the test
		private DelayQueue<DelayedServiceProcess> delayQueue;
		private ServiceProcess process;
		
		// The "parent"-object for our test-object
		private AbstractQueueQueryExecutionControl control;

		// The tested object
		private ServiceProcessQueue queue;

		// A ArgumentMatcher which is used to verify if a given DelayedServiceProcess is correct
		private ArgumentMatcher<DelayedServiceProcess> delayedServiceProcessMatcher;

		@SuppressWarnings("unchecked")
		@Before
		public void init() throws Exception {
			// Create the DelayQueue-mock and stub that it waits 1 second when take() is called
			delayQueue = mock(DelayQueue.class);
			when(delayQueue.take()).thenAnswer(new Answer<DelayedServiceProcess>() {

				@Override
				public DelayedServiceProcess answer(InvocationOnMock invocation) throws Throwable {
					Thread.sleep(1000);
					return null;
				}
			});
			// Initialize that this mock is injected when a new DelayQueue is created
			PowerMockito.whenNew(DelayQueue.class).withNoArguments().thenReturn(delayQueue);

			// Create the other related objects
			setProcess(mock(ServiceProcess.class));
			when(getProcess().getInternalId()).thenReturn(TEST_INTERNAL_SERVICE_ID);

			control = mock(AbstractQueueQueryExecutionControl.class);
			doNothing().when(control).executedQueuedServiceProcess(getProcess());

			// Create our tested object as a child of the AbstractQueueQueryExecutionControl object
			queue = control.new ServiceProcessQueue(TEST_DELAY_PERIOD);

			// Initialize the ArgumentMatcher which is used in our test-cases
			delayedServiceProcessMatcher = new ArgumentMatcher<DelayedServiceProcess>() {

				@Override
				public boolean matches(Object argument) {
					if (argument instanceof DelayedServiceProcess) {
						DelayedServiceProcess delayedProcess = (DelayedServiceProcess) argument;

						return delayedProcess.getServiceProcess().equals(getProcess())
								&& delayedProcess.getEndOfDelay() <= System.currentTimeMillis() + TEST_DELAY_PERIOD
										* 1000;
					}
					return false;
				}
			};
		}

		@Test
		public void testAdd() {
			// Call the test-method
			queue.add(getProcess());

			// Verify that a valid counter-object was created
			ConcurrentMap<String, Integer> map = queue.getCounterMap();
			assertEquals(1, map.size());
			Entry<String, Integer> entry = map.entrySet().iterator().next();
			assertEquals(entry.getKey(), TEST_INTERNAL_SERVICE_ID);
			assertEquals(entry.getValue(), new Integer(1));

			// Verify that the added process was inserted in the DelayedServiceQueue
			verify(delayQueue, times(1)).add(argThat(delayedServiceProcessMatcher));
		}

		@Test
		public void testAddExists() {
			// Our process-object should be added once
			queue.getCounterMap().put(TEST_INTERNAL_SERVICE_ID, TEST_VALUE);

			// Call the test-method
			queue.add(getProcess());

			// Verify that the counter-object was increased
			ConcurrentMap<String, Integer> map = queue.getCounterMap();
			assertEquals(1, map.size());
			Entry<String, Integer> entry = map.entrySet().iterator().next();
			assertEquals(entry.getKey(), TEST_INTERNAL_SERVICE_ID);
			assertEquals(entry.getValue(), new Integer(TEST_VALUE + 1));

			// Verify that the added process was inserted in the DelayedServiceQueue once again
			verify(delayQueue, times(1)).add(argThat(delayedServiceProcessMatcher));
		}

		public ServiceProcess getProcess() {
			return process;
		}

		public void setProcess(ServiceProcess process) {
			this.process = process;
		}
	}
}
