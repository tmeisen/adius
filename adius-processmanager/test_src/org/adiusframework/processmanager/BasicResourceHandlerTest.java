package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.SimulationStep;
import org.adiusframework.query.Query;
import org.adiusframework.resource.ObjectResource;
import org.adiusframework.resource.PropertiesResource;
import org.adiusframework.resource.Resource;
import org.adiusframework.resource.StringResourceCapabilityRule;
import org.adiusframework.resourcemanager.ResourceManager;
import org.adiusframework.resourcemanager.ResourceQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AbstractResourceHandler.class)
public class BasicResourceHandlerTest {
	private static final String TEST_PROCESS_URI = "testProcess";
	private static final String TEST_STEP_URI = "testStep";
	private static final String TEST_REL_EXPRESSION = "testREL";
	private static final String TEST_DESCRIBTION = "testDescribtion";
	
	// Related object, which's methods could be called by the AbstractResourceHandler
	private ResourceManager resourceManager;
	private RELParser parser;

	// Dummy objects which doesn't need implemented behavior
	private Serializable object;
	private Properties properties;
	private SimulationStep step;
	
	// "Data"-objects which are processed by the AbstractResourceHandler
	private ServiceProcess process;
	private ObjectResource<Serializable> resource;
	private Query query;
	
	// Test tested object
	private AbstractResourceHandler handler;
	
	@SuppressWarnings("unchecked")
	@Before
	public void init() {
		// Create dummy-objects
		object = mock(Serializable.class);
		properties = mock(Properties.class);
		step = mock(SimulationStep.class);
		
		// Create data-object and link them among themselves and with the dummy-objects
		resource = mock(ObjectResource.class);
		when(resource.getObject()).thenReturn(object);
		List<Resource> list = new LinkedList<Resource>();
		list.add(resource);
		
		process = mock(ServiceProcess.class);
		when(process.getStep()).thenReturn(step);
		query = mock(Query.class);
		when(query.getProperties()).thenReturn(properties);
		when(query.getAttachedResources()).thenReturn(list);
		
		// Create and stub the related objects
		resourceManager = mock(ResourceManager.class);
		when(resourceManager.existResource(eq(TEST_STEP_URI), any(StringResourceCapabilityRule.class))).thenReturn(false);
		when(resourceManager.getResource(eq(TEST_PROCESS_URI), any(ResourceQuery.class))).thenReturn(resource);
		parser = mock(RELParser.class);
		when(parser.parse(TEST_REL_EXPRESSION)).thenReturn(TEST_DESCRIBTION);
		
		// Mock the static method of the tested class
		PowerMockito.mockStatic(AbstractResourceHandler.class);
		when(AbstractResourceHandler.generateResourceUri(any(ServiceProcess.class))).thenReturn(TEST_PROCESS_URI);
		when(AbstractResourceHandler.generateResourceUri(any(SimulationStep.class))).thenReturn(TEST_STEP_URI);
		
		// Create and initialize the test-object
		handler = spy(new AbstractResourceHandler());
		handler.setResourceManager(resourceManager);
		handler.setRELParser(parser);
		doReturn(true).when(handler).registerResource(anyString(), any(Resource.class));
	}

	@Test
	public void testRegisterResources() {
		handler.registerResources(process, query);
		
		verify(handler, times(1)).registerResource(eq(TEST_STEP_URI), any(ObjectResource.class));
		verify(handler, times(1)).registerResource(TEST_PROCESS_URI, resource);
		verify(handler, times(2)).registerResource(eq(TEST_PROCESS_URI), any(PropertiesResource.class));
	}
	
	@Test
	public void testReplaceRELExpression() {
		assertEquals(object.toString(), handler.replaceRELExpression(process, TEST_REL_EXPRESSION));
	}
}
