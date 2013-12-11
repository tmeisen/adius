package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.adiusframework.processmanager.xml.ProcessDefinition;
import org.adiusframework.processmanager.xml.TaskType;
import org.adiusframework.processmanager.xml.TasksType;
import org.junit.Before;
import org.junit.Test;

public class SpringServiceProcessDefinitionTest {
	private static final String TEST_TASK_NAME = "testTask";
	private static final String SOME_OTHER_TASK_NAME = "someOtherTaskName";
	private static final String ANY_TASK_NAME = "anyTaskName";

	// The "data"-object which are processed by the tested obejct
	private TaskType testTask;
	private List<TaskType> taskList;

	// The tested ParsedServiceProcessDefinition
	private ParsedServiceProcessDefinition definition;

	@Before
	public void init() {
		// Create and initialize the data-objects
		testTask = mock(TaskType.class);
		when(testTask.getName()).thenReturn(TEST_TASK_NAME);

		TaskType someOtherTask = mock(TaskType.class);
		when(someOtherTask.getName()).thenReturn(SOME_OTHER_TASK_NAME);

		taskList = new LinkedList<TaskType>();
		taskList.add(someOtherTask);
		taskList.add(testTask);

		TasksType tasksType = mock(TasksType.class);
		when(tasksType.getTask()).thenReturn(taskList);

		// Create a ProcessDefinition as related object
		ProcessDefinition processDefinition = mock(ProcessDefinition.class);
		when(processDefinition.getTasks()).thenReturn(tasksType);

		// Create the test-object and inject the related object
		definition = new ParsedServiceProcessDefinition();
		definition.setProcessDefinition(processDefinition);
	}

	@Test
	public void testIndexOf() {
		assertEquals(taskList.indexOf(testTask), definition.indexOf(TEST_TASK_NAME));
	}

	@Test
	public void testIsFinalTaskTrue() {
		assertTrue(definition.isFinalTask(TEST_TASK_NAME));
	}

	@Test
	public void testIsFinalTaskFalse() {
		assertFalse(definition.isFinalTask(SOME_OTHER_TASK_NAME));
	}

	@Test
	public void testGetNextTask() {
		assertEquals(testTask, definition.getNextTask(SOME_OTHER_TASK_NAME));
	}

	@Test
	public void testGetNextTaskLastTask() {
		assertNull(definition.getNextTask(TEST_TASK_NAME));
	}

	@Test
	public void testGetNextTaskNotExistingTask() {
		assertNull(definition.getNextTask(ANY_TASK_NAME));
	}
}
