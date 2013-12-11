package org.adiusframework.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ArrayUtilTest {
	private static int MAX_SIZE = 100;

	private Integer[] testArray;

	private int indexA, indexB;

	@Before
	public void init() {
		testArray = new Integer[(int) (Math.random() * MAX_SIZE)];
		for (int i = 0; i < testArray.length; i++) {
			int value = (int) (Math.random() * Integer.MAX_VALUE);
			testArray[i] = value;
		}

		indexA = (int) (Math.random() * testArray.length);
		indexB = (int) (Math.random() * testArray.length);
	}

	@Test
	public void testSwap() {
		Integer[] original = testArray.clone();
		ArrayUtil.swap(testArray, indexA, indexB);

		assertEquals(testArray[indexA], original[indexB]);
		assertEquals(testArray[indexB], original[indexA]);
	}

	@Test
	public void testContains() {
		assertTrue(ArrayUtil.contains(testArray, testArray[indexA]));
	}
}
