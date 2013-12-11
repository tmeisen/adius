package org.adiusframework.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringUtilTest {
	private static final String[] TEST_STRINGS = new String[] { "Test1", "Test2", "String3" };
	private static final String DELIMETER = ",";
	private static final String RESULT = "Test1,Test2,String3";
	private static final String NUMBER_SUFFIX_TEST_STRING = "testName_32.test";

	private static final String A = "a";
	private static final String B = "b";
	private static final String C = "c";
	private static final String TEST_URI = A + DELIMETER + B + DELIMETER + C;

	@Test
	public void testJoin() {
		assertEquals(StringUtil.join(TEST_STRINGS, DELIMETER), RESULT);
	}

	@Test
	public void testConcat() {
		assertEquals(StringUtil.concat((Object[]) TEST_STRINGS), RESULT);
	}

	@Test
	public void testShortenUriWithDelimeter() {
		assertEquals(StringUtil.shortenUri(TEST_URI, DELIMETER), A + DELIMETER + B);
	}

	@Test
	public void testShortenUriWithourDelimeter() {
		assertEquals(StringUtil.shortenUri(A, DELIMETER), "");
	}

	@Test
	public void testGetIdentifierInUriWithDelimeter() {
		assertEquals(StringUtil.getIdentifierInUri(TEST_URI, DELIMETER), C);
	}

	@Test
	public void testGetIdentifierInUriWithoutDelimeter() {
		assertEquals(StringUtil.getIdentifierInUri(A, DELIMETER), A);
	}

	@Test
	public void testNummerSuffix() {
		assertEquals(StringUtil.numberSuffix(NUMBER_SUFFIX_TEST_STRING, 0), 32);
	}
}
