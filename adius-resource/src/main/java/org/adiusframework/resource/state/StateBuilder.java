package org.adiusframework.resource.state;

import java.util.List;
import java.util.Vector;

public class StateBuilder {

	/**
	 * The list of all objects that are referenced by the predicates in this
	 * state.
	 */
	private List<AssertionObject> objects;

	/**
	 * A list of all variables for this state.
	 */
	private List<AssertionVariable> variables;

	/**
	 * The list of assertions in this state.
	 */
	private List<Assertion> assertions;

	public static enum StateTemplate {
		AXIOM_STATE, AS_IS_STATE, GOAL_STATE, ACTION_STATE;
	}

	private StateTemplate template;

	public StateBuilder(StateTemplate template) {
		setTemplate(template);
		reset();
	}

	public StateTemplate getTemplate() {
		return template;
	}

	public void setTemplate(StateTemplate template) {
		this.template = template;
	}

	public void addAssertion(Assertion assertion) {
		addAssertion(assertion, true);
	}

	protected void addAssertion(Assertion assertion, boolean root) {

		// check if assertion violates the template restriction
		if (getTemplate().equals(StateTemplate.AS_IS_STATE) || getTemplate().equals(StateTemplate.AXIOM_STATE)) {
			if (Predicate.class.isInstance(assertion))
				addPredicate(Predicate.class.cast(assertion), root);
			else
				throw new UnsupportedOperationException("AsIsState template only supports predicates, but "
						+ assertion.getClass() + "present");
		} else {
			if (Predicate.class.isInstance(assertion))
				addPredicate(Predicate.class.cast(assertion), root);
			else if (QuantifierAssertion.class.isInstance(assertion))
				addQuantifier(QuantifierAssertion.class.cast(assertion), root);
			else if (LogicalAssertion.class.isInstance(assertion))
				addLogicAssertion(LogicalAssertion.class.cast(assertion), root);
		}
	}

	protected void addPredicate(Predicate predicate, boolean root) {
		for (AssertionEntity entity : predicate.getArguments()) {
			if (AssertionObject.class.isInstance(entity))
				addObject(AssertionObject.class.cast(entity));
			else if (AssertionVariable.class.isInstance(entity))
				addVariable(AssertionVariable.class.cast(entity));
		}
		if (root)
			assertions.add(predicate);
	}

	protected void addQuantifier(QuantifierAssertion assertion, boolean root) {
		for (AssertionVariable variable : assertion.getParameters())
			addVariable(variable);
		for (Assertion precondition : assertion.getPreconditions())
			addAssertion(precondition, false);
		for (Assertion effect : assertion.getEffects())
			addAssertion(effect, false);
		if (root)
			assertions.add(assertion);
	}

	protected void addVariable(AssertionVariable variable) {
		if (!variables.contains(variable))
			variables.add(variable);
	}

	protected void addObject(AssertionObject object) {
		if (!objects.contains(object))
			objects.add(object);
	}

	protected void addLogicAssertion(LogicalAssertion assertion, boolean root) {
		for (Assertion argument : assertion.getArguments())
			addAssertion(argument, false);
		if (root)
			assertions.add(assertion);
	}

	/**
	 * Finds a state object in a list. Therefore a name match is used.
	 * 
	 * @param objectList
	 *            the list of objects
	 * @param objectName
	 *            the name of the object
	 * @return the StateObject with the specified name or null if no object was
	 *         found
	 */
	protected AssertionObject findObject(String objectName) {
		for (AssertionObject obj : objects)
			if (obj.getName().equals(objectName))
				return obj;
		return null;
	}

	protected AssertionVariable findVariable(String variableName) {
		for (AssertionVariable var : variables)
			if (var.getName().equals(variableName))
				return var;
		return null;
	}

	public void reset() {
		objects = new Vector<AssertionObject>();
		variables = new Vector<AssertionVariable>();
		assertions = new Vector<Assertion>();
	}

	public State build(boolean autoReset) {
		State state = new DefaultState(objects, variables, assertions);
		if (autoReset)
			reset();
		return state;
	}

}
