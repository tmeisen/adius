package org.adiusframework.serviceregistry;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.adiusframework.service.ServiceCapabilityRule;
import org.adiusframework.service.ServiceRegistration;
import org.junit.Before;
import org.junit.Test;

public class BasicServiceFinderTest {
	private static final int TEST_COUNT_1 = (int) (Integer.MAX_VALUE * Math.random());
	private static final int TEST_COUNT_2 = (int) (Integer.MAX_VALUE * Math.random());
	private static final int TEST_COUNT_3 = (int) (Integer.MAX_VALUE * Math.random());

	// Dummy objects which are passed through the test-method
	private ServiceCapabilityRule rule;
	private ServiceRegistration registration1, registration2, registration3;

	// UUIDs which represent our test services
	private UUID service1, service2, service3;

	// The BasicServiceFinder will use this mock
	private ServiceIndexer indexer;

	// The tested object
	private BasicServiceFinder finder;

	@Before
	public void init() {
		// Create the dummy objects
		rule = mock(ServiceCapabilityRule.class);
		registration1 = mock(ServiceRegistration.class);
		registration2 = mock(ServiceRegistration.class);
		registration3 = mock(ServiceRegistration.class);

		// Create and initialize the test services
		service1 = UUID.randomUUID();
		service2 = UUID.randomUUID();
		service3 = UUID.randomUUID();
		List<UUID> services = new LinkedList<UUID>();
		services.add(service1);
		services.add(service2);
		services.add(service3);

		// Create the ServiceIndexer-mock and stub the needed behavior
		indexer = mock(ServiceIndexer.class);
		when(indexer.find(rule)).thenReturn(services);
		when(indexer.get(service1)).thenReturn(registration1);
		when(indexer.get(service2)).thenReturn(registration2);
		when(indexer.get(service3)).thenReturn(registration3);
		when(indexer.count(service1)).thenReturn(TEST_COUNT_1);
		when(indexer.count(service2)).thenReturn(TEST_COUNT_2);
		when(indexer.count(service3)).thenReturn(TEST_COUNT_3);

		// Create and initialize the test-object
		finder = new BasicServiceFinder();
		finder.setIndexer(indexer);
	}

	@Test
	public void testFind() {
		// Evaluate the result
		ServiceRegistration result;
		if (TEST_COUNT_1 < Math.min(TEST_COUNT_2, TEST_COUNT_3)) {
			result = registration1;
		} else {
			result = TEST_COUNT_2 < TEST_COUNT_3 ? registration2 : registration3;
		}

		// Call the test-method and verify the result
		assertEquals(result, finder.find(rule));
	}
}
