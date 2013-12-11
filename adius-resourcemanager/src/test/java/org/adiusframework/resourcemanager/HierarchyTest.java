package org.adiusframework.resourcemanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.adiusframework.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Hierarchy.class)
public class HierarchyTest {
	private static final String FIND_PARENT_URI_METHODNAME = "findParentUri";
	private static final String UPDATE_CHILDREN_METHODNAME = "updateChildren";
	private static final String UPDATE_PARENT_METHODNAME = "updateParent";

	private static final String ROOT = "root";
	private static final String TEST_PARENT_URI = "test.parent.uri";
	private static final String TEST_IDENTIFIER = "test";
	private static final String TEST_URI = TEST_PARENT_URI + "." + TEST_IDENTIFIER;

	// This dummy-object will be passed through the methods without interaction
	private HierarchyResourceContainer root;
	private HierarchyResourceContainer container;

	// This objects are needed for the test
	private ResourceContainerFactory factory;

	// Our test Hierarchy
	private Hierarchy hierarchy;

	@Before
	public void init() {
		// Create the dummy-mock
		root = mock(HierarchyResourceContainer.class);
		container = mock(HierarchyResourceContainer.class);

		// Create the related objects as mocks
		factory = mock(ResourceContainerFactory.class);
		when(factory.create(ROOT)).thenReturn(root);

		// Create the test-object
		hierarchy = PowerMockito.spy(new Hierarchy(factory));

		// Verify that the test-object was created correct
		assertEquals(root, hierarchy.getRoot());
	}

	@Test
	public void testGetContainer() {
		// Inject a test-container into the hierarchy
		assertEquals(container, hierarchy.addContainer(TEST_URI, container));

		// Call the test-method and verify the result
		assertEquals(container, hierarchy.getContainer(TEST_URI, true));
	}

	@Test
	public void testGetContainerParent() {
		// Inject a test-container into the hierarchy with parent uri
		assertEquals(container, hierarchy.addContainer(TEST_PARENT_URI, container));

		// Call the test-method and verify the result
		assertEquals(container, hierarchy.getContainer(TEST_URI, true));
	}

	@Test
	public void testGetContainerWithoutParent() {
		// Inject a test-container into the hierarchy with parent uri
		assertEquals(container, hierarchy.addContainer(TEST_PARENT_URI, container));

		// Call the test-method and verify the result
		assertNull(hierarchy.getContainer(TEST_URI, false));
	}

	@Test
	public void testGetContainerRoot() {
		// Call the tested method and verify that the result is the root
		assertEquals(root, hierarchy.getContainer("", true));
	}

	@Test
	public void testAddContainer() throws Exception {
		when(factory.create(TEST_IDENTIFIER)).thenReturn(container);
		// Call the test-method and verify the result
		assertEquals(container, hierarchy.addContainer(TEST_URI));

		PowerMockito.verifyPrivate(hierarchy, times(1)).invoke(UPDATE_CHILDREN_METHODNAME, TEST_URI, container);
		PowerMockito.verifyPrivate(hierarchy, times(1)).invoke(UPDATE_PARENT_METHODNAME, TEST_URI, container);
	}

	@Test
	public void testAddContainerExisting() {
		// Inject a test-container into the hierarchy
		assertEquals(container, hierarchy.addContainer(TEST_URI, container));

		// Call the test-method and verify the result
		assertEquals(container, hierarchy.addContainer(TEST_URI));
	}

	@Test
	public void testAddContainerRoot() {
		// Call the tested method and verify that the result is the root
		assertEquals(root, hierarchy.addContainer(""));
	}

	@Test
	public void testFindParentUri() throws Exception {
		// Prepare the test-object
		hierarchy.addContainer(TEST_PARENT_URI, container);

		// Call the (private) test-method and verify it's result
		assertEquals(TEST_PARENT_URI, Whitebox.invokeMethod(hierarchy, FIND_PARENT_URI_METHODNAME, TEST_URI));
	}

	@Test
	public void testUpdateParent() throws Exception {
		// Prepare the test-object
		hierarchy.addContainer(TEST_PARENT_URI, container);

		// Call the (private) test-method
		Whitebox.invokeMethod(hierarchy, UPDATE_PARENT_METHODNAME, TEST_URI, container);

		// Verify that the parent was set correctly
		verify(container, times(1)).setParent(container);
	}

	@Test
	public void testUpdateParentRoot() throws Exception {
		// Call the (private) test-method
		Whitebox.invokeMethod(hierarchy, UPDATE_PARENT_METHODNAME, TEST_URI, container);

		// Verify that the parent was set correctly
		verify(container, times(1)).setParent(root);
	}

	// Some constants for the last tests
	private static final String TEST_MID_PARENT_URI = "prevParent.newParent";
	private static final String TEST_CHILD_A_URI = TEST_MID_PARENT_URI + ".childA";
	private static final String TEST_CHILD_B_URI = TEST_MID_PARENT_URI + ".childB";
	private static final String TEST_CHILD_C_URI = TEST_MID_PARENT_URI + ".childC";

	@Test
	public void testUpdateChildren() throws Exception {
		// Prepare the test-object
		hierarchy.addContainer(TEST_CHILD_A_URI, container);
		hierarchy.addContainer(TEST_CHILD_B_URI, container);
		hierarchy.addContainer(TEST_CHILD_C_URI, container);

		// Call the (private) test-method
		Whitebox.invokeMethod(hierarchy, UPDATE_CHILDREN_METHODNAME, "prevParent.newParent", container);

		verify(container, times(3)).setParent(container);
	}

	@Test
	public void testRemoveContainers() throws Exception {
		// Create a dummy list of Resource objects
		Resource resource = mock(Resource.class);
		List<Resource> resourceList = new LinkedList<Resource>();
		resourceList.add(resource);

		// Stub "removeContainer" for our test object
		when(hierarchy.removeContainer(anyString())).thenReturn(resourceList);

		// Prepare the test-object
		hierarchy.addContainer(TEST_MID_PARENT_URI, container);
		hierarchy.addContainer(TEST_CHILD_A_URI, container);
		hierarchy.addContainer(TEST_CHILD_B_URI, container);
		hierarchy.addContainer(TEST_CHILD_C_URI, container);

		// Call the test method
		List<Resource> result = hierarchy.removeContainers(TEST_MID_PARENT_URI, true);

		// Verify that the dummy resource object was added to the result once
		// for every removed container
		assertNotNull(result);
		assertEquals(4, result.size());
		assertEquals(resource, result.get(0));
		assertEquals(resource, result.get(1));
		assertEquals(resource, result.get(2));
		assertEquals(resource, result.get(3));
	}

	@Test
	public void testRemoveContainer() throws Exception {
		// Create a dummy list of Resource objects
		Resource resource = mock(Resource.class);
		List<Resource> resourceList = new LinkedList<Resource>();
		resourceList.add(resource);

		// Add the dummy list to the container
		when(container.getAllResources()).thenReturn(resourceList);

		// Prepare the test-object
		hierarchy.addContainer(TEST_MID_PARENT_URI, container);
		PowerMockito.doNothing().when(hierarchy, UPDATE_CHILDREN_METHODNAME, any(String.class),
				any(HierarchyResourceContainer.class));

		// Call the test-method
		List<Resource> result = hierarchy.removeContainer(TEST_MID_PARENT_URI);

		// Verify that the dummy Resource was passed through
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(resource, result.get(0));

		// That the parent-child-structure was updated
		PowerMockito.verifyPrivate(hierarchy, times(1)).invoke(UPDATE_CHILDREN_METHODNAME, TEST_MID_PARENT_URI,
				container);
	}
}
