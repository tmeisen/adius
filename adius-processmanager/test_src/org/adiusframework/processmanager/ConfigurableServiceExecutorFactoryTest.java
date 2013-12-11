package org.adiusframework.processmanager;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.adiusframework.service.ServiceInput;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.serviceprovider.xml.Binding;
import org.adiusframework.serviceprovider.xml.Protocol;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Protocol.class)
public class ConfigurableServiceExecutorFactoryTest {
	private static final String TEST_CORRELATION_ID = "TestID";
	
	// Objects which are processed by the ConfigurableServiceExecutorFactory
	private Protocol protocol;
	
	private ServiceRegistration registration;
	private ServiceProviderDefinition definition;
	private Binding binding;
	private ServiceExecutor executor;
	private ServiceInput input;
	
	// The test-object
	private ConfigurableServiceExecutorFactory factory;
	
	@Before
	public void init() {
		// Create the necessary mocks and stub their behavior
		protocol = PowerMockito.mock(Protocol.class);
		
		binding = mock(Binding.class);
		when(binding.getProtocol()).thenReturn(protocol);
				
		definition = mock(ServiceProviderDefinition.class);
		when(definition.getBinding()).thenReturn(binding);
				
		registration = mock(ServiceRegistration.class);
		when(registration.getProviderDefinition()).thenReturn(definition);
		
		executor = mock(ServiceExecutor.class);
		input = mock(ServiceInput.class);
		
		// Create a object which is tested
		factory = new ConfigurableServiceExecutorFactory();
	}
	
	@Test
	public void testExecute() {
		// Initialize the test-specific configuration
		Map<Protocol, ServiceExecutor> protocolMapping = new HashMap<Protocol, ServiceExecutor>();
		protocolMapping.put(protocol, executor);
		factory.setProtocolMapping(protocolMapping);
		
		// Call the tested method
		factory.execute(TEST_CORRELATION_ID, registration, input);
		
		// Verify if the call was redirected correct
		verify(executor, times(1)).execute(TEST_CORRELATION_ID, registration, input);
	}
	
	@Test
	public void testExecuteException() {
		// Initialize the test-specific configuration
		factory.setProtocolMapping(new HashMap<Protocol, ServiceExecutor>());
		
		// The execution should end in a UnsupportedOperationException
		try {
			factory.execute(TEST_CORRELATION_ID, registration, input);
			fail();
		} catch(UnsupportedOperationException exception) {
			// hence we are lucky if the exception occurs
		}
		
		verify(executor, times(0)).execute(TEST_CORRELATION_ID, registration, input);
	}
}
