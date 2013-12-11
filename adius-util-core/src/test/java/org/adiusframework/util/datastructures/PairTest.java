package org.adiusframework.util.datastructures;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PairTest {
	private String a = "A", b = "B", c = "C";

	private Pair<String, String> testPair;

	@Before
	public void init() {
		testPair = new Pair<String, String>(a, b);
	}

	@Test
	public void testEqualsNormal() {
		assertTrue(testPair.equals(new Pair<String, String>(a, b)));
	}

	@Test
	public void testEqualsWrongGenerics() {
		assertFalse(testPair.equals(new Pair<Object, Object>(new Object(), new Object())));
	}

	@Test
	public void testEqualsNotEqual() {
		assertFalse(testPair.equals(new Pair<String, String>(a, c)));
	}
}
