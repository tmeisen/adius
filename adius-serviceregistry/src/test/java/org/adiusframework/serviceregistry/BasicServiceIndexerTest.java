package org.adiusframework.serviceregistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.adiusframework.service.ServiceCapabilityRule;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.service.xml.ServiceCapability;
import org.adiusframework.service.xml.ServiceDefinition;
import org.junit.Before;
import org.junit.Test;

public class BasicServiceIndexerTest {
	private static final int TEST_REGISTRATION_NUMBER = 5;
	private static final int TEST_INDEX_A = 2;
	private static final int TEST_INDEX_B = 4;

	// Objects which are needed for the tests
	private ServiceCapabilityRule rule;
	private ServiceCapability[] capabilities;
	private ServiceDefinition[] definitions;
	private ServiceRegistration[] registrations;
	private UUID uuidA, uuidB;

	// The test-object
	private BasicServiceIndexer indexer;

	@Before
	public void init() {
		// Mock and stub the object which are needed for the test
		capabilities = new ServiceCapability[TEST_REGISTRATION_NUMBER];
		registrations = new ServiceRegistration[TEST_REGISTRATION_NUMBER];
		definitions = new ServiceDefinition[TEST_REGISTRATION_NUMBER];
		for (int i = 0; i < registrations.length; i++) {
			capabilities[i] = mock(ServiceCapability.class);
			definitions[i] = mock(ServiceDefinition.class);
			when(definitions[i].getCapability()).thenReturn(capabilities[i]);
			registrations[i] = mock(ServiceRegistration.class);
			when(registrations[i].getServiceDefinition()).thenReturn(definitions[i]);
		}

		rule = mock(ServiceCapabilityRule.class);
		when(rule.satisfiedBy(capabilities[TEST_INDEX_A])).thenReturn(true);
		when(rule.satisfiedBy(capabilities[TEST_INDEX_B])).thenReturn(true);

		// Create the test-object and inject our test-registrations
		indexer = new BasicServiceIndexer();
		for (int i = 0; i < registrations.length; i++) {
			UUID uuid = indexer.add(registrations[i]);
			if (i == TEST_INDEX_A) {
				uuidA = uuid;
			}
			if (i == TEST_INDEX_B) {
				uuidB = uuid;
			}
		}
	}

	@Test
	public void testFind() {
		// Call the test-method
		List<UUID> result = indexer.find(rule);

		// Verify the result
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(uuidA));
		assertTrue(result.contains(uuidB));
	}
}
