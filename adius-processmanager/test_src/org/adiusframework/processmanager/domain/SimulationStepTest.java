package org.adiusframework.processmanager.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SimulationStep.class)
public class SimulationStepTest {
	private static final String ADD_TO_MAP_METHODNAME = "addToMap";
	private static final String TEST_IDENTIFIER = "testID";
	
	private SimulationProcess process;
	private SimulationStep anotherStep;
	private Set<SimulationStep> steps;
	
	private SimulationStep testStep;
	
	@Before
	public void init() {
		process = mock(SimulationProcess.class);
		
		anotherStep = mock(SimulationStep.class);
		when(anotherStep.getProcess()).thenReturn(process);
		when(anotherStep.getIdentifier()).thenReturn(TEST_IDENTIFIER);
		steps = new HashSet<SimulationStep>();
		
		testStep = PowerMockito.spy(new SimulationStep());
		testStep.setProcess(process);
	}
	
	@Test
	public void testAddChildTrue() throws Exception {
		testStep.setChilds(steps);
		PowerMockito.doReturn(true).when(testStep, ADD_TO_MAP_METHODNAME, anotherStep, steps);
		
		testStep.addChild(anotherStep);
		
		verify(anotherStep, times(1)).addParent(testStep);
	}
	
	@Test
	public void testAddChildFalse() throws Exception {
		testStep.setChilds(steps);
		PowerMockito.doReturn(false).when(testStep, ADD_TO_MAP_METHODNAME, anotherStep, steps);
		
		testStep.addChild(anotherStep);
		
		verify(anotherStep, never()).addParent(testStep);
	}
	
	@Test
	public void testAddParentTrue() throws Exception {
		testStep.setParents(steps);
		PowerMockito.doReturn(true).when(testStep, ADD_TO_MAP_METHODNAME, anotherStep, steps);
		
		testStep.addParent(anotherStep);
		
		verify(anotherStep, times(1)).addChild(testStep);
	}
	
	@Test
	public void testAddParentFalse() throws Exception {
		testStep.setParents(steps);
		PowerMockito.doReturn(false).when(testStep, ADD_TO_MAP_METHODNAME, anotherStep, steps);
		
		testStep.addParent(anotherStep);
		
		verify(anotherStep, never()).addChild(testStep);
	}
	
	@Test
	public void testAddToMapTrue() throws Exception {
		assertTrue((Boolean)Whitebox.invokeMethod(testStep, ADD_TO_MAP_METHODNAME, anotherStep, steps));
	}
	
	@Test
	public void testAddToMapFalse() throws Exception {
		steps.add(anotherStep);
		
		assertFalse((Boolean)Whitebox.invokeMethod(testStep, ADD_TO_MAP_METHODNAME, anotherStep, steps));
	}
	
	@Test
	public void testAddToMapWrongProcess() throws Exception {
		when(anotherStep.getProcess()).thenReturn(null);
		
		assertFalse((Boolean)Whitebox.invokeMethod(testStep, ADD_TO_MAP_METHODNAME, anotherStep, steps));
	}
}
