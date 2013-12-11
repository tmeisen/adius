package org.adiusframework.serviceregistry;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.adiusframework.service.ServiceCapabilityRule;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.adiusframework.util.datastructures.SystemData;
import org.junit.Before;
import org.junit.Test;

public class BasicServiceRegistryTest {
	// The object which get the redirects of the test-object
	private ServiceManager manager;
	private ServiceFinder finder;

	// The test-object
	private BasicServiceRegistry registry;

	@Before
	public void init() {
		// Create the infrastructure objects
		manager = mock(ServiceManager.class);
		finder = mock(ServiceFinder.class);

		// Create the test-object and inject the dependent objects
		registry = new BasicServiceRegistry();
		registry.setManager(manager);
		registry.setFinder(finder);
	}

	@Test
	public void testRegister() {
		// Create test-specific dummies
		UUID uuid = UUID.randomUUID();
		ServiceProviderDefinition providerDefinition = mock(ServiceProviderDefinition.class);
		ServiceDefinition serviceDefinition = mock(ServiceDefinition.class);
		SystemData systemData = mock(SystemData.class);

		// Mock specific behavior
		when(manager.register(providerDefinition, serviceDefinition, systemData)).thenReturn(uuid);

		// Call the test-method and verify it's result
		assertEquals(uuid, registry.register(providerDefinition, serviceDefinition, systemData));

		// Verify that the call was redirected correctly
		verify(manager, times(1)).register(providerDefinition, serviceDefinition, systemData);
	}

	@Test
	public void testUnregister() {
		// Create test-specific dummies
		UUID uuid = UUID.randomUUID();

		// Call the test-method
		registry.unregister(uuid);

		// Verify that the call was redirected correctly
		verify(manager, times(1)).unregister(uuid);
	}

	@Test
	public void testUnregisterAll() {
		// Call the test-method
		registry.unregisterAll();

		// Verify that the call was redirected correctly
		verify(manager, times(1)).unregisterAll();
	}

	@Test
	public void testStarted() {
		// Create test-specific dummies
		UUID uuid = UUID.randomUUID();

		// Call the test-method
		registry.started(uuid);

		// Verify that the call was redirected correctly
		verify(manager, times(1)).started(uuid);
	}

	@Test
	public void testStopped() {
		// Create test-specific dummies
		UUID uuid = UUID.randomUUID();

		// Call the test-method
		registry.stopped(uuid);

		// Verify that the call was redirected correctly
		verify(manager, times(1)).stopped(uuid);
	}

	@Test
	public void testFind() {
		// Create test-specific dummies
		ServiceCapabilityRule rule = mock(ServiceCapabilityRule.class);
		ServiceRegistration registration = mock(ServiceRegistration.class);

		// Mock specific behavior
		when(finder.find(rule)).thenReturn(registration);

		// Call the test-method
		registry.find(rule);

		// Verify that the call was redirected correctly
		verify(finder, times(1)).find(rule);
	}
}
