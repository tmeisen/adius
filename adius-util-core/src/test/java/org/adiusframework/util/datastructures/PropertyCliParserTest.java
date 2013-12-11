package org.adiusframework.util.datastructures;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PropertyCliParserTest {
	private static final String KEY_A = "A";
	private static final String KEY_B = "B";
	private static final String VALUE_A = "valueA";
	private static final String VALUE_B = "valueB";

	@Test
	public void testParse() {
		Properties properties = PropertyCliParser.parse("-" + KEY_A + ":" + VALUE_A, "-" + KEY_B + " : \"" + VALUE_B
				+ "\"");

		assertTrue(properties.containsKey(KEY_A));
		assertTrue(properties.containsKey(KEY_B));
		assertEquals(properties.get(KEY_A), VALUE_A);
		assertEquals(properties.get(KEY_B), VALUE_B);
	}
}
