package org.adiusframework.processmanager.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class SimulationProcessTest {
	private static final String TEST_ID = "testId";
	private static final String ANOTHER_TEST_ID = "anotherTestId";
	
	private SimulationStep testStep, anotherStep;
	private Map<String, SimulationStep> steps;
	
	private SimulationProcess process;
	
	@Before
	public void init() {
		testStep = mock(SimulationStep.class);
		anotherStep = mock(SimulationStep.class);
		when(anotherStep.getStatus()).thenReturn(SimulationStepStatus.PROCESSED);
		steps = new HashMap<String, SimulationStep>();
		steps.put(TEST_ID, testStep);
		steps.put(ANOTHER_TEST_ID, anotherStep);
		
		Set<SimulationStep> set = new HashSet<SimulationStep>();
		set.add(anotherStep);
		when(testStep.getChilds()).thenReturn(set);
		
		process = new SimulationProcess();
		process.setSteps(steps);
	}
	
	@Test
	public void testUpdateStatusProcessed() {
		when(testStep.getStatus()).thenReturn(SimulationStepStatus.PROCESSED);
		
		process.updateStatus(SimulationStepStatus.PROCESSED);
		
		assertEquals(SimulationProcessStatus.PROCESSED, process.getStatus());
	}
	
	@Test
	public void testUpdateStatusUnprocessed() {
		when(testStep.getStatus()).thenReturn(SimulationStepStatus.UNPROCESSED);
		
		process.updateStatus(SimulationStepStatus.UNPROCESSED);
		
		assertEquals(SimulationProcessStatus.UNPROCESSED, process.getStatus());
	}
	
	@Test
	public void testUpdateStatusError() {
		process.updateStatus(SimulationStepStatus.ERROR);
		
		assertEquals(SimulationProcessStatus.ERROR, process.getStatus());
	}
	@Test
	public void testGetNext() {
		when(testStep.getStatus()).thenReturn(SimulationStepStatus.PROCESSED);
		when(anotherStep.getStatus()).thenReturn(SimulationStepStatus.UNPROCESSED);
		
		assertEquals(anotherStep, process.getNext(testStep));
	}
	
	@Test
	public void testGetNextNoMatch() {
		when(testStep.getStatus()).thenReturn(SimulationStepStatus.UNPROCESSED);
		when(anotherStep.getStatus()).thenReturn(SimulationStepStatus.PROCESSED);
		
		assertNull(process.getNext(testStep));
	}
	
	@Test
	public void testGetNextTwoMatch() {
		when(testStep.getStatus()).thenReturn(SimulationStepStatus.UNPROCESSED);
		
		assertNull(process.getNext(testStep));
	}
}
