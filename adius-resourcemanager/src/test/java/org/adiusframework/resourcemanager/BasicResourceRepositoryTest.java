package org.adiusframework.resourcemanager;

import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.adiusframework.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(BasicResourceRepository.class)
public class BasicResourceRepositoryTest {
	private static final String TEST_ID_A = "testIdA";
	private static final String TEST_ID_B = "testIdB";

	// A infrastructure-objects that the repository needs to work
	private Hierarchy hierarchy;

	// A dummy-object which is passed through the constructor
	private ResourceContainerFactory factory;

	// The test-resources-set
	private Resource resource_A_A, resource_A_B, resource_B_A, resource_B_B;
	private Map<String, List<Resource>> resources;
	private ResourceContainer containerA, containerB;

	// The test-object
	private BasicResourceRepository repository;

	@Before
	public void init() throws Exception {
		// Create our test-resources-set
		resource_A_A = mock(Resource.class);
		resource_A_B = mock(Resource.class);
		resource_B_A = mock(Resource.class);
		resource_B_B = mock(Resource.class);
		List<Resource> resourcesA = new LinkedList<Resource>();
		resourcesA.add(resource_A_A);
		resourcesA.add(resource_A_B);
		List<Resource> resourcesB = new LinkedList<Resource>();
		resourcesB.add(resource_B_A);
		resourcesB.add(resource_B_B);
		resources = new HashMap<String, List<Resource>>();
		resources.put(TEST_ID_A, resourcesA);
		resources.put(TEST_ID_B, resourcesB);

		containerA = mock(ResourceContainer.class);
		containerB = mock(ResourceContainer.class);

		// Create the dummy
		factory = mock(ResourceContainerFactory.class);

		// Initialize the infrastructure
		hierarchy = mock(Hierarchy.class);
		when(hierarchy.getContainer(anyString(), anyBoolean())).thenReturn(null);
		when(hierarchy.addContainer(TEST_ID_A)).thenReturn(containerA);
		when(hierarchy.addContainer(TEST_ID_B)).thenReturn(containerB);
		PowerMockito.whenNew(Hierarchy.class).withArguments(factory).thenReturn(hierarchy);

		// Crate the test-object and inject the infrastructure
		repository = new BasicResourceRepository(factory);
	}

	@Test
	public void testSetContainers() {
		// Call the test-method
		repository.setContainers(resources);

		// Verify that the test-set was processed correctly
		verify(hierarchy, times(1)).addContainer(TEST_ID_A);
		verify(hierarchy, times(1)).addContainer(TEST_ID_B);
		verify(containerA, times(1)).addResource(resource_A_A);
		verify(containerA, times(1)).addResource(resource_A_B);
		verify(containerB, times(1)).addResource(resource_B_A);
		verify(containerB, times(1)).addResource(resource_B_B);
	}
}
