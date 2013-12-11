package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class RegExpRELParserTest {
	private static final String TEST_RESOURCE_NAME = "testResource";
	private static final String TEST_REL_EXPRESSION = "${resource." + TEST_RESOURCE_NAME + "}";
	private static final String TEST_NO_MATCH_EXPRESSION = "{resource." + TEST_RESOURCE_NAME + "}";
	private static final String TEST_WRONG_PREFIX_EXPRESSION = "${prefix." + TEST_RESOURCE_NAME + "}";
	
	// The tested object
	private RegExpRELParser parser;
	
	@Before
	public void init() {
		parser = new RegExpRELParser();
	}
	
	@Test
	public void testParse() {
		assertEquals(TEST_RESOURCE_NAME, parser.parse(TEST_REL_EXPRESSION));
	}
	
	@Test
	public void testParseNoMatch() {
		assertNull(parser.parse(TEST_NO_MATCH_EXPRESSION));
	}
	
	@Test
	public void testParseWrongPrefix() {
		// The execution with a wrong prefix should end in an UnsupportedOperationException
		try {
			parser.parse(TEST_WRONG_PREFIX_EXPRESSION);
			fail();
		} catch(UnsupportedOperationException exception) {
			// hence we are lucky if the exception occurs
		}
	}
}
