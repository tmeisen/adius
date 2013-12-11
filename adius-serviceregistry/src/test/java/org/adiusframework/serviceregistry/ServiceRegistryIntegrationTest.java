package org.adiusframework.serviceregistry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.adiusframework.service.BasicServiceRegistration;
import org.adiusframework.service.CategoryServiceCapabilityRule;
import org.adiusframework.service.ServiceRegistration;
import org.adiusframework.service.xml.Category;
import org.adiusframework.service.xml.ServiceCapability;
import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.adiusframework.util.datastructures.SystemData;
import org.adiusframework.util.testing.AdiusIntegrationTest;
import org.junit.Test;

public class ServiceRegistryIntegrationTest extends AdiusIntegrationTest<ServiceRegistry> {
	private static final Category[] TEST_CATEGORIES = new Category[] { Category.ANALYSIS, Category.DECOMPOSING,
			Category.EXTRACTION, Category.GENERATION, Category.INTEGRATION, Category.PLANNING, Category.TRANSFORMATION };
	private static final String[] TEST_SUB_CATEGORIES = new String[] { "SubCategory1", "SubCategory2", "SubCategory3" };

	private static final Category TEST_CATEGORY = Category.INTEGRATION;

	private List<UUID> uuids = new LinkedList<UUID>();

	private ServiceRegistry registry;

	@Override
	protected void setApplication(ServiceRegistry application) {
		registry = application;
	}

	/**
	 * A first simple test which registers a service at the tested application
	 * and checks if this service is return without any exception.
	 */
	@Test
	public void testServiceRegistry() {
		// Register the test services with no sub-category
		Hashtable<Category, ServiceRegistration> noSubcategory = new Hashtable<Category, ServiceRegistration>();
		for (Category category : TEST_CATEGORIES) {
			noSubcategory.put(category, registerService(category, null));
		}

		// Register the services with sub-category (all with the same category)
		Hashtable<String, ServiceRegistration> withSubcategory = new Hashtable<String, ServiceRegistration>();
		for (String subCategory : TEST_SUB_CATEGORIES) {
			withSubcategory.put(subCategory, registerService(TEST_CATEGORY, subCategory));
		}

		// Check if the services with no sub-category are registered and found
		// correctly
		for (Category category : TEST_CATEGORIES) {
			assertEquals(noSubcategory.get(category), registry.find(new CategoryServiceCapabilityRule(category)));
		}

		// Check if the services with sub-category are registered and found
		// correctly
		for (String subCategory : TEST_SUB_CATEGORIES) {
			assertEquals(withSubcategory.get(subCategory),
					registry.find(new CategoryServiceCapabilityRule(TEST_CATEGORY, subCategory)));
		}

		// Unregister all services one by one
		for (UUID uuid : uuids) {
			registry.unregister(uuid);
		}

		// Check if the services can't be found anymore
		for (Category category : TEST_CATEGORIES) {
			assertNull(registry.find(new CategoryServiceCapabilityRule(category)));
		}
	}

	/**
	 * Checks if two given ServiceRegistrations are equal.
	 * 
	 * @param a
	 *            The first ServiceRegistration.
	 * @param b
	 *            The second ServiceRegistration.
	 */
	private void assertEquals(ServiceRegistration a, ServiceRegistration b) {
		assertNotNull(a);
		assertNotNull(b);
		assertTrue(a.getProviderDefinition().equals(b.getProviderDefinition()));
		assertTrue(a.getServiceDefinition().equals(b.getServiceDefinition()));
		assertTrue(a.getSystemData().equals(b.getSystemData()));
	}

	private ServiceRegistration registerService(Category category, String subCategory) {
		// Create the test service
		ServiceProviderDefinition providerDefinition = new ServiceProviderDefinition();
		ServiceDefinition serviceDefinition = new ServiceDefinition();
		ServiceCapability capability = new ServiceCapability();
		capability.setCategory(category);
		capability.setSubcategory(subCategory);
		serviceDefinition.setCapability(capability);
		SystemData systemData = new SystemData();

		// Register the service at out tested ServiceRegistry
		UUID uuid = registry.register(providerDefinition, serviceDefinition, systemData);
		uuids.add(uuid);

		return new BasicServiceRegistration(providerDefinition, serviceDefinition, systemData);
	}
}
