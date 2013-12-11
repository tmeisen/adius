package org.adiusframework.resourcemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.adiusframework.resource.AbstractConverter;
import org.adiusframework.resource.AbstractGenerator;
import org.adiusframework.resource.AbstractResource;
import org.adiusframework.resource.BasicResourceTypeMapper;
import org.adiusframework.resource.ConfigurableConverterManager;
import org.adiusframework.resource.ConfigurableGeneratorManager;
import org.adiusframework.resource.Converter;
import org.adiusframework.resource.Generator;
import org.adiusframework.resource.Resource;
import org.adiusframework.resource.StringResourceCapability;
import org.adiusframework.service.xml.ResourceRequirement;
import org.adiusframework.util.datastructures.Pair;
import org.adiusframework.util.datastructures.SystemData;
import org.adiusframework.util.testing.AdiusIntegrationTest;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;

public class ResourceManagerIntegrationTest extends AdiusIntegrationTest<ResourceManager> {
	// Some constants for the test
	private static final String TEST_PROTOCOL = "protocol1";
	private static final String TEST_PROTOCOL_2 = "protocol2";
	private static final String TEST_TYPE = "type1";
	private static final String TEST_TYPE_2 = "type2";
	private static final String TEST_RULE = "testRule";
	private static final String TEST_URI = "test";

	private static final String[] TEST_PROTOCOLS = new String[] { TEST_PROTOCOL, TEST_PROTOCOL, TEST_PROTOCOL_2,
			TEST_PROTOCOL_2 };
	private static final String[] TEST_TYPES = new String[] { TEST_TYPE, TEST_TYPE, TEST_TYPE_2, TEST_TYPE_2 };
	private static final String[] TEST_RULES = new String[] { TEST_RULE, "rule2", "rule3", "rule4" };
	private static final String[] TEST_URIS = new String[] { TEST_URI, "test/test", "test/testUri/uri",
			"another/test/uri" };

	// Two test resources that are used to test the connection with the
	// GenratorManager and ConverterManager
	protected static final TestResource TEST_GENERATED_RESOURCE = new TestResource();
	protected static final TestResource TEST_CONVERTED_RESOURCE = new TestResource();

	/**
	 * The proxy object that represents the tested ResourceManager. It is set by
	 * the super-type.
	 */
	private ResourceManager manager;

	@Override
	protected void setApplication(ResourceManager application) {
		manager = application;
	}

	@Override
	protected void doSpecificConfiguration(AbstractApplicationContext context) {
		// Set up a mapping for the TestResource-class
		ConcurrentMap<Pair<String, String>, Class<? extends Resource>> testMapping = new ConcurrentHashMap<Pair<String, String>, Class<? extends Resource>>();
		testMapping.put(new Pair<String, String>(TEST_TYPE, TEST_PROTOCOL), TestResource.class);
		context.getBean("resourceTypeMapper", BasicResourceTypeMapper.class).setMapping(testMapping);

		// Add a generator for the TestResource
		ConcurrentMap<Class<? extends Resource>, Generator<? extends Resource>> testGeneratorMapping = new ConcurrentHashMap<Class<? extends Resource>, Generator<? extends Resource>>();
		testGeneratorMapping.put(TestResource.class, new AbstractGenerator<TestResource>() {
			@Override
			public TestResource generate(Resource base) {
				return TEST_GENERATED_RESOURCE;
			}
		});
		context.getBean("generatorManager", ConfigurableGeneratorManager.class).setMapping(testGeneratorMapping);

		// Add a converter for the TestResource
		List<Converter<?, ?>> testConverters = new LinkedList<Converter<?, ?>>();
		testConverters.add(new AbstractConverter<TestResource, TestResource>() {
			@Override
			protected TestResource typeSafeConvert(TestResource from, TestResource to) {
				if (to.equals(TEST_GENERATED_RESOURCE))
					return TEST_CONVERTED_RESOURCE;

				return null;
			}
		});
		context.getBean("converterManager", ConfigurableConverterManager.class).setConverters(testConverters);
	}

	/**
	 * The main test method where several resources where added, deleted due to
	 * the according test-case.
	 * 
	 * @throws NonAccessibleResourceException
	 *             This exception if passed through the method if it happens and
	 *             the test faisl because of that.
	 */
	@Test
	public void testResourceManager() throws NonAccessibleResourceException {
		// Initialize some domain objects
		SystemData systemData = new SystemData();
		ResourceRequirement defaultRequirement = new ResourceRequirement();
		defaultRequirement.setCapabilityRule(TEST_RULE);
		defaultRequirement.setProtocols(TEST_PROTOCOL);
		defaultRequirement.setTypes(TEST_TYPE);
		StandardResourceQuery defaultQuery = new StandardResourceQuery(defaultRequirement, systemData);

		// Register the test resources
		TestResource[] resources = new TestResource[TEST_URIS.length];
		for (int i = 0; i < TEST_URIS.length; i++) {
			resources[i] = new TestResource();
			resources[i].setProtocol(TEST_PROTOCOLS[i]);
			resources[i].setType(TEST_TYPES[i]);
			resources[i].setCapability(new StringResourceCapability(TEST_RULES[i]));
			resources[i].setSystemData(systemData);
			manager.registerResource(TEST_URIS[i], resources[i]);
		}

		// Check if the methods abort correctly
		assertNull(manager.getResource(null, defaultQuery));
		assertNull(manager.getResource(TEST_URI, null));

		// Check if the resources where registered correctly
		for (int i = 0; i < TEST_URIS.length; i++) {
			ResourceRequirement resourceRequirement = new ResourceRequirement();
			resourceRequirement.setCapabilityRule(TEST_RULES[i]);
			resourceRequirement.setProtocols(TEST_PROTOCOL + "," + TEST_PROTOCOL_2);
			resourceRequirement.setTypes(TEST_TYPE + "," + TEST_TYPE_2);
			assertEquals(resources[i],
					manager.getResource(TEST_URIS[i], new StandardResourceQuery(resourceRequirement, systemData)));
		}

		// Check if the correct resources were returned according to a specific
		// type
		for (int i = 0; i < TEST_URIS.length; i++) {
			ResourceRequirement resourceRequirement = new ResourceRequirement();
			resourceRequirement.setCapabilityRule(TEST_RULES[i]);
			resourceRequirement.setProtocols(TEST_PROTOCOL + "," + TEST_PROTOCOL_2);
			resourceRequirement.setTypes(TEST_TYPE);
			assertEquals(TEST_TYPES[i].equals(TEST_TYPE), resources[i].equals(manager.getResource(TEST_URIS[i],
					new StandardResourceQuery(resourceRequirement, systemData))));
		}

		// Check if the correct resources were returned according to a specific
		// protocol
		for (int i = 0; i < TEST_URIS.length; i++) {
			ResourceRequirement resourceRequirement = new ResourceRequirement();
			resourceRequirement.setCapabilityRule(TEST_RULES[i]);
			resourceRequirement.setProtocols(TEST_PROTOCOL);
			resourceRequirement.setTypes(TEST_TYPE + "," + TEST_TYPE_2);
			assertEquals(TEST_PROTOCOLS[i].equals(TEST_PROTOCOL), resources[i].equals(manager.getResource(TEST_URIS[i],
					new StandardResourceQuery(resourceRequirement, systemData))));
		}

		// Unregister the default resource and test if this was successfully
		manager.unregisterResources(TEST_URI);
		assertNull(manager.getResource(TEST_URI, defaultQuery));

		// Register a new default resource that must be converted
		TestResource resource = new TestResource();
		resource.setCapability(new StringResourceCapability(TEST_RULE));
		resource.setSystemData(systemData);
		manager.registerResource(TEST_URI, resource);

		// Test if the resources were converted correctly
		assertEquals(TEST_CONVERTED_RESOURCE, manager.getResource(TEST_URI, defaultQuery));

		// Remove all resources
		for (int i = 0; i < TEST_URIS.length; i++) {
			manager.unregisterResources(TEST_URIS[i]);
		}

		// Test if there are resources registered
		for (int i = 0; i < TEST_URIS.length; i++) {
			ResourceRequirement resourceRequirement = new ResourceRequirement();
			resourceRequirement.setCapabilityRule(TEST_RULES[i]);
			resourceRequirement.setProtocols(TEST_PROTOCOL + "," + TEST_PROTOCOL_2);
			resourceRequirement.setTypes(TEST_TYPE + "," + TEST_TYPE_2);
			assertNull(manager.getResource(TEST_URIS[i], new StandardResourceQuery(resourceRequirement, systemData)));
		}
	}

	/**
	 * A simple implementation of the abstract class AbstractResource that is
	 * used for test-purpose.
	 */
	private static class TestResource extends AbstractResource {
		private static final long serialVersionUID = -7101918459923333797L;

		public TestResource() {
		}

		@Override
		public void setSystemData(SystemData systemData) {
			super.setSystemData(systemData);
		}

		@Override
		public void setProtocol(String protocols) {
			super.setProtocol(protocols);
		}

		@Override
		public void setType(String types) {
			super.setType(types);
		}
	}
}
