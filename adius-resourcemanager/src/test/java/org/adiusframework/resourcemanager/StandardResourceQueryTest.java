package org.adiusframework.resourcemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.adiusframework.service.xml.ResourceRequirement;
import org.adiusframework.util.datastructures.SystemData;
import org.junit.Test;

public class StandardResourceQueryTest {
	private static final String TEST_CAPABILITY_RULE = "testCapabilityRule";
	private static final String TEST_TYPE_A = "testTypeA";
	private static final String TEST_TYPE_B = "testTypeB";
	private static final String TEST_TYPES = TEST_TYPE_A + "," + TEST_TYPE_B;
	private static final String TEST_PROTOCOL_A = "testProtocolA";
	private static final String TEST_PROTOCOL_B = "testProtocolB";
	private static final String TEST_PROTOCOLS = TEST_PROTOCOL_A + "," + TEST_PROTOCOL_B;

	// Only the constructor can respectively must be tested
	@Test
	public void testConstructor() {
		// Prepare some mocks to create a test object
		ResourceRequirement resourceRequirement = mock(ResourceRequirement.class);
		when(resourceRequirement.getCapabilityRule()).thenReturn(TEST_CAPABILITY_RULE);
		when(resourceRequirement.getTypes()).thenReturn(TEST_TYPES);
		when(resourceRequirement.getProtocols()).thenReturn(TEST_PROTOCOLS);
		SystemData systemData = mock(SystemData.class);

		// Create the test object with the prepared mocks
		StandardResourceQuery query = new StandardResourceQuery(resourceRequirement, systemData);

		// Verify that the query was created correct
		assertNotNull(query.getCapabilityRule());
		assertEquals(TEST_CAPABILITY_RULE, query.getCapabilityRule().toString());

		List<String> types = query.getTypes();
		assertNotNull(types);
		assertEquals(2, types.size());
		assertEquals(TEST_TYPE_A, types.get(0));
		assertEquals(TEST_TYPE_B, types.get(1));

		List<String> protocols = query.getProtocols();
		assertNotNull(protocols);
		assertEquals(2, protocols.size());
		assertEquals(TEST_PROTOCOL_A, protocols.get(0));
		assertEquals(TEST_PROTOCOL_B, protocols.get(1));

		assertEquals(systemData, query.getSystemRequirement());
		assertNotNull(query.getQueryDomainData());
		assertEquals(0, query.getQueryDomainData().size());
	}
}
