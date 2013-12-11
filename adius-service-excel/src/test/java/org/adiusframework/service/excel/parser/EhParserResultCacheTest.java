package org.adiusframework.service.excel.parser;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.List;

import org.adiusframework.service.excel.parser.cache.CacheKey;
import org.adiusframework.service.excel.parser.cache.EhParserResultCache;
import org.junit.Test;

public class EhParserResultCacheTest implements Serializable {
	private static final long serialVersionUID = -6816707799661820201L;

	@Test
	public void testSetCache() {
		EhParserResultCache cache = new EhParserResultCache("/ehcache-scope-test.xml");
		cache.registerSetCache(TestEntity.class);
		TestEntity value = new TestEntity("Test");
		Serializable key = cache.put(value);
		assertTrue(key != null && !key.toString().isEmpty());
		Serializable returnValue = cache.get(key, TestEntity.class);
		assertSame(returnValue, value);
		cache.dispose();
	}

	@Test
	public void testKeyCache() {
		EhParserResultCache cache = new EhParserResultCache("/ehcache-scope-test.xml");
		cache.registerKeyCache(TestEntity.class);
		cache.registerKeyCache(TestEntity3.class, "#object.getValue()", "object");
		TestEntity value = new TestEntity("Test");
		Serializable key = cache.put(value);
		assertTrue(key != null && !key.toString().isEmpty() && key.toString().contains("Test"));
		Serializable returnValue = cache.get(key, TestEntity.class);
		assertSame(returnValue, value);
		cache.dispose();
	}

	@Test
	public void testSearchCache() {
		EhParserResultCache cache = new EhParserResultCache("/ehcache-scope-test.xml");
		cache.registerKeyCache(TestEntity.class);
		cache.registerKeyCache(TestEntity2.class);
		cache.registerKeyCache(TestEntity3.class, "#object.getValue()", "object");
		cache.put(new TestEntity("Test1"));
		cache.put(new TestEntity("Test2"));
		cache.put(new TestEntity("Test3"));
		cache.put(new TestEntity2("Test1"));
		cache.put(new TestEntity2("Test2"));
		cache.put(new TestEntity2("Test3"));
		cache.put(new TestEntity3("Test1"));
		cache.put(new TestEntity3("Test2"));
		cache.put(new TestEntity3("Test3"));
		List<TestEntity> results = cache.getAll(TestEntity.class);
		assertEquals(results.size(), 3);
		List<TestEntity3> results3 = cache.getAll(TestEntity3.class);
		assertEquals(results3.size(), 3);
		cache.dispose();
	}

	@CacheKey(value = "#object.getValue()", reference = "object")
	private class TestEntity implements Serializable {
		private static final long serialVersionUID = -876226496258447888L;

		private String value;

		public TestEntity(String value) {
			this.value = value;
		}

		@SuppressWarnings("unused")
		public String getValue() {
			return value;
		}

	}

	@CacheKey(value = "#object.getValue()", reference = "object")
	private class TestEntity2 implements Serializable {
		private static final long serialVersionUID = -876226496258447888L;

		private String value;

		public TestEntity2(String value) {
			this.value = value;
		}

		@SuppressWarnings("unused")
		public String getValue() {
			return value;
		}

	}

	private class TestEntity3 implements Serializable {
		private static final long serialVersionUID = -876226496258447888L;

		private String value;

		public TestEntity3(String value) {
			this.value = value;
		}

		@SuppressWarnings("unused")
		public String getValue() {
			return value;
		}

	}

}
