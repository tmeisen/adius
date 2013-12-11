package org.adiusframework.service.decomposer.AssertionGraphRepresentation;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.adiusframework.resource.state.Assertion;
import org.adiusframework.resource.state.AssertionEntity;
import org.adiusframework.resource.state.AssertionObject;
import org.adiusframework.resource.state.Predicate;
import org.junit.Before;
import org.junit.Test;

public class StateRelationGraphBuilderMultipleRelations {

	/**
	 * List of Assertions first Test
	 */
	private List<Assertion> assertionsWithNonConstant;

	/**
	 * List of the first-layer Enities of first Test
	 */
	private List<AssertionObject> representivesWithNonConstant;

	/**
	 * List of Assertions second Test
	 */
	private List<Assertion> assertionsWithConstant;

	/**
	 * List of the first-layer Enities of second Test
	 */
	private List<AssertionObject> representivesWithConstant;

	/**
	 * Connects Assertion Object over two path and the Element which connects
	 * the path isn't a Constant, this fact should end up in a Exception
	 * 
	 * assertionObj1 -> connectionObj(Non-constant) assertionObj2 ->
	 * connectionObj(Non-constant)
	 * 
	 */
	private void initNonConstant() {
		representivesWithNonConstant = new ArrayList<AssertionObject>();
		assertionsWithNonConstant = new ArrayList<Assertion>();

		AssertionObject assertionOb1 = mock(AssertionObject.class);
		AssertionObject connectionObj = mock(AssertionObject.class);

		representivesWithNonConstant.add(assertionOb1);

		/**
		 * Set AssertionOb2 as non constant, this should end up in a exception
		 * because Assertion is in both pathes
		 */

		when(connectionObj.isConstant()).thenReturn(false);

		List<AssertionEntity> arguments = new ArrayList<AssertionEntity>();
		arguments.add(assertionOb1);
		arguments.add(connectionObj);

		Predicate assertion1 = mock(Predicate.class);
		when(assertion1.getName()).thenReturn("assertionType");
		assertionsWithNonConstant.add(assertion1);

		when(assertion1.getArguments()).thenReturn(arguments);

		AssertionObject assertionOb2 = mock(AssertionObject.class);
		when(assertionOb2.isConstant()).thenReturn(false);

		List<AssertionEntity> arguments2 = new ArrayList<AssertionEntity>();
		arguments2.add(assertionOb2);
		arguments2.add(connectionObj);

		representivesWithNonConstant.add(assertionOb2);

		Predicate assertion2 = mock(Predicate.class);
		/**
		 * Set type of Assertion-mock same as the first assertion
		 */
		when(assertion2.getName()).thenReturn("assertionType");
		assertionsWithNonConstant.add(assertion2);
		// assertionList.add(assertion);

		/**
		 * Set AssertionEnities of Assertion
		 */
		when(assertion2.getArguments()).thenReturn(arguments2);
	}

	/**
	 * Connects Assertion Object over two path and the Element which connects
	 * the path isn't a Constant, this fact should end up in a Exception
	 * 
	 * assertionObj1 -> connectionObj(Constant) assertionObj2 ->
	 * connectionObj(Constant)
	 * 
	 */
	private void initConstant() {
		representivesWithConstant = new ArrayList<AssertionObject>();
		assertionsWithConstant = new ArrayList<Assertion>();

		AssertionObject assertionOb1 = mock(AssertionObject.class);
		AssertionObject connectionObj = mock(AssertionObject.class);

		representivesWithConstant.add(assertionOb1);

		/**
		 * Set AssertionOb2 as constant, this should not end up in a exception
		 * because Assertion is in both pathes but its a constant
		 */

		when(connectionObj.isConstant()).thenReturn(true);

		List<AssertionEntity> arguments = new ArrayList<AssertionEntity>();
		arguments.add(assertionOb1);
		arguments.add(connectionObj);

		Predicate assertion1 = mock(Predicate.class);
		when(assertion1.getName()).thenReturn("assertionType");
		assertionsWithConstant.add(assertion1);

		when(assertion1.getArguments()).thenReturn(arguments);

		AssertionObject assertionOb3 = mock(AssertionObject.class);
		when(assertionOb3.isConstant()).thenReturn(false);

		List<AssertionEntity> arguments2 = new ArrayList<AssertionEntity>();
		arguments2.add(assertionOb3);
		arguments2.add(connectionObj);

		representivesWithConstant.add(assertionOb3);

		Predicate assertion2 = mock(Predicate.class);
		/**
		 * Set type of Assertion-mock same as the first assertion
		 */
		when(assertion2.getName()).thenReturn("assertionType");
		assertionsWithConstant.add(assertion2);
		// assertionList.add(assertion);

		/**
		 * Set AssertionEnities of Assertion
		 */
		when(assertion2.getArguments()).thenReturn(arguments2);
	}

	@Before
	public void init() {
		this.initNonConstant();
		this.initConstant();

	}

	@Test
	public void MultipleWithNonConstaint() {
		StateRelationGraphBuilder builder = new StateRelationGraphBuilder(assertionsWithNonConstant);

		for (AssertionObject a : representivesWithNonConstant)
			builder.addStateRepresentiveAssertionEntry(a);

		try {
			builder.getBuilders();

			/**
			 * getBuilders should throw an Exception
			 */
			fail("Should have thrown a Exception");
		} catch (MoreThanOnePathContainsAssertionEnitiyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void MultipleWithConstaint() {
		StateRelationGraphBuilder builder = new StateRelationGraphBuilder(assertionsWithConstant);

		for (AssertionObject a : representivesWithConstant)
			builder.addStateRepresentiveAssertionEntry(a);

		try {
			builder.getBuilders();

			/**
			 * getBuilders should not throw an Exception
			 */

		} catch (MoreThanOnePathContainsAssertionEnitiyException e) {
			fail("Shouldn't have thrown a Exception");
			e.printStackTrace();
		}
	}
}
