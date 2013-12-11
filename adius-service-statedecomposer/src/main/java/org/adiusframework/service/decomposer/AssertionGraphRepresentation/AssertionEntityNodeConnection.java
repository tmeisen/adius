package org.adiusframework.service.decomposer.AssertionGraphRepresentation;

import org.adiusframework.resource.state.Assertion;
import org.adiusframework.resource.state.Predicate;
import org.adiusframework.service.statedecomposer.exception.UnsupportedAssertionException;

/**
 * This class represents a directed connection between two AssertionEnityNodes
 * 
 * @author bb715812
 * 
 */
public class AssertionEntityNodeConnection {

	// startpoint of connection
	private AssertionEntityNode from;

	// endpoint of connection
	private AssertionEntityNode to;

	// Assertion which connects the two Enities
	private Assertion assertion;

	// Type Assertion which connects the Enities
	private String assertionType;

	/**
	 * Directed connetion between two AssertionEnities
	 * 
	 * @param from
	 *            Node where the connection starts
	 * @param to
	 *            Node where the connection end
	 * @param assertion
	 *            which connect the AssertionEnities
	 * @throws UnsupportedAssertionException
	 *             Currently only Predicate Assertion are allowed, other
	 *             Assertion will throw this Exception
	 */
	public AssertionEntityNodeConnection(AssertionEntityNode from, AssertionEntityNode to, Assertion assertion)
			throws UnsupportedAssertionException {
		this.from = from;
		this.to = to;
		this.assertion = assertion;
		if (!(assertion instanceof Predicate))
			throw new UnsupportedAssertionException(assertion);
		else
			assertionType = ((Predicate) assertion).getName();
	}

	/**
	 * 
	 * @return returns the AssertionEnitiyNode where the connection is pointing
	 *         to
	 */
	public AssertionEntityNode getTo() {
		return to;
	}

	/**
	 * 
	 * @return returns the AssertionEnitiyNode where the connection is coming
	 *         from
	 */
	public AssertionEntityNode getFrom() {
		return from;
	}

	/**
	 * 
	 * @return returns the Assertion which connects those two Assertions
	 */
	public Assertion getAssertion() {
		return assertion;
	}

	/**
	 * 
	 * @return returns the Type of Assertion
	 */
	public String getAssertionType() {
		return assertionType;
	}

}
