package org.adiusframework.processmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
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
import org.adiusframework.processmanager.exception.InvalidDomainException;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;
import org.adiusframework.resource.ObjectResource;
import org.adiusframework.resource.PropertiesResource;
import org.adiusframework.resource.Resource;
import org.adiusframework.resource.ResourceCapability;
import org.adiusframework.resource.ResourceCapabilityRule;
import org.adiusframework.resource.StringResourceCapability;
import org.adiusframework.resource.StringResourceCapabilityRule;
import org.adiusframework.resourcemanager.NonAccessibleResourceException;
import org.adiusframework.resourcemanager.ResourceManager;
import org.adiusframework.resourcemanager.ResourceQuery;
import org.adiusframework.service.StandardServiceResult;
import org.adiusframework.service.xml.ResourceRequirement;
import org.adiusframework.util.datastructures.SystemData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

public class AbstractResourceHandlerTest {
	private static final String TEST_DOMAIN = "testDomain";
	private static final int TEST_ENTITY_ID = 42;
	private static final int TEST_PROCESS_ID = 1234;
	private static final String TEST_ENTITY_URI = "testUri";
	private static final String TEST_RESOURCE_URI = TEST_DOMAIN + "." + TEST_ENTITY_URI + "." + TEST_PROCESS_ID;
	private static final String TEST_REL_EXPRESSION = "testRELExpression";
	private static final String TEST_DESCRIBTION = "testDescribtion";
	private static final String TEST_CAPABILITY_RULE = "testRule";
	private static final String TEST_TYPE_1 = "type1";
	private static final String TEST_TYPE_2 = "type2";

	private static final String SPLIT_CHAR = ",";
	private static final String UPDATE_RULE = "extraction_target";
	private static final String PROPERTIES_CAPABILITY = "user_properties";

	// A data-object which is passed through the methods
	private ServiceProcess process;

	// Some sample Resources bundle in a List
	private Resource resource1;
	private Resource resource2;
	private List<Resource> resources;

	// Dependent object which's methods are called during the tests
	private ResourceManager manager;
	private RELParser parser;

	// The test object
	private AbstractResourceHandler handler;

	@Before
	public void init() {
		// Create the data-object
		process = mock(ServiceProcess.class);
		when(process.getDomain()).thenReturn(TEST_DOMAIN);
		when(process.getEntityId()).thenReturn(TEST_ENTITY_ID);
		when(process.getProcessId()).thenReturn(TEST_PROCESS_ID);

		// Create the example Resources
		resource1 = mock(Resource.class);
		resource2 = mock(Resource.class);
		resources = new LinkedList<Resource>();
		resources.add(resource1);
		resources.add(resource2);

		// Create the dependent objects
		manager = mock(ResourceManager.class);
		parser = mock(RELParser.class);

		// Create the test object and inject the dependencies
		handler = spy(new AbstractResourceHandler() {

			@Override
			protected String generateEntityResourceUri(String domain, Integer entityId) throws InvalidDomainException {
				if (TEST_DOMAIN.equals(domain) && TEST_ENTITY_ID == entityId) {
					return TEST_ENTITY_URI;
				}

				return null;
			}
		});
		handler.setResourceManager(manager);
		handler.setRELParser(parser);
	}

	@Test
	public void testRegisterResourcesResult() throws ProcessManagerException {
		// Create some test specific objects
		StandardServiceResult result = mock(StandardServiceResult.class);
		when(result.getResources()).thenReturn(resources);
		doReturn(true).when(handler).registerResource(eq(TEST_RESOURCE_URI), any(Resource.class));

		// Call the test method
		handler.registerResources(process, result);

		// Verify that both resources were registered
		verify(handler, times(1)).registerResource(TEST_RESOURCE_URI, resource1);
		verify(handler, times(1)).registerResource(TEST_RESOURCE_URI, resource2);
	}

	@Test
	public void testRegisterResourcesQuery() throws ProcessManagerException {
		// Make some test specific configuration
		final Properties properties = mock(Properties.class);
		Query query = mock(Query.class);
		when(query.getProperties()).thenReturn(properties);
		when(query.getResources()).thenReturn(resources);
		when(manager.existResource(anyString(), any(ResourceCapabilityRule.class))).thenReturn(false);
		doReturn(true).when(handler).registerResource(anyString(), any(Resource.class));

		// Call the test method
		handler.registerResources(process, query);

		// Check that all necessary Resources were registered
		verify(handler, times(1)).registerResource(eq(TEST_DOMAIN + "." + TEST_ENTITY_URI),
				argThat(new ArgumentMatcher<Resource>() {

					@SuppressWarnings({ "rawtypes", "unchecked" })
					@Override
					public boolean matches(Object argument) {
						if (argument instanceof ObjectResource) {
							ObjectResource<Integer> resource = (ObjectResource) argument;
							ResourceCapability capability = resource.getCapability();

							return capability instanceof StringResourceCapability
									&& ((StringResourceCapability) capability).getDescription().equals(
											AbstractResourceHandler.RESOURCE_ENTITY_ID_NAME)
									&& resource.getObject().equals(TEST_ENTITY_ID);
						}
						return false;
					}
				}));
		verify(handler, times(1)).registerResource(TEST_RESOURCE_URI, resource1);
		verify(handler, times(1)).registerResource(TEST_RESOURCE_URI, resource2);
		verify(handler, times(1)).registerResource(eq(TEST_RESOURCE_URI), argThat(new ArgumentMatcher<Resource>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof PropertiesResource) {
					PropertiesResource resource = (PropertiesResource) argument;
					ResourceCapability capability = resource.getCapability();

					return capability instanceof StringResourceCapability
							&& ((StringResourceCapability) capability).getDescription().equals(PROPERTIES_CAPABILITY)
							&& resource.getProperties().equals(properties);
				}
				return false;
			}
		}));
	}

	@Test
	public void testRegisterResourcesResource() throws NonAccessibleResourceException {
		// Create some test specific objects
		when(manager.registerResource(TEST_RESOURCE_URI, resource1)).thenReturn(true);

		// Call the test method and check the return value
		assertTrue(handler.registerResource(TEST_RESOURCE_URI, resource1));

		// Verify the method calls
		verify(manager, times(1)).registerResource(TEST_RESOURCE_URI, resource1);
	}

	@Test
	public void testFindResources() throws ProcessManagerException {
		// Create test specific mocks and link them with each other
		ResourceRequirement rr = mock(ResourceRequirement.class);
		when(rr.getTypes()).thenReturn(TEST_TYPE_1 + SPLIT_CHAR + TEST_TYPE_2);
		when(rr.getCapabilityRule()).thenReturn(TEST_CAPABILITY_RULE);
		final SystemData systemData = mock(SystemData.class);

		// Stub test specific behavior
		when(manager.getResource(eq(TEST_RESOURCE_URI), argThat(new ArgumentMatcher<ResourceQuery>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof ResourceQuery) {
					ResourceQuery query = (ResourceQuery) argument;
					ResourceCapabilityRule rule = query.getCapabilityRule();

					return query.getSystemRequirement().equals(systemData)
							&& rule instanceof StringResourceCapabilityRule
							&& ((StringResourceCapabilityRule) rule).getRule().equals(TEST_CAPABILITY_RULE)
							&& query.getTypes().size() == 2 && query.getTypes().contains(TEST_TYPE_1)
							&& query.getTypes().contains(TEST_TYPE_2);
				}
				return false;
			}
		}))).thenReturn(resource1);

		// Call the test method and check the return value
		assertEquals(resource1, handler.findResource(process, rr, systemData));
	}

	@Test
	public void testReleaseResources() throws ProcessManagerException {
		// Call the test method
		handler.releaseResources(process);

		// Check the method call
		verify(manager, times(1)).unregisterResources(TEST_RESOURCE_URI);
	}

	@Test
	public void testUpdateResources() throws ProcessManagerException {
		// Call the test method
		handler.updateResources(process);

		// Check the method call
		verify(manager, times(1)).updateResources(eq(TEST_RESOURCE_URI),
				argThat(new ArgumentMatcher<StringResourceCapabilityRule>() {

					@Override
					public boolean matches(Object argument) {
						if (argument instanceof StringResourceCapabilityRule) {
							StringResourceCapabilityRule rule = (StringResourceCapabilityRule) argument;

							return rule.getRule().equals(UPDATE_RULE);
						}

						return false;
					}
				}));
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testReplaceRELExpression() throws ProcessManagerException {
		when(parser.parse(TEST_REL_EXPRESSION)).thenReturn(TEST_DESCRIBTION);

		Object object = new Serializable() {
			private static final long serialVersionUID = -6560105269360762283L;
		};
		ObjectResource resource = mock(ObjectResource.class);
		when(resource.getObject()).thenReturn((Serializable) object);
		when(manager.getResource(eq(TEST_RESOURCE_URI), argThat(new ArgumentMatcher<ResourceQuery>() {

			@Override
			public boolean matches(Object argument) {
				if (argument instanceof ResourceQuery) {
					ResourceQuery query = (ResourceQuery) argument;
					ResourceCapabilityRule rule = query.getCapabilityRule();

					return query.getProtocols() == null && query.getSystemRequirement().equals(new SystemData())
							&& rule instanceof StringResourceCapabilityRule
							&& ((StringResourceCapabilityRule) rule).getRule().equals(TEST_DESCRIBTION);
				}
				return false;
			}
		}))).thenReturn(resource);

		assertEquals(object.toString(), handler.replaceRELExpression(process, TEST_REL_EXPRESSION));
	}

	@Test
	public void testReplaceRELExpressionNoResource() throws ProcessManagerException {
		when(parser.parse(TEST_REL_EXPRESSION)).thenReturn(TEST_DESCRIBTION);

		assertNull(handler.replaceRELExpression(process, TEST_REL_EXPRESSION));
	}

	@Test
	public void testReplaceRELExpressionNoReferenz() throws ProcessManagerException {
		// Stub test specific behavior, call the method and test the result
		when(parser.parse(TEST_REL_EXPRESSION)).thenReturn(null);
		assertEquals(TEST_REL_EXPRESSION, handler.replaceRELExpression(process, TEST_REL_EXPRESSION));
	}
}
