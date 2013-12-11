package org.adiusframework.processmanager.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ServiceTaskTest {
	private ServiceSubTask subTaskA, subTaskB;
	
	private ServiceTask task;
	
	@Before
	public void init() {
		subTaskA = mock(ServiceSubTask.class);
		subTaskB = mock(ServiceSubTask.class);
		when(subTaskA.getStatus()).thenReturn(ServiceSubTaskStatus.CLOSED);
		when(subTaskB.getStatus()).thenReturn(ServiceSubTaskStatus.OPEN);
		
		Map<Integer, ServiceSubTask> subTasks = new HashMap<Integer, ServiceSubTask>();
		subTasks.put(1, subTaskA);
		subTasks.put(2, subTaskB);
		
		task = new ServiceTask();
		task.setSubTasks(subTasks);
	}
	
	@Test
	public void testIsCloseableTrue() {
		when(subTaskB.getStatus()).thenReturn(ServiceSubTaskStatus.CLOSED);
		
		assertTrue(task.isCloseable());
	}
	
	@Test
	public void testIsCloseableFalse() {
		assertFalse(task.isCloseable());
	}
	
	@Test
	public void testIsCloseableNoSubtasks() {
		task.setSubTasks(new HashMap<Integer, ServiceSubTask>());
		
		assertTrue(task.isCloseable());
	}
	
	@Test
	public void testIsCloseableNull() {
		task.setSubTasks(null);
		
		assertTrue(task.isCloseable());
	}
	
	@Test
	public void testGetNextOpenSubTask() {
		assertEquals(subTaskB, task.getNextOpenSubTask());
	}
	
	@Test
	public void testGetNextOpenSubTaskWaiting() {
		when(subTaskB.getStatus()).thenReturn(ServiceSubTaskStatus.WAITING);
		
		assertEquals(subTaskB, task.getNextOpenSubTask());
	}
	
	@Test
	public void testCloseSubTask() {
		task.closeSubTask();
		
		verify(subTaskB, times(1)).setStatus(ServiceSubTaskStatus.CLOSED);
	}
}
