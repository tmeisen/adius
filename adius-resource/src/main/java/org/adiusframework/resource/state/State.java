package org.adiusframework.resource.state;

import java.io.Serializable;
import java.util.List;

public interface State extends Serializable {

	/**
	 * Returns the list of assertion objects referenced by any assertion within
	 * this state.
	 * 
	 * @return list of assertion objects
	 */
	public List<AssertionObject> getObjects();

	/**
	 * Returns the list of variables used by this state.
	 * 
	 * @return list of variables
	 */
	public List<AssertionVariable> getVariables();

	/**
	 * Returns the list of predicates.
	 * 
	 * @return list of predicates
	 */
	public List<Assertion> getAssertions();

}
