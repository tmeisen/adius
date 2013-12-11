package org.adiusframework.resourcemanager;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.adiusframework.resource.ResourceCapability;
import org.adiusframework.resource.ResourceCapabilityRule;
import org.junit.Before;
import org.junit.Test;

/**
 * TODO Test anpassen wenn der OntologyCapabilityRuleValidator ergänzt wurde
 */
public class OntologyCapabilityRuleValidatorTest {
	// Dummy-objects
	private Properties execConditions;
	private ResourceCapability capability;

	// Mocks which are used during the test
	private ResourceCapabilityRule rule;

	// The tested object
	private OntologyCapabilityRuleValidator validator;

	@Before
	public void init() {
		// Create our test-related object
		execConditions = mock(Properties.class);
		capability = mock(ResourceCapability.class);
		rule = mock(ResourceCapabilityRule.class);
		when(rule.satisfiedBy(capability)).thenReturn(true);

		// Create our test-object
		validator = new OntologyCapabilityRuleValidator();
	}

	@Test
	public void testIsSatisfied() {
		// Call the test-method and test it's return value
		assertTrue(validator.isSatisfied(rule, execConditions, capability));
	}
}
