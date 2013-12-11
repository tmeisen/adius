package org.adiusframework.processmanager.domain;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class ServiceProcessTest {
	private ServiceTask taskA, taskB;
	
	private ServiceProcess process;
	
	@Before
	public void init() {
		taskA = mock(ServiceTask.class);
		taskB = mock(ServiceTask.class);
		
		Map<Integer, ServiceTask> tasks = new HashMap<Integer, ServiceTask>();
		tasks.put(1, taskA);
		tasks.put(2, taskB);
		
		process = new ServiceProcess();
		process.setTasks(tasks);
	}
	
	@Test
	public void getOpenTask() {
		when(taskA.getStatus()).thenReturn(ServiceTaskStatus.CLOSED);
		when(taskB.getStatus()).thenReturn(ServiceTaskStatus.OPEN);
		
		assertEquals(taskB, process.getOpenTask());
	}
	
	@Test
	public void getOpenTaskNoneOpened() {
		when(taskA.getStatus()).thenReturn(ServiceTaskStatus.CLOSED);
		when(taskB.getStatus()).thenReturn(ServiceTaskStatus.CLOSED);
		
		assertNull(process.getOpenTask());
	}
	
	@Test
	public void getOpenTaskTwoOpened() {
		when(taskA.getStatus()).thenReturn(ServiceTaskStatus.OPEN);
		when(taskB.getStatus()).thenReturn(ServiceTaskStatus.OPEN);
		
		assertNull(process.getOpenTask());
	}
}
