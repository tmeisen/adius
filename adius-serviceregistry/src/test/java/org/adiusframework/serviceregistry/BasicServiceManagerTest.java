package org.adiusframework.serviceregistry;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.adiusframework.util.datastructures.SystemData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

public class BasicServiceManagerTest {

	// Infrastructure objects which are necessary for the test
	private ServiceIndexer indexer;

	// Dummy objects
	final ServiceProviderDefinition providerDefinition = mock(ServiceProviderDefinition.class);
	final ServiceDefinition serviceDefinition = mock(ServiceDefinition.class);
	final SystemData systemData = mock(SystemData.class);

	// The tested object
	private BasicServiceManager manager;

	// A matcher for the test
	private ArgumentMatcher<ServiceRegistration> serviceRegistrationMatcher = new ArgumentMatcher<ServiceRegistration>() {

		@Override
		public boolean matches(Object argument) {
			if (argument instanceof ServiceRegistration) {
				ServiceRegistration registration = (ServiceRegistration) argument;

				return registration.getProviderDefinition().equals(providerDefinition)
						&& registration.getServiceDefinition().equals(serviceDefinition)
						&& registration.getSystemData().equals(systemData);
			}

			return false;
		}

	};

	@Before
	public void init() {

		// Create the infrastructure objects
		indexer = mock(ServiceIndexer.class);

		// Create the tested object and inject the dependent objects
		manager = new BasicServiceManager();
		manager.setIndexer(indexer);
	}

	@Test
	public void testRegister() {
		// Some dummy objects
		UUID uuid = UUID.randomUUID();

		when(indexer.add(argThat(serviceRegistrationMatcher))).thenReturn(uuid);

		// Call the test-method
		assertEquals(uuid, manager.register(providerDefinition, serviceDefinition, systemData));

		// Verify
		verify(indexer, times(1)).add(argThat(serviceRegistrationMatcher));
	}

	@Test
	public void testUnregister() {
		// Some dummy objects
		UUID uuid = UUID.randomUUID();

		// Call the test-method
		manager.unregister(uuid);

		// Verify
		verify(indexer, times(1)).remove(uuid);
	}

	@Test
	public void testUnregisterAll() {
		// Call the test-method
		manager.unregisterAll();

		// Verify
		verify(indexer, times(1)).removeAll();
	}

	@Test
	public void testStarted() {
		// Some dummy objects
		UUID uuid = UUID.randomUUID();

		// Call the test-method
		manager.started(uuid);

		// Verify
		verify(indexer, times(1)).started(uuid);
	}

	@Test
	public void testStopped() {
		// Some dummy objects
		UUID uuid = UUID.randomUUID();

		// Call the test-method
		manager.stopped(uuid);

		// Verify
		verify(indexer, times(1)).stopped(uuid);
	}
}
