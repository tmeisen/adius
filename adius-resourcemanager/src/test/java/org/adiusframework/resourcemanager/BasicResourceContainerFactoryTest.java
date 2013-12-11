package org.adiusframework.resourcemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class BasicResourceContainerFactoryTest {
	private static final String TEST_IDENTIFIER = "testId";

	// A dummy-object which is passed through the methods
	private CapabilityRuleValidator capabilityRuleValidator;

	// The test-object
	private BasicResourceContainerFactory factory;

	@Before
	public void init() {
		// Create the dummy-object
		capabilityRuleValidator = mock(CapabilityRuleValidator.class);

		// Create and initialize the test-object
		factory = new BasicResourceContainerFactory();
		factory.setCapabilityRuleValidator(capabilityRuleValidator);
	}

	@Test
	public void testCreate() {
		// Call test-method
		HierarchyResourceContainer container = factory.create(TEST_IDENTIFIER);

		// Verify that the result was created correct
		assertTrue(container instanceof BasicResourceContainer);
		assertEquals(TEST_IDENTIFIER, container.getIdentifier());
		assertEquals(capabilityRuleValidator, ((BasicResourceContainer) container).getCapabilityRuleValidator());
	}
}
