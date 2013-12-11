package org.adiusframework.processmanager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.IsServiceProcessRelated;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.ErrorQueryResult;
import org.adiusframework.query.Query;
import org.adiusframework.query.QueryResult;
import org.adiusframework.query.StandardQueryResult;
import org.adiusframework.util.net.Callback;
import org.adiusframework.util.net.CallbackExecutor;
import org.adiusframework.util.net.CallbackRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class BasicProcessManagerTest {
	private static final String TEST_QUERY_ID = "testID";
	private static final String TEST_INTERNAL_ID = "internalID";
	private static final String TEST_EXTERNAL_ID = "externalID";

	// Related objects, which's methods could by called by the ProcessManager in
	// order to achieve the result
	private ExecutionEngine executionEngine;
	private CallbackRepository callbackRepository;
	private CallbackExecutor callbackExecutor;

	// Dummy-object to verify certain behavior
	private Query query;
	private Callback callback;
	private ServiceResultData result;

	// "Data"-object Mocks
	private TestException exception;
	private ServiceProcess process;
	private QueryStatusEvent event;

	// The tested BasicProcessManager
	private BasicProcessManager processManager;

	@Before
	public void init() throws ProcessManagerException {
		// Create dummy-object
		query = mock(Query.class);
		when(query.getId()).thenReturn(TEST_QUERY_ID);
		callback = mock(Callback.class);
		result = mock(ServiceResultData.class);

		// Create and link data-objects
		process = mock(ServiceProcess.class);
		when(process.getInternalId()).thenReturn(TEST_INTERNAL_ID);
		when(process.getExternalId()).thenReturn(TEST_EXTERNAL_ID);
		exception = mock(TestException.class);
		when(exception.getServiceProcess()).thenReturn(process);
		event = mock(QueryStatusEvent.class);
		when(event.getInternalId()).thenReturn(TEST_INTERNAL_ID);

		// Create and stub the related mocks
		executionEngine = mock(ExecutionEngine.class);
		callbackRepository = mock(CallbackRepository.class);
		when(callbackRepository.find(TEST_INTERNAL_ID)).thenReturn(callback);
		callbackExecutor = mock(CallbackExecutor.class);

		// Initialize the test-object
		processManager = spy(new BasicProcessManager());
		processManager.setExecutionEngine(executionEngine);
		processManager.setCallbackRepository(callbackRepository);
		processManager.setCallbackExecutor(callbackExecutor);

		// The BasicProcessManager should register as a listener to the
		// ExecutionEngine-mock
		verify(executionEngine, times(1)).registerListener(processManager);
	}

	@Test
	public void testHandleQuery() throws ProcessManagerException {
		processManager.handleQuery(query, callback);

		verify(executionEngine, times(1)).handleQuery(eq(query), anyString());
		verify(callbackRepository, times(1)).register(anyString(), eq(callback));
	}

	@Test
	public void testHandleQueryProcessManagerException() throws ProcessManagerException {
		doThrow(exception).when(executionEngine).handleQuery(eq(query), anyString());

		processManager.handleQuery(query, callback);

		verify(callbackExecutor, times(1)).execute(eq(callback), any(ErrorQueryResult.class));
	}

	@Test
	public void testHandleResult() throws ProcessManagerException {
		processManager.handleResult(result);

		verify(executionEngine, times(1)).handleResult(result);
	}

	@Test
	public void testHandleResultException() throws Exception {
		doNothing().when(processManager).executeCallback(eq(TEST_INTERNAL_ID), any(QueryResult.class));
		doThrow(exception).when(executionEngine).handleResult(result);

		processManager.handleResult(result);

		verify(processManager, times(1)).executeCallback(eq(TEST_INTERNAL_ID), any(ErrorQueryResult.class));
	}

	@Test
	public void testQueryStatusChanged() {
		doNothing().when(processManager).executeCallback(eq(TEST_INTERNAL_ID), any(QueryResult.class));
		when(event.getStatus()).thenReturn(QueryStatus.FINISHED);

		processManager.queryStatusChanged(event);

		verify(processManager, times(1)).executeCallback(eq(TEST_INTERNAL_ID), any(StandardQueryResult.class));
	}

	@Test
	public void testQueryStatusChangedError() {
		doNothing().when(processManager).executeCallback(eq(TEST_INTERNAL_ID), any(QueryResult.class));

		when(event.getStatus()).thenReturn(QueryStatus.QUEUETIMEDOUT);
		processManager.queryStatusChanged(event);

		when(event.getStatus()).thenReturn(QueryStatus.QUEUEFAILED);
		processManager.queryStatusChanged(event);

		verify(processManager, times(2)).executeCallback(eq(TEST_INTERNAL_ID), any(ErrorQueryResult.class));
	}

	private static class TestException extends ProcessManagerException implements IsServiceProcessRelated {
		private static final long serialVersionUID = -2021417774969552162L;

		public TestException(String message) {
			super(message);
		}

		@Override
		public ServiceProcess getServiceProcess() {
			return null;
		}
	}
}
