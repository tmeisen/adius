package org.adiusframework.util.datastructures;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.adiusframework.util.exception.UnexpectedException;
import org.adiusframework.util.testing.TestUtil;
import org.junit.Before;
import org.junit.Test;

public class PropertiesTest {
	private static final String PROPERTIE_A = "PropertieA";
	private static final String VALUE_A = "ValueA";
	private static final String PROPERTIE_B = "PropertieB";
	private static final String VALUE_B = "ValueB";
	private static final String PROPERTIE_C = "PropertieC";
	private static final String VALUE_C = "OPTIONAL";

	private Properties properties;

	@Before
	public void init() {
		properties = new Properties();
		properties.setProperties(PROPERTIE_A, VALUE_A);
	}

	@Test
	public void testSetPropertiesException() {
		try {
			properties.setProperties(PROPERTIE_A);
			fail("Exception not raised");
		} catch (UnexpectedException e) {
			// nothing to do here
		}
	}

	@Test
	public void testContainsKeyTrue() {
		assertTrue(properties.containsKey(PROPERTIE_A));
	}

	@Test
	public void testContainsKeyFalse() {
		assertFalse(properties.containsKey(PROPERTIE_B));
	}

	@Test
	public void testSetOptionalProperties() {
		this.properties.setOptionalProperties(TestUtil.mockProperties(PROPERTIE_B, VALUE_B, PROPERTIE_C, VALUE_C));

		assertTrue(this.properties.containsKey(PROPERTIE_B));
		assertFalse(this.properties.containsKey(PROPERTIE_C));
	}
}
