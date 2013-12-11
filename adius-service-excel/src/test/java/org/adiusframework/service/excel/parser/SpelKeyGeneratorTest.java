package org.adiusframework.service.excel.parser;

import static org.junit.Assert.assertEquals;

import org.adiusframework.service.excel.exception.ParserConfigException;
import org.adiusframework.service.excel.parser.cache.SpelKeyGenerator;
import org.junit.Test;

public class SpelKeyGeneratorTest {

	@Test
	public void testGenerate() {
		SpelKeyGenerator generator = new SpelKeyGenerator("#object.value");
		assertEquals("TestValue", generator.generate(new TestElement("TestValue")));
		assertEquals("TestValue2", generator.generate(new TestElement("TestValue2")));
		
		generator = new SpelKeyGenerator("#test.value", "test");
		assertEquals("TestValue", generator.generate(new TestElement("TestValue")));
		assertEquals("TestValue2", generator.generate(new TestElement("TestValue2")));
	}

	@Test(expected = ParserConfigException.class)
	public void testGenerateFail() {
		SpelKeyGenerator generator = new SpelKeyGenerator("#object.value2");
		assertEquals("TestValue", generator.generate(new TestElement("TestValue")));
		assertEquals("TestValue2", generator.generate(new TestElement("TestValue2")));
	}

	private class TestElement {

		private String value;

		public TestElement(String value) {
			this.value = value;
		}

		@SuppressWarnings("unused")
		public String getValue() {
			return value;
		}

	}

}
