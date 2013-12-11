package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.adiusframework.query.Query;
import org.adiusframework.query.QueryAspect;
import org.adiusframework.resource.Resource;
import org.junit.Before;
import org.junit.Test;

public class MapQueryTypeMapperTest {
	private static final String TEST_SERVICE_PROCESS = "TestServiceProcess";

	// The tested MapQueryTypeMapper-object
	private MapQueryTypeMapper mapper;

	@SuppressWarnings("unchecked")
	@Before
	public void init() {
		// Create a test configuration (without mocks because of problems with
		// mocking Class<?> types)
		Set<Entry<Class<Query>, String>> set = new HashSet<Entry<Class<Query>, String>>();
		set.add(new Entry<Class<Query>, String>() {
			@SuppressWarnings("rawtypes")
			@Override
			public Class getKey() {
				return MappedTestQuery.class;
			}

			@Override
			public String getValue() {
				return TEST_SERVICE_PROCESS;
			}

			@Override
			public String setValue(String value) {
				return null;
			}
		});
		HashMap<Class<Query>, String> mapping = mock(HashMap.class);
		when(mapping.get(MappedTestQuery.class)).thenReturn(TEST_SERVICE_PROCESS);
		when(mapping.entrySet()).thenReturn(set);

		// Create the test-object and inject the test-configuration
		mapper = new MapQueryTypeMapper();
		mapper.setMapping(mapping);
	}

	@Test
	public void testGetTypeByQuery() {
		assertEquals(TEST_SERVICE_PROCESS, mapper.getTypeByQuery(new MappedTestQuery()));
	}

	@Test
	public void testGetTypeByQueryUnmapped() {
		// The processing of the unmapped query should end in a
		// NullPointerException
		try {
			mapper.getTypeByQuery(new UnmappedTestQuery());
			fail();
		} catch (NullPointerException exception) {
			// hence we are lucky if the exception occurs
		}
	}

	@Test
	public void testGetQueryByType() {
		assertEquals(MappedTestQuery.class, mapper.getQueryByType(TEST_SERVICE_PROCESS));
	}

	/**
	 * A test-query which will be mapped in our test-case
	 * 
	 * (The object can't replaced by a mock)
	 */
	protected static class MappedTestQuery extends UnmappedTestQuery {

		private static final long serialVersionUID = -7731642567528279395L;
	}

	/**
	 * A test-query which will not be mapped in our test-case
	 * 
	 * (The object can't replaced by a mock)
	 */
	protected static class UnmappedTestQuery implements Query {
		private static final long serialVersionUID = 9059852262149876449L;

		@Override
		public Properties getProperties() {
			return null;
		}

		@Override
		public String getDomain() {
			return null;
		}

		@Override
		public String getProcessIdentifier() {
			return null;
		}

		@Override
		public String getStepIdentifier() {
			return null;
		}

		@Override
		public String getId() {
			return null;
		}

		@Override
		public boolean hasAspect(QueryAspect aspect) {
			return false;
		}

		@Override
		public List<Resource> getAttachedResources() {
			return null;
		}
	}
}
