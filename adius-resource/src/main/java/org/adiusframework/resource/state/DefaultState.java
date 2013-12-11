package org.adiusframework.resource.state;

import java.util.List;

public class DefaultState implements State {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 8621422693930406374L;

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

	public DefaultState(List<AssertionObject> objects, List<AssertionVariable> variables, List<Assertion> assertions) {
		setObjects(objects);
		setVariables(variables);
		setAssertions(assertions);
	}

	@Override
	public List<AssertionObject> getObjects() {
		return objects;
	}

	public void setObjects(List<AssertionObject> objects) {
		this.objects = objects;
	}

	@Override
	public List<AssertionVariable> getVariables() {
		return variables;
	}

	public void setVariables(List<AssertionVariable> variables) {
		this.variables = variables;
	}

	@Override
	public List<Assertion> getAssertions() {
		return assertions;
	}

	public void setAssertions(List<Assertion> assertions) {
		this.assertions = assertions;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		// append objects
		builder.append("Objects:\n");
		for (AssertionObject object : getObjects()) {
			builder.append(object.toString()).append("\n");
		}
		builder.append("\n");

		// append variables
		builder.append("Variables:\n");
		for (AssertionVariable variable : getVariables()) {
			builder.append(variable.toString()).append("\n");
		}
		builder.append("\n");

		// append assertions
		builder.append("Assertions:\n");
		for (Assertion assertion : getAssertions()) {
			builder.append(assertion.toString()).append("\n");
		}
		return builder.toString();
	}

}
