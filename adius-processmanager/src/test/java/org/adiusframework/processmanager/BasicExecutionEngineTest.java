package org.adiusframework.processmanager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;
import org.junit.Before;
import org.junit.Test;

public class BasicExecutionEngineTest {
	private static final String TEST_INTERNAL_ID = "testId";

	// The objects that are accessed by the BasicExecutionEngine
	private QueryExecutionControl queryExecutionControl;
	private ServiceProcessExecutionControl serviceProcessExecutionControl;

	// The tested BasicExecutionEngine
	private BasicExecutionEngine engine;

	@Before
	public void init() {
		// Create the dependent objects
		queryExecutionControl = mock(QueryExecutionControl.class);
		serviceProcessExecutionControl = mock(ServiceProcessExecutionControl.class);

		// Create the test object and inject the dependencies
		engine = new BasicExecutionEngine();
		engine.setQueryExecutionControl(queryExecutionControl);
		engine.setServiceProcessExecutionControl(serviceProcessExecutionControl);
	}

	@Test
	public void testHandleQuery() throws ProcessManagerException {
		// Create a test Query
		Query query = mock(Query.class);

		// Call the test method
		engine.handleQuery(query, TEST_INTERNAL_ID);

		// Check that the call was redirected correctly
		verify(queryExecutionControl, times(1)).handleQuery(query, TEST_INTERNAL_ID);
	}

	@Test
	public void testHandleResult() throws ProcessManagerException {
		// Create a test Query
		ServiceResultData result = mock(ServiceResultData.class);

		// Call the test method
		engine.handleResult(result);

		// Check that the call was redirected correctly
		verify(serviceProcessExecutionControl, times(1)).handleResult(result);
	}

	@Test
	public void testRegisterListener() {
		// Create a test Query
		QueryStatusListener listener = mock(QueryStatusListener.class);

		// Call the test method
		engine.registerListener(listener);

		// Check that the call was redirected correctly
		verify(queryExecutionControl, times(1)).registerListener(listener);
		verify(serviceProcessExecutionControl, times(1)).registerListener(listener);
	}

	@Test
	public void testUnregisterListener() {
		// Create a test Query
		QueryStatusListener listener = mock(QueryStatusListener.class);

		// Call the test method
		engine.unregisterListener(listener);

		// Check that the call was redirected correctly
		verify(queryExecutionControl, times(1)).unregisterListener(listener);
		verify(serviceProcessExecutionControl, times(1)).unregisterListener(listener);
	}
}
