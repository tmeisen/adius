package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.service.xml.ServiceBinding;
import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceprovider.xml.Binding;
import org.adiusframework.serviceprovider.xml.JmsData;
import org.adiusframework.serviceprovider.xml.ProtocolData;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class SpringJmsRoutingServiceExecutorTest {
	private static final String TEST_CORRELATION_ID = "testId";
	private static final String TEST_QUEUE = "queue";
	private static final String TEST_ROUTE = "testRoute";

	private static final String ROUTE_NAME = "route";

	// Mocks that are used during the test
	private JmsTemplate template;
	Destination replyTo;

	// The test-object
	private SpringJmsRoutingServiceExecutor executor;

	@Before
	public void init() {
		// Create dependent mocks
		template = mock(JmsTemplate.class);
		replyTo = mock(Destination.class);

		// Create the test-object and inject the decencies
		executor = new SpringJmsRoutingServiceExecutor();
		executor.setJmsTemplate(template);
		executor.setReplyTo(replyTo);
	}

	@Test
	public void testExecute() throws JMSException {
		// Create some test specific mocks
		JmsData jmsData = mock(JmsData.class);
		when(jmsData.getQueue()).thenReturn(TEST_QUEUE);
		ProtocolData data = mock(ProtocolData.class);
		when(data.getJmsData()).thenReturn(jmsData);
		Binding binding = mock(Binding.class);
		when(binding.getData()).thenReturn(data);
		ServiceProviderDefinition pd = mock(ServiceProviderDefinition.class);
		when(pd.getBinding()).thenReturn(binding);

		ServiceBinding serviceBinding = mock(ServiceBinding.class);
		when(serviceBinding.getRoute()).thenReturn(TEST_ROUTE);
		ServiceDefinition sd = mock(ServiceDefinition.class);
		when(sd.getBinding()).thenReturn(serviceBinding);

		ServiceRegistration sr = mock(ServiceRegistration.class);
		when(sr.getProviderDefinition()).thenReturn(pd);
		when(sr.getServiceDefinition()).thenReturn(sd);

		// Create a dummy object that is passed though the test method
		final ServiceInput input = mock(ServiceInput.class);

		// Create some mocks to verify that the message is created correctly
		final ObjectMessage message = mock(ObjectMessage.class);
		final Session session = mock(Session.class);
		when(session.createObjectMessage()).thenReturn(message);

		// Call the test-method
		executor.execute(TEST_CORRELATION_ID, sr, input);

		verify(template, times(1)).send(eq(TEST_QUEUE), argThat(new ArgumentMatcher<MessageCreator>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof MessageCreator) {
					try {
						MessageCreator creator = (MessageCreator) argument;
						assertEquals(message, creator.createMessage(session));

						verify(message, atLeastOnce()).setJMSCorrelationID(TEST_CORRELATION_ID);
						verify(message, atLeastOnce()).setJMSReplyTo(replyTo);
						verify(message, atLeastOnce()).setStringProperty(ROUTE_NAME, TEST_ROUTE);
						verify(message, atLeastOnce()).setObject(input);

						return true;
					} catch (JMSException e) {
						fail();
					}
				}

				return false;
			}
		}));
	}
}
