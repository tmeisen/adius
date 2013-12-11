package org.adiusframework.service.decomposer.AssertionGraphRepresentation;

import java.util.ArrayList;
import java.util.List;

import org.adiusframework.resource.state.Assertion;
import org.adiusframework.resource.state.AssertionObject;
import org.adiusframework.service.statedecomposer.exception.UnsupportedAssertionException;

/**
 * Represents a AssertionEntriy with all it's relations
 */
public class AssertionEntityNode {

	/**
	 * Entry which will be represented by this node
	 */
	private AssertionObject assertionEntry;

	/**
	 * contains all the relation which exist from this AssertionEntry to all
	 * other Entries
	 */
	private List<AssertionEntityNodeConnection> connections;

	public AssertionEntityNode(AssertionObject assertionEntry) {
		connections = new ArrayList<AssertionEntityNodeConnection>();
		/*
		 * The root nodes are not derived from a Assertion an so their don't
		 * have a relation to one.
		 */
		this.assertionEntry = assertionEntry;
	}

	/**
	 * Creates a Connection between this node and the given one
	 * 
	 * @param node
	 *            the connected node
	 * @param assertion
	 *            which connected the node with each other
	 */
	public void addLink(AssertionEntityNode node, Assertion assertion) {
		AssertionEntityNodeConnection conn;
		try {
			conn = new AssertionEntityNodeConnection(this, node, assertion);
			if (!connections.contains(conn)) {
				connections.add(conn);
			}
		} catch (UnsupportedAssertionException e) {

			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return returns all connections of this AssertionEntityNode
	 */
	public List<AssertionEntityNodeConnection> getConnections() {
		return connections;
	}

	@Override
	public String toString() {
		if (assertionEntry != null)
			return assertionEntry.toString();
		else
			return " ";
	}

	@Override
	public int hashCode() {
		return assertionEntry.hashCode();
	}

	public boolean isConstant() {
		return assertionEntry.isConstant();
	}

}
