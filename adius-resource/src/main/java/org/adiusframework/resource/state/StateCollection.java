package org.adiusframework.resource.state;

import java.util.List;

public interface StateCollection extends State {

	public List<State> getStates();

	public void addState(State state);

}
