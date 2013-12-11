package org.adiusframework.resourcemanager;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.adiusframework.resource.Resource;
import org.adiusframework.resource.ResourceCapability;
import org.adiusframework.resource.ResourceCapabilityRule;
import org.adiusframework.util.datastructures.SystemData;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BasicResourceContainerTest {
	private static final int RESOURCE_ARRAY_LENGTH = 10;
	private static final int TEST_OBJECT_MATCHING_INDEX = 5;
	private static final int PARENT_MATCHING_INDEX = 8;

	private static final String TEST_IDENTIFIER = "testId";

	// Some dummy objects which should be passed through our test-methods
	private ResourceCapabilityRule resourceCapabilityRule;
	private Properties execConditions;

	// The Resource objects which are added to the test-object
	private Resource[] resources;
	private ResourceCapability[] capabilities;

	// The object(s) which are needed to build up the "infrastructure" around
	// our test-object
	private CapabilityRuleValidator capabilityRuleValidator;
	private HierarchyResourceContainer parent;

	// The tested BasicResourceContainer object
	private BasicResourceContainer container;

	@Before
	public void init() {
		// Create the dummy objects
		resourceCapabilityRule = mock(ResourceCapabilityRule.class);
		execConditions = mock(Properties.class);

		// Create the test-resources
		resources = new Resource[RESOURCE_ARRAY_LENGTH];
		capabilities = new ResourceCapability[RESOURCE_ARRAY_LENGTH];
		for (int i = 0; i < resources.length; i++) {
			resources[i] = mock(i == TEST_OBJECT_MATCHING_INDEX ? TestResourceClass.class : Resource.class);
			capabilities[i] = mock(ResourceCapability.class);
			when(resources[i].getCapability()).thenReturn(capabilities[i]);
		}

		// Create and stub the infrastructure-objects
		capabilityRuleValidator = mock(CapabilityRuleValidator.class);
		when(
				capabilityRuleValidator.isSatisfied(resourceCapabilityRule, execConditions,
						capabilities[TEST_OBJECT_MATCHING_INDEX])).thenReturn(true);
		parent = mock(HierarchyResourceContainer.class);
		List<Resource> parentList = new LinkedList<Resource>();
		parentList.add(resources[PARENT_MATCHING_INDEX]);
		when(parent.getResources(eq(resourceCapabilityRule), anyBoolean())).thenReturn(parentList);
		when(parent.getResourcesByClass(eq(TestResourceClass.class), anyBoolean())).thenReturn(parentList);

		// Create and initialize the tested object
		container = new BasicResourceContainer(TEST_IDENTIFIER, capabilityRuleValidator);
		container.setParent(parent);

		// Add the test-resources to our BasicResourceContainer
		for (int i = 0; i < resources.length; i++) {
			container.addResource(resources[i]);
		}
	}

	@Test
	public void testGetResourceFullscan() {
		// Call the test-method
		List<Resource> result = container.getResources(resourceCapabilityRule, execConditions, true);

		// Verify that both object, the test-object and the parent one, were
		// added
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(resources[TEST_OBJECT_MATCHING_INDEX]));
		assertTrue(result.contains(resources[PARENT_MATCHING_INDEX]));
	}

	@Test
	public void testGetResourceNoFullscan() {
		// Call the test-method
		List<Resource> result = container.getResources(resourceCapabilityRule, execConditions, false);

		// Verify that only the object, which is added by the test-object is
		// returned
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(resources[TEST_OBJECT_MATCHING_INDEX], result.get(0));
	}

	@Test
	public void testGetResourceNoMatchInChild() {
		// Now the test-object should not have any match
		when(
				capabilityRuleValidator.isSatisfied(eq(resourceCapabilityRule), eq(execConditions),
						any(ResourceCapability.class))).thenReturn(false);

		// Call the test-method
		List<Resource> result = container.getResources(resourceCapabilityRule, execConditions, false);

		// Verify that the returned List contains only the object from the
		// parent-ResourceContainer
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(resources[PARENT_MATCHING_INDEX], result.get(0));
	}

	@Test
	public void testGetResourcesByClassFullscan() {
		// Call the test-method
		List<Resource> result = container.getResourcesByClass(TestResourceClass.class, true);

		// Verify that both object, the test-object and the parent one, were
		// added
		assertNotNull(result);
		assertEquals(2, result.size());
		assertTrue(result.contains(resources[TEST_OBJECT_MATCHING_INDEX]));
		assertTrue(result.contains(resources[PARENT_MATCHING_INDEX]));
	}

	@Test
	public void testGetResourcesByClassNoFullscan() {
		// Call the test-method
		List<Resource> result = container.getResourcesByClass(TestResourceClass.class, false);

		// Verify that only the object, which is added by the test-object is
		// returned
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(resources[TEST_OBJECT_MATCHING_INDEX], result.get(0));
	}

	@Test
	public void testGetResourcesByClassNoMatchInChild() {
		// Remove the Resource-object, which is a instance of our test-class
		container.removeResource(resources[TEST_OBJECT_MATCHING_INDEX]);

		// Call the test-method
		List<Resource> result = container.getResourcesByClass(TestResourceClass.class, false);

		// Verify that the returned List contains only the object from the
		// parent-ResourceContainer
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(resources[PARENT_MATCHING_INDEX], result.get(0));
	}

	/**
	 * A class which is used to describe a special type of Resources.
	 * 
	 * (This way is used because of problems with mocking the Class<?>-class)
	 */
	private static class TestResourceClass implements Resource {
		private static final long serialVersionUID = -8675151216184491215L;

		@Override
		public UUID getUUID() {
			return null;
		}

		@Override
		public String getType() {
			return null;
		}

		@Override
		public String getProtocol() {
			return null;
		}

		@Override
		public boolean hasProtocol() {
			return false;
		}

		@Override
		public ResourceCapability getCapability() {
			return null;
		}

		@Override
		public SystemData getSystemData() {
			return null;
		}
	}
}
