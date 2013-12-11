package org.adiusframework.resource.state;

import java.util.List;
import java.util.Vector;

public class DefaultStateCollection implements StateCollection {
	private static final long serialVersionUID = 4861906895310055013L;

	private List<State> states;

	public DefaultStateCollection() {
		states = new Vector<State>();
	}

	@Override
	public List<AssertionObject> getObjects() {
		List<AssertionObject> objects = new Vector<AssertionObject>();
		for (State state : states)
			objects.addAll(state.getObjects());
		return objects;
	}

	@Override
	public List<AssertionVariable> getVariables() {
		List<AssertionVariable> variables = new Vector<AssertionVariable>();
		for (State state : states)
			variables.addAll(state.getVariables());
		return variables;
	}

	@Override
	public List<Assertion> getAssertions() {
		List<Assertion> assertions = new Vector<Assertion>();
		for (State state : states)
			assertions.addAll(state.getAssertions());
		return assertions;
	}

	@Override
	public List<State> getStates() {
		return states;
	}

	@Override
	public void addState(State state) {
		states.add(state);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (State state : states)
			builder.append(state.toString()).append("\n");
		return builder.toString();
	}
}
