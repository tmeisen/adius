package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

public class AbstractQueryExecutionControlTest {
	protected static final int TEST_ID = 42;
	protected static final String TEST_INTERNAL_ID = "testInternalId";
	protected static final String TEST_EXTERNAL_ID = "testExternalId";

	// Objects that store data and are passed through the tested methods
	protected Query query;
	protected ServiceProcess serviceProcess;
	protected ServiceProcessDefinition spd;

	// Related object which are infrastructure for the test
	protected ProcessManagerRepository repository;
	protected ServiceProcessExecutionControl executionControl;
	protected ResourceHandler resourceHandler;

	// The tested object
	private AbstractQueryExecutionControl control;

	@Before
	public void init() throws ProcessManagerException {
		// Create a AbstractQueryExecutionControl object and call the
		// initialization
		control = spy(new AbstractQueryExecutionControl() {

			@Override
			protected int identifyDomainEntityId(ServiceProcessDefinition spd, Query q) throws ProcessManagerException {
				if (query.equals(q)) {
					return TEST_ID;
				}

				return 0;
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

	/**
	 * This initialization should be called for the tested object of all
	 * sub-types of the AbstarctQueryExecutioNControlTest with the respective
	 * test-object.
	 * 
	 * @param control
	 *            The tested object.
	 * @throws ProcessManagerException
	 *             This exception should been thrown never, it's just because of
	 *             Mockito.
	 */
	public void init(AbstractQueryExecutionControl control) throws ProcessManagerException {
		// Create the data objects
		query = mock(Query.class);
		serviceProcess = mock(ServiceProcess.class);
		when(serviceProcess.getInternalId()).thenReturn(TEST_INTERNAL_ID);
		when(serviceProcess.getExternalId()).thenReturn(TEST_EXTERNAL_ID);
		spd = mock(ServiceProcessDefinition.class);
		when(spd.isDataAccessor()).thenReturn(true);

		// Create the infrastructure
		repository = mock(ProcessManagerRepository.class);
		when(repository.createServiceProcess(query, TEST_INTERNAL_ID, TEST_ID)).thenReturn(serviceProcess);
		executionControl = mock(ServiceProcessExecutionControl.class);
		when(executionControl.findServiceDefinition(anyString(), anyString())).thenReturn(spd);
		when(executionControl.executeProcess(serviceProcess)).thenReturn(serviceProcess);
		resourceHandler = mock(ResourceHandler.class);

		// Create the test-object and inject the related objects
		control.setRepository(repository);
		control.setServiceProcessExecutionControl(executionControl);
		control.setResourceHandler(resourceHandler);
		doNothing().when(control).fireEvent(any(QueryStatusEvent.class));

		this.control = control;
	}

	@Test
	public void testHandleQuery() throws ProcessManagerException {
		// Add test specific behavior
		doReturn(serviceProcess).when(control).executeServiceProcess(serviceProcess);

		// Call the tested method
		control.handleQuery(query, TEST_INTERNAL_ID);

		// Check that the necessary method calls happened
		verify(resourceHandler, times(1)).registerResources(serviceProcess, query);
		verify(control, times(1)).executeServiceProcess(serviceProcess);
		verify(control, times(1)).fireEvent(argThat(new ArgumentMatcher<QueryStatusEvent>() {
			@Override
			public boolean matches(Object argument) {
				if (argument instanceof QueryStatusEvent) {
					QueryStatusEvent event = (QueryStatusEvent) argument;

					return event.getInternalId().equals(TEST_INTERNAL_ID)
							&& event.getExternalId().equals(TEST_EXTERNAL_ID)
							&& event.getStatus().equals(QueryStatus.EXECUTION);
				}

				return false;
			}
		}));
	}

	@Test
	public void testExecuteServiceProcess() throws ProcessManagerException {
		// Call the test-method and verify it's return value
		assertEquals(serviceProcess, control.executeServiceProcess(serviceProcess));
	}
}
