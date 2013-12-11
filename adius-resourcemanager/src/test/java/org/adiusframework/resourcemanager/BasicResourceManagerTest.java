package org.adiusframework.resourcemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.adiusframework.resource.AbstractResource;
import org.adiusframework.resource.Converter;
import org.adiusframework.resource.ConverterManager;
import org.adiusframework.resource.FileResource;
import org.adiusframework.resource.FileResourceOperations;
import org.adiusframework.resource.Generator;
import org.adiusframework.resource.GeneratorManager;
import org.adiusframework.resource.Resource;
import org.adiusframework.resource.ResourceCapabilityRule;
import org.adiusframework.resource.ResourceTypeMapper;
import org.adiusframework.resource.Transient;
import org.adiusframework.util.datastructures.SystemData;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FileResourceOperations.class)
public class BasicResourceManagerTest {
	// Some constant test-properties
	private static final String TEST_URI = "test.uri";
	private static final String TEST_TYPE = "testType";
	private static final String TEST_PROTOCOL = "testProtocol";
	private static final String TEST_PROTOCOL_1 = "testProtocol1";
	private static final String TEST_PROTOCOL_2 = "testProtocol2";

	// Lists of used types and protocols
	private static final List<String> TEST_TYPES = new LinkedList<String>();
	private static final List<String> TEST_PROTOCOLS = new LinkedList<String>();

	// Add the test-types and -protocols to their list
	static {
		TEST_TYPES.add(TEST_TYPE);
		TEST_PROTOCOLS.add(TEST_PROTOCOL);
		TEST_PROTOCOLS.add(TEST_PROTOCOL_1);
		TEST_PROTOCOLS.add(TEST_PROTOCOL_2);
	}

	// Dummy-objects which are passed through the test-methods
	private ResourceCapabilityRule rule;
	private Properties queryDomainData;
	private UUID uuid;

	// This object are dependent and therefore mocked
	private SystemData systemData;
	private Resource resource;
	private List<Resource> resourceList;
	private ResourceContainer container;
	private ResourceQuery query;
	private Converter<?, ?> converter;
	private List<Converter<?, ?>> converterList;
	@SuppressWarnings("rawtypes")
	private Generator generator;

	// The infrastructure-objects around the ResourceManager
	private ResourceRepository repository;
	private ConverterManager converterManager;
	private GeneratorManager generatorManager;
	private ResourceTypeMapper resourceTypeMapper;

	// The tested BasicResourceManager
	private BasicResourceManager manager;

	@SuppressWarnings("unchecked")
	@Before
	public void init() {
		// Create the dummy objects
		rule = mock(ResourceCapabilityRule.class);
		queryDomainData = mock(Properties.class);
		uuid = UUID.randomUUID();

		// Create and initialize the dependent objects which default behavior...
		systemData = mock(SystemData.class);
		when(systemData.validate(systemData)).thenReturn(true);

		// ... a default Resource which is added to the resource-list
		resource = mock(Resource.class);
		when(resource.getType()).thenReturn(TEST_TYPE);
		when(resource.getProtocol()).thenReturn(TEST_PROTOCOL);
		when(resource.getSystemData()).thenReturn(systemData);
		when(resource.getUUID()).thenReturn(uuid);
		resourceList = new LinkedList<Resource>();
		resourceList.add(resource);

		// ... the ResourceContainer and ResourceQuery which are used in the
		// test-cases
		container = mock(ResourceContainer.class);
		when(container.addResource(resource)).thenReturn(true);
		when(container.getResources(eq(rule), anyBoolean())).thenReturn(resourceList);
		when(container.getResources(eq(rule), eq(queryDomainData), anyBoolean())).thenReturn(resourceList);
		query = mock(ResourceQuery.class);
		when(query.getTypes()).thenReturn(TEST_TYPES);
		when(query.getProtocols()).thenReturn(TEST_PROTOCOLS);
		when(query.getCapabilityRule()).thenReturn(rule);
		when(query.getQueryDomainData()).thenReturn(queryDomainData);
		when(query.getSystemRequirement()).thenReturn(systemData);

		// ... a default converter which is added to the converter-list
		converter = mock(Converter.class);
		when(converter.convert(resource, resource)).thenReturn(resource);
		converterList = new LinkedList<Converter<?, ?>>();
		converterList.add(converter);

		// ... a generator which default behavior
		generator = mock(Generator.class);
		when(generator.generate(resource)).thenReturn(resource);

		// Mock and stub the infrastructure-objects
		repository = mock(ResourceRepository.class);
		when(repository.getContainer(eq(TEST_URI), anyBoolean())).thenReturn(container);
		when(repository.setContainer(TEST_URI)).thenReturn(container);
		converterManager = mock(ConverterManager.class);
		when(converterManager.getConverters(any(Class.class), any(Class.class), anyBoolean()))
				.thenReturn(converterList);
		generatorManager = mock(GeneratorManager.class);
		when(generatorManager.get(any(Class.class))).thenReturn(generator);
		resourceTypeMapper = mock(ResourceTypeMapper.class);
		doReturn(TestResource1.class).when(resourceTypeMapper).getClass(TEST_TYPE, TEST_PROTOCOL_1);
		doReturn(TestResource2.class).when(resourceTypeMapper).getClass(TEST_TYPE, TEST_PROTOCOL_2);

		// Create and initialize the test-object
		manager = spy(new BasicResourceManager());
		manager.setRepository(repository);
		manager.setConverterManager(converterManager);
		manager.setGeneratorManager(generatorManager);
		manager.setSystemData(systemData);
		manager.setResourceTypeMapper(resourceTypeMapper);

		doReturn(true).when(manager).validateResource(resource, systemData);
	}

	@Test
	public void testExistsResource() {
		// Call the test-method and verify it's result
		assertTrue(manager.existResource(TEST_URI, rule));
	}

	@Test
	public void testExistsResourceNoResource() {
		// Create this special test-case
		resourceList.remove(resource);

		// Call the test-method and verify it's result
		assertFalse(manager.existResource(TEST_URI, rule));
	}

	@Test
	public void testExistsResourceNoContainer() {
		// Create this special test-case
		when(repository.getContainer(eq(TEST_URI), anyBoolean())).thenReturn(null);

		// Call the test-method and verify it's result
		assertFalse(manager.existResource(TEST_URI, rule));
	}

	@Test
	public void testGetResource() {
		// Call the test-method and verify the result
		assertEquals(resource, manager.getResource(TEST_URI, query));
	}

	@Test
	public void testGetResourceWrongType() throws NonAccessibleResourceException {
		// Create specific test-case
		when(query.getTypes()).thenReturn(new LinkedList<String>());

		// Disable converting
		doReturn(null).when(manager).convertResource(eq(TEST_URI), eq(resource), eq(query), anyBoolean());

		// Call the test-method and verify the result
		assertNull(manager.getResource(TEST_URI, query));
	}

	@Test
	public void testGetResourceWrongProtocol() throws NonAccessibleResourceException {
		// Create specific test-case
		when(query.getProtocols()).thenReturn(new LinkedList<String>());

		// Disable converting
		doReturn(null).when(manager).convertResource(eq(TEST_URI), eq(resource), eq(query), anyBoolean());

		// Call the test-method and verify the result
		assertNull(manager.getResource(TEST_URI, query));
	}

	@Test
	public void testGetResourceNoContainer() {
		// Call the test-method and verify the result
		assertNull(manager.getResource("wrong.uri", query));
	}

	@Test
	public void testGetResourceConvertable() throws NonAccessibleResourceException {
		// Create specific test-case
		doReturn(false).when(manager).validateResource(resource, systemData);

		// Disable converting
		doReturn(resource).when(manager).convertResource(eq(TEST_URI), eq(resource), eq(query), anyBoolean());

		// Call the test-method and verify the result
		assertEquals(resource, manager.getResource(TEST_URI, query));
	}

	@Test
	public void testRegisterResoruce() throws NonAccessibleResourceException {
		// Call the test-method
		assertTrue(manager.registerResource(TEST_URI, resource));

		// Verify that the Resource was added to it's container
		verify(container, times(1)).addResource(resource);
	}

	@Test
	public void testRegisterResoruceException() {
		// Prepare the specific test-case
		doReturn(false).when(manager).validateResource(resource, systemData);

		// The execution of the test-method should end in a
		// NonAccessibleResourceException
		try {
			manager.registerResource(TEST_URI, resource);
			fail();
		} catch (NonAccessibleResourceException e) {
			// this is the desired behavior
		}
	}

	@Test
	public void testConvertResource() throws NonAccessibleResourceException {
		// Prepare the specific test-case
		doReturn(false).doReturn(true).when(manager).registerResource(TEST_URI, resource);

		// Call the test-method
		assertEquals(resource, manager.convertResource(TEST_URI, resource, query, true));

		// Verify that the Resource was added to the list of temporary Resources
		List<UUID> temporary = manager.getTemporaryResources();
		assertNotNull(temporary);
		assertEquals(1, temporary.size());
		assertEquals(uuid, temporary.get(0));

		// Verify that the first Resource was removed
		verify(manager, times(1)).removeResource(resource);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertResourceNoConverters() throws NonAccessibleResourceException {
		// Prepare the specific test-case
		when(converterManager.getConverters(any(Class.class), any(Class.class), anyBoolean())).thenReturn(
				new LinkedList<Converter<?, ?>>());

		// Execute the test-method and verify the result
		assertNull(manager.convertResource(TEST_URI, resource, query, true));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testConvertResourceNoGenerator() throws NonAccessibleResourceException {
		// Prepare the specific test-case
		when(generatorManager.get(any(Class.class))).thenReturn(null);

		// Execute the test-method and verify the result
		assertNull(manager.convertResource(TEST_URI, resource, query, true));
	}

	@Test
	public void testConvertResourceNonTransientException() {
		// Prepare the specific test-case
		when(systemData.validate(systemData)).thenReturn(false);
		List<String> protocols = new LinkedList<String>();
		protocols.add(TEST_PROTOCOL_1);
		when(query.getProtocols()).thenReturn(protocols);

		// The execution of the test-method should end in a
		// NonAccessibleResourceException
		try {
			manager.convertResource(TEST_URI, resource, query, true);
			fail();
		} catch (NonAccessibleResourceException e) {
			// this is the desired behavior
		}
	}

	@Test
	public void testConvertResourceNoToClassed() {
		// Prepare the specific test-case
		when(resourceTypeMapper.getClass(anyString(), anyString())).thenReturn(null);

		// The execution of the test-method should end in a
		// NonAccessibleResourceException
		try {
			manager.convertResource(TEST_URI, resource, query, true);
			fail();
		} catch (NonAccessibleResourceException e) {
			// this is the desired behavior
		}
	}

	@Test
	public void testConvertResourceSystemDataException() {
		// Prepare the specific test-case
		when(systemData.validate(systemData)).thenReturn(false);

		// The execution of the test-method should end in a
		// NonAccessibleResourceException
		try {
			manager.convertResource(TEST_URI, resource, query, true);
			fail();
		} catch (NonAccessibleResourceException e) {
			// this is the desired behavior
		}
	}

	private static final long TEST_BASE_VALUE = 42;

	@Test
	public void testUpdateResource() throws FileSystemException {
		// Prepare the initialization
		PowerMockito.mockStatic(FileResourceOperations.class);

		// Prepare a File Resource which is the base-resource
		FileContent baseFileContent = mock(FileContent.class);
		when(baseFileContent.getLastModifiedTime()).thenReturn(TEST_BASE_VALUE);
		FileObject baseFileObject = mock(FileObject.class);
		when(baseFileObject.exists()).thenReturn(true);
		when(baseFileObject.getContent()).thenReturn(baseFileContent);

		FileResource baseResource = mock(TestResource2.class);
		resourceList.add(baseResource);
		when(FileResourceOperations.resolve(baseResource)).thenReturn(baseFileObject);

		// Prepare another Resource
		FileContent anotherFileContent = mock(FileContent.class);
		when(anotherFileContent.getLastModifiedTime()).thenReturn(TEST_BASE_VALUE + 1);
		FileObject anotherFileObject = mock(FileObject.class);
		when(anotherFileObject.exists()).thenReturn(true);
		when(anotherFileObject.getContent()).thenReturn(anotherFileContent);

		FileResource anotherResource = mock(TestResource2.class);
		resourceList.add(anotherResource);
		when(FileResourceOperations.resolve(anotherResource)).thenReturn(anotherFileObject);

		// Call the test-method
		manager.updateResources(TEST_URI, rule);

		// Verify that the Resource was converted to the BaseResource
		verify(converter, times(1)).convert(baseResource, anotherResource);
	}

	/**
	 * This class is used for the tests because it's impossible to stub the
	 * getClass() method of a mock.
	 */
	private static class TestResource1 extends AbstractResource {
		private static final long serialVersionUID = -8601956525355698946L;
	}

	/**
	 * This class is used for the tests because it's impossible to stub the
	 * getClass() method of a mock.
	 */
	@Transient
	private static class TestResource2 extends FileResource {
		private static final long serialVersionUID = -8601956525355698946L;

		@Override
		public String getUrlForm() {
			return null;
		}
		
		@Override
		public boolean supportsInputStream() {
			return false;
		}

		@Override
		public InputStream openInputStream() throws IOException {
			return null;
		}

	}
	
}
