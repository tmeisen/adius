package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import org.adiusframework.processmanager.domain.ServiceSubTask;
import org.adiusframework.processmanager.domain.ServiceSubTaskProperty;
import org.adiusframework.processmanager.domain.ServiceTask;
import org.adiusframework.processmanager.domain.dao.ServiceTaskDao;
import org.adiusframework.resource.serviceplan.ServiceProcessPlan;
import org.adiusframework.resource.serviceplan.ServiceTemplate;
import org.adiusframework.util.testing.TestUtil;
import org.junit.Before;
import org.junit.Test;

public class BasicProcessManagerRepositoryTest {
	// Some test-constants
	private static final String TEST_SUB_CATEGORY = "testCategory";
	private static final String TEST_PROPERTY = "testProperty";
	private static final String TEST_VALUE = "testValue";

	// The Data-Access-Objects, which interact with the
	// BasicProcessManagerRepository
	private ServiceTaskDao taskDao;

	// This object will be tested
	private BasicProcessManagerRepository repository;

	@Before
	public void init() {
		taskDao = mock(ServiceTaskDao.class);

		// Create and initialize the test-object
		repository = new BasicProcessManagerRepository();
		repository.setServiceTaskDao(taskDao);
	}

	@Test
	public void testExtendTaskByServiceProcessPlan() {
		// Create a ServiceProcessPlan with it's related ServiceTemplate,
		// Properties and ServiceTask
		ServiceTask task = new ServiceTask();
		task.setSubTasks(new HashMap<Integer, ServiceSubTask>());

		Properties testProperties = TestUtil.mockProperties(TEST_PROPERTY, TEST_VALUE);
		ServiceTemplate template = mock(ServiceTemplate.class);
		when(template.getSubCategory()).thenReturn(TEST_SUB_CATEGORY);
		when(template.getProperties()).thenReturn(testProperties);
		ServiceProcessPlan plan = mock(ServiceProcessPlan.class);
		when(plan.getLength()).thenReturn(1);
		when(plan.getServiceTemplate(0)).thenReturn(template);

		// Call the test-method with the
		repository.extendTaskByServiceProcessPlan(task, plan);

		// Verify that a SubTask with the used Properties was added
		assertEquals(1, task.getSubTasks().size());
		ServiceSubTask subTask = task.getSubTasks().values().iterator().next();
		assertEquals(subTask.getSubCategory(), TEST_SUB_CATEGORY);
		verify(taskDao, times(1)).saveOrUpdate(task);
		Set<ServiceSubTaskProperty> properties = subTask.getProperties();
		assertEquals(1, properties.size());
		ServiceSubTaskProperty property = properties.iterator().next();
		assertEquals(subTask, property.getSubTask());
		assertEquals(TEST_PROPERTY, property.getName());
		assertEquals(TEST_VALUE, property.getValue());
	}
}
