package org.adiusframework.service.decomposer.AssertionGraphRepresentation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.adiusframework.resource.state.Assertion;
import org.adiusframework.resource.state.AssertionEntity;
import org.adiusframework.resource.state.AssertionObject;
import org.adiusframework.resource.state.DefaultState;
import org.adiusframework.resource.state.Predicate;
import org.adiusframework.resource.state.StateBuilder;
import org.junit.Before;
import org.junit.Test;

/**
 * Test of the StateRelationGraphBuilder which is responsible for finding
 * connection between Assertion especially for ones with a deeper relation than
 * one layer
 * 
 * @author bb715812
 * 
 */
public class StateRelationGraphBuilderDeepRelationTest {

	/**
	 * Defines how depth a relation goes. For example depth of 3 would be:
	 * 
	 * 1 2 3 assertionEnity1 <-> assertionEnity2 <-> assertionEnity3 <->
	 * assertionEnity4
	 */
	private static int relationDepth = 3;

	/**
	 * Defines how many of these relation-chains exists
	 */
	private static int representivCount = 4;

	/**
	 * List of all Assertions
	 */
	private List<Assertion> assertions;

	/**
	 * List of the first-layer Enities
	 */
	private List<AssertionObject> representives;

	@Before
	/**
	 * Initialize the Test with all the assertions
	 */
	public void init() {

		assertions = new ArrayList<Assertion>();
		representives = new ArrayList<AssertionObject>();

		/**
		 * Create a "assertion-chain" for every representiv
		 */
		for (int i = 0; i < representivCount; i++) {
			AssertionObject assertionEntiy1 = mock(AssertionObject.class);
			List<Assertion> assertionList = new ArrayList<Assertion>();
			representives.add(assertionEntiy1);
			/**
			 * create assertion with the defined depth Example:
			 * Assertion1(type1) Assertion2(type2) ... AssertionEntity1<->
			 * AssertionEnity2<->AssertionEnity3 ...
			 */
			for (int j = 0; j < relationDepth; j++) {
				AssertionObject assertionEntiy2 = mock(AssertionObject.class);
				List<AssertionEntity> arguments = new ArrayList<AssertionEntity>();
				arguments.add(assertionEntiy1);
				arguments.add(assertionEntiy2);

				Predicate assertion = mock(Predicate.class);
				/**
				 * Set type of Assertion-mock
				 */
				when(assertion.getName()).thenReturn("assertionType" + j);
				assertions.add(assertion);
				assertionList.add(assertion);

				/**
				 * Set AssertionEnities of Assertion
				 */
				when(assertion.getArguments()).thenReturn(arguments);
				assertionEntiy1 = assertionEntiy2;
			}
		}

	}

	@Test
	/**
	 * A simple Test which tests if the StateRelationBuilder found the right amount of builders
	 * with the correct amount of Assertions
	 */
	public void representivAndAssertionTest() throws MoreThanOnePathContainsAssertionEnitiyException {
		StateRelationGraphBuilder builder = new StateRelationGraphBuilder(assertions);

		for (AssertionObject a : representives)
			builder.addStateRepresentiveAssertionEntry(a);

		List<StateBuilder> statebuilders = builder.getBuilders();
		for (StateBuilder singlebuilder : statebuilders) {
			DefaultState state = (DefaultState) singlebuilder.build(true);
			/**
			 * right amount of Assertions
			 */
			assertEquals(relationDepth, state.getAssertions().size());

		}
		/**
		 * right amount of Relation-Chains
		 */
		assertEquals(representivCount, statebuilders.size());
	}

}
