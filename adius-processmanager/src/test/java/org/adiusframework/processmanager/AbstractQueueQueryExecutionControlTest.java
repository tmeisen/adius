package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
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
import org.adiusframework.processmanager.exception.InvalidDomainException;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.processmanager.exception.ServiceProcessFailedException;
import org.adiusframework.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * This test class is a sub-type of the AbstractQueryExecutionControlTest
 * because the tested classes are sub-types, too. Therefore it hase two
 * init-method, which invoke the init-methods of the super-type. Otherwise the
 * test would not been configured proper.
 */
public class AbstractQueueQueryExecutionControlTest extends AbstractQueryExecutionControlTest {
	private static final String TEST_PROCESS_TYPE = "testType";
	private static final String TEST_MESSAGE = "Test exception message";

	private static final int TEST_MAX_COUNT = 5;

	// Fields to indicate, that store information for the callback of the tested
	// object
	private boolean isUnprocessed;
	private boolean isError;

	// A related object which is needed for the test
	private ServiceProcessQueue queue;

	// The new test-object
	private AbstractQueueQueryExecutionControl control;

	@Override
	@Before
	public void init() throws ProcessManagerException {
		// Create the test-object and call the necessary init-method
		control = spy(new AbstractQueueQueryExecutionControl() {
			@Override
			protected boolean isUnprocessed(ServiceProcess serviceProcess) throws InvalidDomainException {
				return AbstractQueueQueryExecutionControlTest.this.isUnprocessed();
			}

			@Override
			protected boolean isError(ServiceProcess serviceProcess) throws InvalidDomainException {
				return AbstractQueueQueryExecutionControlTest.this.isError();
			}

			@Override
			protected int identifyDomainEntityId(ServiceProcessDefinition spd, Query query)
					throws ProcessManagerException {
				return TEST_ID;
			}

			@Override
			public boolean checkConfiguration() {
				return true;
			}

			@Override
			protected void handleError(Query query, int entityId) throws ProcessManagerException {
				System.out.println("handleError: " + query + " entity id " + entityId);
			}
		});
		init(control);
	}

	public void init(AbstractQueueQueryExecutionControl control) throws ProcessManagerException {

		// Initialize the super-class
		super.init(control);

		// Create the mocks which are needed in the extended tests
		queue = mock(ServiceProcessQueue.class);
		when(serviceProcess.getType()).thenReturn(TEST_PROCESS_TYPE);

		// Initialize the test object
		control.setRetryCount(TEST_MAX_COUNT);
		control.setQueue(queue);

		this.control = control;
	}

	@Test
	public void testExecuteServiceProcessProcessed() throws ProcessManagerException {
		// Set the information about the status of the ServiceProcess
		setUnprocessed(false);
		setError(false);

		// Stub test-specific behavior
		when(executionControl.executeProcess(serviceProcess)).thenReturn(serviceProcess);

		// Call the tested method and verify it's return value
		assertEquals(serviceProcess, control.executeServiceProcess(serviceProcess));

		// Check that the related objects where used correctly
		verify(repository, times(1)).refreshServiceProcess(serviceProcess);
		verify(queue, times(1)).remove(serviceProcess);
	}

	@Test
	public void testExecuteServiceProcessUnprocessed() throws ProcessManagerException {
		// Set the information about the status of the ServiceProcess
		setUnprocessed(true);
		setError(false);

		// Stub test-specific behavior
		doNothing().when(control).queueServiceProcess(serviceProcess);

		// Call the tested method and verify it's return value
		assertEquals(serviceProcess, control.executeServiceProcess(serviceProcess));

		// Check that the related objects where used correctly
		verify(repository, times(1)).refreshServiceProcess(serviceProcess);
		verify(control, times(1)).queueServiceProcess(serviceProcess);
	}

	@Test
	public void testExecuteServiceProcessError() throws ProcessManagerException {
		// Set the information about the status of the ServiceProcess
		setUnprocessed(false);
		setError(true);

		// The execution should end in a ServiceProcessFailsException
		try {
			control.executeServiceProcess(serviceProcess);
			fail();
		} catch (ServiceProcessFailedException exception) {
			// hence we are lucky if the exception occurs
		}

		// Check that the related objects where used correctly
		verify(repository, times(1)).refreshServiceProcess(serviceProcess);
	}

	@Test
	public void testExecuteQueuedServiceProcessExceeded() throws ProcessManagerException {
		// Stub the test-specific behavior that the execution fails
		doThrow(new ProcessManagerException(TEST_MESSAGE) {
			/**
			 * UID for serialization
			 */
			private static final long serialVersionUID = 1298395056982148599L;

		}).when(control).executeServiceProcess(serviceProcess);

		// Call the test method
		control.executedQueuedServiceProcess(serviceProcess);

		// Verify that a QueryStatusEvent with the correct parameters was fired
		verify(queue, times(1)).remove(serviceProcess);
		verify(control, times(1)).fireEvent(argThat(new ArgumentMatcher<QueryStatusEvent>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof QueryStatusEvent) {
					QueryStatusEvent event = (QueryStatusEvent) argument;

					return event.getInternalId().equals(TEST_INTERNAL_ID)
							&& event.getExternalId().equals(TEST_EXTERNAL_ID)
							&& event.getStatus().equals(QueryStatus.QUEUEFAILED)
							&& event.getAttachment().equals(TEST_MESSAGE);
				}
				return false;
			}
		}));
	}

	@Test
	public void testQueueServiceProcess() {
		// Stub test specific behavior
		when(queue.getCounter(serviceProcess)).thenReturn(TEST_MAX_COUNT - 1);

		// Call the test method
		control.queueServiceProcess(serviceProcess);

		// Check that the related objects where used correctly
		verify(queue, times(1)).add(serviceProcess);
	}

	@Test
	public void testQueueServiceProcessExceeded() throws ProcessManagerException {
		// Stub test specific behavior
		when(queue.getCounter(serviceProcess)).thenReturn(TEST_MAX_COUNT + 1);

		// Call the test method
		control.queueServiceProcess(serviceProcess);

		// Verify that the ServiceProcess was removed from the Queue and that a
		// QueryStatusEvent
		// with the correct parameters was fired
		verify(queue, times(1)).remove(serviceProcess);
		verify(control, times(1)).fireEvent(argThat(new ArgumentMatcher<QueryStatusEvent>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof QueryStatusEvent) {
					QueryStatusEvent event = (QueryStatusEvent) argument;

					return event.getInternalId().equals(TEST_INTERNAL_ID)
							&& event.getExternalId().equals(TEST_EXTERNAL_ID)
							&& event.getStatus().equals(QueryStatus.QUEUETIMEDOUT);
				}
				return false;
			}
		}));
	}

	public boolean isUnprocessed() {
		return isUnprocessed;
	}

	public void setUnprocessed(boolean isUnprocessed) {
		this.isUnprocessed = isUnprocessed;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(boolean isError) {
		this.isError = isError;
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

		// A ArgumentMatcher which is used to verify if a given
		// DelayedServiceProcess is correct
		private ArgumentMatcher<DelayedServiceProcess> delayedServiceProcessMatcher;

		@SuppressWarnings("unchecked")
		@Before
		public void init() throws Exception {
			// Create the DelayQueue-mock and stub that it waits 1 second when
			// take() is called
			delayQueue = mock(DelayQueue.class);
			when(delayQueue.take()).thenAnswer(new Answer<DelayedServiceProcess>() {

				@Override
				public DelayedServiceProcess answer(InvocationOnMock invocation) throws Throwable {
					Thread.sleep(1000);
					return null;
				}
			});
			// Initialize that this mock is injected when a new DelayQueue is
			// created
			PowerMockito.whenNew(DelayQueue.class).withNoArguments().thenReturn(delayQueue);

			// Create the other related objects
			setProcess(mock(ServiceProcess.class));
			when(getProcess().getInternalId()).thenReturn(TEST_INTERNAL_ID);

			control = mock(AbstractQueueQueryExecutionControl.class);
			doNothing().when(control).executedQueuedServiceProcess(getProcess());

			// Create our tested object as a child of the
			// AbstractQueueQueryExecutionControl object
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
			assertEquals(entry.getKey(), TEST_INTERNAL_ID);
			assertEquals(entry.getValue(), new Integer(1));

			// Verify that the added process was inserted in the
			// DelayedServiceQueue
			verify(delayQueue, times(1)).add(argThat(delayedServiceProcessMatcher));
		}

		@Test
		public void testAddExists() {
			// Our process-object should be added once
			queue.getCounterMap().put(TEST_INTERNAL_ID, TEST_VALUE);

			// Call the test-method
			queue.add(getProcess());

			// Verify that the counter-object was increased
			ConcurrentMap<String, Integer> map = queue.getCounterMap();
			assertEquals(1, map.size());
			Entry<String, Integer> entry = map.entrySet().iterator().next();
			assertEquals(entry.getKey(), TEST_INTERNAL_ID);
			assertEquals(entry.getValue(), new Integer(TEST_VALUE + 1));

			// Verify that the added process was inserted in the
			// DelayedServiceQueue once again
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
