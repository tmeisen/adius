package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.InvalidDomainException;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.ErrorQueryResult;
import org.adiusframework.query.Query;
import org.adiusframework.query.StandardQueryResult;
import org.adiusframework.resource.Resource;
import org.adiusframework.resourcemanager.ResourceManager;
import org.adiusframework.service.CategoryServiceCapabilityRule;
import org.adiusframework.service.ErrorServiceResult;
import org.adiusframework.service.ServiceCapabilityRule;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.service.StandardServiceInput;
import org.adiusframework.service.StandardServiceResult;
import org.adiusframework.service.xml.Category;
import org.adiusframework.serviceprovider.xml.Protocol;
import org.adiusframework.serviceregistry.ServiceFinder;
import org.adiusframework.util.jms.JmsCallback;
import org.adiusframework.util.testing.AdiusIntegrationTest;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.JmsTemplate;

public class ProcessManagerIntegrationTest extends AdiusIntegrationTest<ProcessManager> implements Serializable {
	private static final long serialVersionUID = 188217010767806286L;

	// Some constants that are needed for the asynchronous answers
	private static final String ANSWER_QUEUE = "returnQueue";
	private static final String TEST_SERVICE_QUEUE = "testServiceQueue";
	private static final String TEST_JMS_ID = "testJmsId";
	private static final int TEST_RECEIVE_TIMEOUT = 5000;

	// The constants that belong to our test-scenario
	private static final String TEST_DOMAIN = "testDomain";
	private static final String TEST_QUERY_ID = "testQueryId";
	private static final String TEST_QUERY_TYPE = "test_process";
	private static final String TEST_ENTITY_RESOURCE_URI = "test/entity/resource/uri";
	private static final String TEST_SUBCATEGORY = "TestSubCategory";
	static final Category TEST_CATEGORY = Category.INTEGRATION;
	private static final int TEST_DOMAIN_ENTITY_ID = 1234;
	private static final String TEST_ERROR_MESSAGE = "TestError";

	// The JmsTemplate to receive the asynchronous answer of the ProcessManager
	// and a Callback
	// object to provide the ProcessManager with the needed information
	JmsTemplate template;
	private JmsCallback callback;

	private BasicServiceProcessDefinitionFinder definitionFinder;

	// The tested (remoted) application
	ProcessManager manager;

	// The mocks of the ServiceRegistry and the ResourceManager
	private ServiceFinder serviceFinder;
	private ResourceManager resourceManager;

	// An ArgumentMatcher for stub the ServiceRegistry correctly
	private ArgumentMatcher<ServiceCapabilityRule> categoryServiceCapabilityRuleMatcher;

	// Some mapping that have a different configuration based on the current
	// test-case
	private Map<Protocol, ServiceExecutor> protocolMapping;

	// Some sample objects that are needed for test-specific-configuration
	private ParsedServiceProcessDefinition serviceProcessDefinition;
	private ServiceRegistration serviceRegistration;
	private SpringJmsRoutingServiceExecutor serviceExecutor;
	private TestServiceProcessExecutionControl serviceProcessExecutionControl;

	// A test-service to handle the requests
	private TestService testService;

	@After
	public void clearConfiguration() {
		protocolMapping.clear();
		serviceProcessExecutionControl.clear();

		when(serviceFinder.find(argThat(categoryServiceCapabilityRuleMatcher))).thenReturn(null);

		testService.stopService();
	}

	@Override
	protected void setApplication(ProcessManager application) {
		manager = application;
	}

	@Override
	protected void doSpecificConfiguration(AbstractApplicationContext context) {

		// Create the JmsTemplate and prepare the Callback
		template = new JmsTemplate(context.getBean("connectionFactory", ConnectionFactory.class));
		template.setDefaultDestinationName(ANSWER_QUEUE);
		template.setReceiveTimeout(TEST_RECEIVE_TIMEOUT);
		callback = new JmsCallback();
		callback.setConnectionUrl("tcp://localhost:35501");
		callback.setDestinationType(JmsCallback.DESTINATION_TYPE_QUEUE);
		callback.setDestinationName(ANSWER_QUEUE);
		callback.setCorrelationId(TEST_JMS_ID);

		// Prepare the mocks of the other components an inject them
		serviceFinder = mock(ServiceFinder.class);
		when(serviceFinder.checkConfiguration()).thenReturn(true);
		resourceManager = mock(ResourceManager.class);
		when(resourceManager.checkConfiguration()).thenReturn(true);
		context.getBean("serviceProcessExecutionControl", AbstractServiceProcessExecutionControl.class)
				.setServiceFinder(serviceFinder);
		context.getBean("resourceHandler", AbstractResourceHandler.class).setResourceManager(resourceManager);

		// Create the ArgumentMatcher for the ServiceFinder
		categoryServiceCapabilityRuleMatcher = new ArgumentMatcher<ServiceCapabilityRule>() {
			@Override
			public boolean matches(Object argument) {
				if (argument instanceof CategoryServiceCapabilityRule) {
					CategoryServiceCapabilityRule rule = (CategoryServiceCapabilityRule) argument;

					return rule.getCategory().equals(TEST_CATEGORY) && rule.getSubcategory() != null
							&& rule.getSubcategory().equals(TEST_SUBCATEGORY);
				}
				return false;
			}
		};

		// Prepare the objects with a test-case-specific configuration
		definitionFinder = context.getBean("serviceProcessDefinitionFinder", BasicServiceProcessDefinitionFinder.class);
		protocolMapping = new HashMap<Protocol, ServiceExecutor>();
		context.getBean("serviceExecutorFactory", ConfigurableServiceExecutorFactory.class).setProtocolMapping(
				protocolMapping);

		serviceProcessDefinition = context
				.getBean("testServiceProcessDefinition", ParsedServiceProcessDefinition.class);
		serviceRegistration = context.getBean("serviceRegistration", ServiceRegistration.class);
		serviceExecutor = context.getBean("serviceExecutor", SpringJmsRoutingServiceExecutor.class);
		serviceExecutor.setJmsTemplate(template);
		serviceProcessExecutionControl = context.getBean("serviceProcessExecutionControl",
				TestServiceProcessExecutionControl.class);

		testService = new TestService();
	}

	@Test
	public void testProcessManager() throws IOException, JMSException {
		// Set the test-specific configuration
		definitionFinder.add(serviceProcessDefinition);
		when(serviceFinder.find(argThat(categoryServiceCapabilityRuleMatcher))).thenReturn(serviceRegistration);
		protocolMapping.put(Protocol.JMS, serviceExecutor);
		testService.startService(new StandardServiceResult());

		// Create a test-query and call the initial test-method
		Query query = new TestQuery(TEST_QUERY_ID, TEST_DOMAIN);
		manager.handleQuery(query, callback);

		// Verify that the execution of the Query was successful
		Message m = template.receive();
		if (m == null) {
			fail("No answer");
			return;
		}
		assertTrue("No object message returned", m instanceof ObjectMessage);
		Object o = ((ObjectMessage) m).getObject();
		assertTrue("No standard query result returned: " + o, o instanceof StandardQueryResult);
		StandardQueryResult result = (StandardQueryResult) o;
		assertEquals("Unequal ids", TEST_QUERY_ID, result.getQueryId());
		assertTrue("Service process has not been triggered correctly",
				serviceProcessExecutionControl.wasServiceProcessResultTriggered());
		assertFalse("Error result has not been triggered correctly",
				serviceProcessExecutionControl.wasErrorResultTriggered());
	}

	@Test
	public void testProcessManagerServiceException() throws IOException, JMSException {
		// Set the test-specific configuration
		definitionFinder.add(serviceProcessDefinition);
		when(serviceFinder.find(argThat(categoryServiceCapabilityRuleMatcher))).thenReturn(serviceRegistration);
		protocolMapping.put(Protocol.JMS, serviceExecutor);
		testService.startService(new ErrorServiceResult(TEST_ERROR_MESSAGE));

		// Create a test-query and call the initial test-method
		Query query = new TestQuery(TEST_QUERY_ID, TEST_DOMAIN);
		manager.handleQuery(query, callback);

		// Verify that the execution of the Query was successful
		Message m = template.receive();
		if (m == null) {
			fail("No answer");
			return;
		}
		assertTrue("No object message returned", m instanceof ObjectMessage);
		Object o = ((ObjectMessage) m).getObject();
		assertTrue("No error query result returned", o instanceof ErrorQueryResult);
		ErrorQueryResult result = (ErrorQueryResult) o;
		assertEquals("Unequal ids", TEST_QUERY_ID, result.getQueryId());
		assertEquals("Unequal error message", TEST_ERROR_MESSAGE, result.getMessage());
		assertFalse("Service process has not been triggered correctly",
				serviceProcessExecutionControl.wasServiceProcessResultTriggered());
		assertTrue("Error result has not been triggered correctly",
				serviceProcessExecutionControl.wasErrorResultTriggered());
	}

	@Test
	public void testProcessManagerNoService() throws IOException, JMSException {
		// Set the test-specific configuration
		definitionFinder.add(serviceProcessDefinition);
		when(serviceFinder.find(argThat(categoryServiceCapabilityRuleMatcher))).thenReturn(serviceRegistration);
		protocolMapping.put(Protocol.JMS, serviceExecutor);

		// Create a test-query and call the initial test-method
		Query query = new TestQuery(TEST_QUERY_ID, TEST_DOMAIN);
		manager.handleQuery(query, callback);

		// Verify that the initial execution of the Query failed
		Message m = template.receive();
		assertNull("Returned message is null", m);
		assertFalse("Service process has not been triggered correctly",
				serviceProcessExecutionControl.wasServiceProcessResultTriggered());
		assertFalse("Error result has not been triggered correctly",
				serviceProcessExecutionControl.wasErrorResultTriggered());
	}

	@Test
	public void testProcessManagerNoProtocolMapping() throws IOException, JMSException {
		// Set the test-specific configuration
		definitionFinder.add(serviceProcessDefinition);
		when(serviceFinder.find(argThat(categoryServiceCapabilityRuleMatcher))).thenReturn(serviceRegistration);

		// Create a test-query and call the initial test-method
		Query query = new TestQuery(TEST_QUERY_ID, TEST_DOMAIN);
		manager.handleQuery(query, callback);

		// Verify that the initial execution of the Query failed
		Message m = template.receive();
		if (m == null) {
			fail("No answer");
			return;
		}

		assertTrue(m instanceof ObjectMessage);
		Object o = ((ObjectMessage) m).getObject();
		assertTrue(o instanceof ErrorQueryResult);
		ErrorQueryResult result = (ErrorQueryResult) o;
		assertEquals(TEST_QUERY_ID, result.getQueryId());
		assertFalse(serviceProcessExecutionControl.wasServiceProcessResultTriggered());
		assertFalse(serviceProcessExecutionControl.wasErrorResultTriggered());
	}

	@Test
	public void testProcessManagerNoServiceRegistration() throws IOException, JMSException {
		// Set the test-specific configuration
		definitionFinder.add(serviceProcessDefinition);

		// Create a test-query and call the initial test-method
		Query query = new TestQuery(TEST_QUERY_ID, TEST_DOMAIN);
		manager.handleQuery(query, callback);

		// Verify that the initial execution of the Query failed
		Message m = template.receive();
		if (m == null) {
			fail("No answer");
			return;
		}

		assertTrue(m instanceof ObjectMessage);
		Object o = ((ObjectMessage) m).getObject();
		assertTrue(o instanceof ErrorQueryResult);
		ErrorQueryResult result = (ErrorQueryResult) o;
		assertEquals(TEST_QUERY_ID, result.getQueryId());
		assertFalse(serviceProcessExecutionControl.wasServiceProcessResultTriggered());
		assertFalse(serviceProcessExecutionControl.wasErrorResultTriggered());
	}

	@Test
	public void testProcessManagerNoServiceProcessDefinition() throws IOException, JMSException {

		// Create a test-query and call the initial test-method
		Query query = new TestQuery(TEST_QUERY_ID, TEST_DOMAIN);
		manager.handleQuery(query, callback);

		// Verify that the initial execution of the Query failed
		Message m = template.receive();
		if (m == null) {
			fail("No answer");
			return;
		}

		assertTrue(m instanceof ObjectMessage);
		Object o = ((ObjectMessage) m).getObject();
		assertTrue(o instanceof ErrorQueryResult);
		ErrorQueryResult result = (ErrorQueryResult) o;
		assertEquals(TEST_QUERY_ID, result.getQueryId());
		assertFalse(serviceProcessExecutionControl.wasServiceProcessResultTriggered());
		assertFalse(serviceProcessExecutionControl.wasErrorResultTriggered());
	}

	@Test
	public void testProcessManagerUnmappedQuery() throws IOException, JMSException {
		// Create a test-query and call the initial test-method
		Query query = new UnmappedTestQuery(TEST_QUERY_ID, TEST_DOMAIN);
		manager.handleQuery(query, callback);

		// Verify that the initial execution of the Query failed
		Message m = template.receive();
		if (m == null) {
			fail("No answer");
			return;
		}

		assertTrue(m instanceof ObjectMessage);
		Object o = ((ObjectMessage) m).getObject();
		assertTrue(o instanceof ErrorQueryResult);
		ErrorQueryResult result = (ErrorQueryResult) o;
		assertEquals(TEST_QUERY_ID, result.getQueryId());
		assertFalse(serviceProcessExecutionControl.wasServiceProcessResultTriggered());
		assertFalse(serviceProcessExecutionControl.wasErrorResultTriggered());
	}

	/**
	 * A class that represents a test service, that simply replies a configured
	 * result.
	 */
	private class TestService implements Runnable {
		// The result that is replied
		private ServiceResult result;

		// Indicates that the Thread should be stopped
		private boolean stop;

		/**
		 * Default constructor
		 */
		public TestService() {
		}

		/**
		 * Creates a new TestService that will reply the specified result.
		 * 
		 * @param result
		 *            The result to be replied.
		 */
		public void startService(ServiceResult result) {
			this.result = result;
			stop = false;
			new Thread(this).start();
		}

		/**
		 * Stops the test service.
		 */
		public void stopService() {
			stop = true;
		}

		@Override
		public void run() {
			try {
				while (!stop) {
					final Message message = template.receive(TEST_SERVICE_QUEUE);
					if (message != null && message instanceof ObjectMessage) {
						try {
							Object object = ((ObjectMessage) message).getObject();
							if (object instanceof StandardServiceInput) {
								LOGGER.info("Received service-call with correlation ID: "
										+ message.getJMSCorrelationID());
								manager.handleResult(new BasicServiceResultData(message.getJMSCorrelationID(), result));
							}
						} catch (JMSException e) {
							LOGGER.info("JMSException while processing a message to the test-service: "
									+ e.getMessage());
						}
					}

					try {
						Thread.sleep(1000);
					} catch (InterruptedException exception) {
						fail();
					}
				}
			} catch (UncategorizedJmsException exception) {
				// Do nothing, could be thrown if the broker is stopped before
				// this threat is stopped
			}
		}
	}

	/**
	 * A basic implementation of the AbstractQueryExecutionControl.
	 */
	public static class TestQueryExecutionControl extends AbstractQueryExecutionControl {

		@Override
		protected int identifyDomainEntityId(ServiceProcessDefinition spd, Query query) throws ProcessManagerException {
			return TEST_DOMAIN_ENTITY_ID;
		}

		@Override
		protected void handleError(Query query, int entityId) throws ProcessManagerException {
			// nothing to do
		}
	}

	/**
	 * A basic implementation of the AbstractServiceProcessExecutionControl that
	 * can store if the two abstract methods a called.
	 */
	public static class TestServiceProcessExecutionControl extends AbstractServiceProcessExecutionControl {

		// stores if the abstract methods are called
		private boolean errorResultTriggered, serviceProcessResultTriggered;

		/**
		 * Determines if {@link #handleErrorResult(ServiceProcess)} was called
		 * since {@link #clear()} was called the last time.
		 * 
		 * @return True, if it was called, false otherwise
		 */
		public boolean wasErrorResultTriggered() {
			return errorResultTriggered;
		}

		/**
		 * Determines if {@link #handleServiceProcessResult(ServiceProcess)} was
		 * called since {@link #clear()} was called the last time.
		 * 
		 * @return True, if it was called, false otherwise
		 */
		public boolean wasServiceProcessResultTriggered() {
			return serviceProcessResultTriggered;
		}

		/**
		 * Resets the status of both methods.
		 */
		public void clear() {
			errorResultTriggered = false;
			serviceProcessResultTriggered = false;
		}

		@Override
		public void handleErrorResult(ServiceProcess serviceProcess) throws InvalidDomainException {
			errorResultTriggered = true;
		}

		@Override
		public void handleServiceProcessResult(ServiceProcess serviceProcess) throws InvalidDomainException {
			serviceProcessResultTriggered = true;
		}
	}

	/**
	 * A basic implementation of the AbstractResourceHandler.
	 */
	public static class TestResourceHandler extends AbstractResourceHandler {

		@Override
		protected String generateEntityResourceUri(String domain, Integer entityId) throws InvalidDomainException {
			return TEST_ENTITY_RESOURCE_URI;
		}
	}

	/**
	 * A basic implementation of the query that is supposed to been
	 * mapped by an ID.
	 */
	public static class TestQuery implements Query {
		private static final long serialVersionUID = 6595797414380417173L;
		
		private String domain;

		private String id;

		public TestQuery(String id, String domain) {
			this.id = id;
			this.domain = domain;
		}

		public boolean isValid() {
			return true;
		}

		@Override
		public String getType() {
			return TEST_QUERY_TYPE;
		}

		@Override
		public String getDomain() {
			return domain;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public List<Resource> getResources() {
			return null;
		}

		@Override
		public Properties getProperties() {
			return null;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("TestQuery [id=");
			builder.append(id);
			builder.append(", domain=");
			builder.append(domain);
			builder.append("]");
			return builder.toString();
		}
	}

	/**
	 * A basic implementation of the query that is supposed to not been
	 * mapped by an ID.
	 */
	public static class UnmappedTestQuery implements Query {
		private static final long serialVersionUID = 6595797414380417173L;

		private String domain;

		private String id;

		public UnmappedTestQuery(String id, String domain) {
			this.id = id;
			this.domain = domain;
		}

		public boolean isValid() {
			return true;
		}

		@Override
		public String getType() {
			return TEST_QUERY_TYPE;
		}

		@Override
		public String getDomain() {
			return domain;
		}

		@Override
		public String getId() {
			return id;
		}

		@Override
		public List<Resource> getResources() {
			return null;
		}

		@Override
		public Properties getProperties() {
			return null;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("UnmappedTestQuery [id=");
			builder.append(id);
			builder.append(", domain=");
			builder.append(domain);
			builder.append("]");
			return builder.toString();
		}
	}
}
