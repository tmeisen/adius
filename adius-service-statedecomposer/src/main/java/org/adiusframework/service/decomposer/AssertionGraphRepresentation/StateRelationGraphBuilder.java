package org.adiusframework.service.decomposer.AssertionGraphRepresentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.adiusframework.resource.state.Assertion;
import org.adiusframework.resource.state.AssertionEntity;
import org.adiusframework.resource.state.AssertionObject;
import org.adiusframework.resource.state.AssertionVariable;
import org.adiusframework.resource.state.Predicate;
import org.adiusframework.resource.state.StateBuilder;
import org.adiusframework.resource.state.StateBuilder.StateTemplate;
import org.adiusframework.service.statedecomposer.exception.UnsupportedAssertionException;

/**
 * Class builds a graph between all the AssertionEntity with is necessary to
 * figure out relations with a higher depth than one later
 */
public class StateRelationGraphBuilder {

	private List<AssertionObject> stateRepresentatives;
	private List<Assertion> assertions;
	/*
	 * saves all the nodes with their relations The nodes are useful to figure
	 * out which Enity a relation with a StateRepresentatives
	 */
	private HashMap<AssertionObject, AssertionEntityNode> nodeMap;

	/**
	 * 
	 * @param assertions
	 *            a list of all available assertions
	 */
	public StateRelationGraphBuilder(List<Assertion> assertions) {
		this.stateRepresentatives = new ArrayList<AssertionObject>();
		this.assertions = assertions;
		this.nodeMap = new HashMap<AssertionObject, AssertionEntityNode>();
	}

	/**
	 * Adds a StateRepresentive to the Graphbuilder This Staterepresentive will
	 * be considered in the next call of the getBuilder method
	 * 
	 * @param object
	 *            State representive
	 */
	public void addStateRepresentiveAssertionEntry(AssertionObject object) {
		if (!stateRepresentatives.contains(object)) {
			stateRepresentatives.add(object);
			nodeMap.put(object, new AssertionEntityNode(object));
		}
	}

	/**
	 * Builds a Graph. The nodes in the Graph are AssertionsEnities. The nodes
	 * are connected by the Assertion
	 * 
	 * @throws UnsupportedAssertionException
	 */
	private void buildAssertionRelations() throws UnsupportedAssertionException {
		// LOGGER.info("assertion: " + a);
		// only predicates are supported, this should be the case, because
		// the base state should satisfy the current state template
		for (Assertion a : assertions) {
			if (Predicate.class.isInstance(a)) {
				Predicate p = Predicate.class.cast(a);

				// Connects every entity with every entity in this Assertion
				for (AssertionEntity e1 : p.getArguments()) {

					// variables can be skipped, because the does not carry any
					// part information
					if (AssertionVariable.class.isInstance(e1))
						continue;
					else if (!AssertionObject.class.isInstance(e1))
						throw new UnsupportedAssertionException(a);
					AssertionObject o1 = (AssertionObject) e1;

					if (!nodeMap.containsKey(o1)) {
						// Creates a Node for this assertionEntity
						AssertionEntityNode assertionEntry = new AssertionEntityNode(o1);
						nodeMap.put(o1, assertionEntry);
					}
					AssertionEntityNode assertionEntry1 = nodeMap.get(e1);
					// Connect this Entry with all Entries of this Assertion
					for (AssertionEntity e2 : p.getArguments()) {

						// variables can be skipped, because the does not carry
						// any
						// part
						// information
						if (AssertionVariable.class.isInstance(e2))
							continue;
						else if (!AssertionObject.class.isInstance(e2))
							throw new UnsupportedAssertionException(a);
						AssertionObject o2 = (AssertionObject) e2;

						// if e1 == e2 but that's the only Entity in this
						// Assertion
						// it will connect with itself, otherwise not
						if (o1 == o2 && p.getArguments().size() > 1)
							continue;
						if (!nodeMap.containsKey(e2)) {
							AssertionEntityNode assertionEntry2 = new AssertionEntityNode(o2);
							nodeMap.put(o2, assertionEntry2);
						}
						AssertionEntityNode assertionEntry2 = nodeMap.get(o2);

						// connect Entities with each other
						assertionEntry1.addLink(assertionEntry2, a);
						assertionEntry2.addLink(assertionEntry1, a);

					}

				}

			}
		}
	}

	/**
	 * Return s a list list of State Builders, each builder is root from one
	 * representative AssertionEntry for a decomposeState
	 * 
	 * @return
	 * @throws MoreThanOnePathContainsAssertionEnitiyException
	 */
	public List<StateBuilder> getBuilders() throws MoreThanOnePathContainsAssertionEnitiyException {
		/**
		 * builds a graph structure with all the AssertionsEntities as Nodes and
		 * the Assertions as connections
		 */
		try {
			buildAssertionRelations();
		} catch (UnsupportedAssertionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (hasIntersectionInGraph())
			throw new MoreThanOnePathContainsAssertionEnitiyException();

		List<StateBuilder> builderList = new ArrayList<StateBuilder>();

		/**
		 * build a builder for every representiv Entity
		 */
		for (AssertionEntity representiv : stateRepresentatives) {
			StateBuilder builder = new StateBuilder(StateTemplate.AS_IS_STATE);
			List<Assertion> assertionList = new ArrayList<Assertion>();
			// List of Assertiontypes which have already been visited
			List<String> visitedAssertionTypes = new ArrayList<String>();

			// Start node where the assertion gathering will begin
			AssertionEntityNode node = nodeMap.get(representiv);
			getAssertionsFromNode(assertionList, node, visitedAssertionTypes);

			// Add all assertion from this representativ to the builder
			for (Assertion a : assertionList) {
				builder.addAssertion(a);
			}

			builderList.add(builder);

		}
		return builderList;
	}

	/***
	 * Recursive function which runs through the graph gathering the assertion
	 * which are connecting the assertion enities with each other.
	 * 
	 * @param assertions
	 *            the list of assertions
	 * @param node
	 *            the next Node which gone be analysed
	 * @param visitedAssertionTypes
	 *            is a list of the assertiontypes which are already have been
	 *            visited in a previous layer
	 */
	private void getAssertionsFromNode(List<Assertion> assertions, AssertionEntityNode node,
			List<String> visitedAssertionTypes) {
		// contains all assertionTyes which have already been visit
		List<String> newVisitList = new ArrayList<String>(visitedAssertionTypes.size());
		// A list of AssertionEnities which have to be visit
		List<AssertionEntityNode> nodesToVisit = new ArrayList<AssertionEntityNode>();

		for (String s : visitedAssertionTypes) {
			newVisitList.add(s);
		}

		// Get all the connections of the current node
		for (AssertionEntityNodeConnection conn : node.getConnections()) {
			// if the assertionType wasn't used on the current way this
			// connection
			// can be analyzed
			if (!visitedAssertionTypes.contains(conn.getAssertionType())) {
				if (!assertions.contains(conn.getAssertion())) {
					assertions.add(conn.getAssertion());
				}
				if (!nodesToVisit.contains(conn.getTo())) {
					nodesToVisit.add(conn.getTo());
				}
				if (!newVisitList.contains(conn.getAssertionType())) {
					newVisitList.add(conn.getAssertionType());
				}
			}
		}

		// analyse connected nodes
		for (AssertionEntityNode n : nodesToVisit) {
			getAssertionsFromNode(assertions, n, newVisitList);
		}
	}

	private boolean hasIntersectionInGraph() {

		HashMap<AssertionEntityNode, Boolean> visitedNodeIsNotConstant = new HashMap<AssertionEntityNode, Boolean>();
		List<String> visitedAssertionTypes = new ArrayList<String>();
		for (AssertionEntity representiv : stateRepresentatives) {

			AssertionEntityNode node = nodeMap.get(representiv);
			if (hasIntersectionWithPreviousNodes(node, visitedAssertionTypes, visitedNodeIsNotConstant)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasIntersectionWithPreviousNodes(AssertionEntityNode node, List<String> visitedAssertionTypes,
			HashMap<AssertionEntityNode, Boolean> isConstant) {
		// contains all assertionTyes which have already been visit
		List<String> newVisitList = new ArrayList<String>(visitedAssertionTypes.size());
		// A list of AssertionEnities which have to be visit
		List<AssertionEntityNode> nodesToVisit = new ArrayList<AssertionEntityNode>();

		for (String s : visitedAssertionTypes) {
			newVisitList.add(s);
		}

		if (isConstant.containsKey(node)) {
			if (!isConstant.get(node)) {
				return true;
			}
		} else {
			isConstant.put(node, node.isConstant());
		}

		// Get all the connections of the current node
		for (AssertionEntityNodeConnection conn : node.getConnections()) {
			// if the assertionType wasn't used on the current way this
			// connection
			// can be analyzed
			if (!visitedAssertionTypes.contains(conn.getAssertionType())) {
				if (!assertions.contains(conn.getAssertion())) {
					assertions.add(conn.getAssertion());
				}
				if (!nodesToVisit.contains(conn.getTo())) {
					nodesToVisit.add(conn.getTo());
				}
				if (!newVisitList.contains(conn.getAssertionType())) {
					newVisitList.add(conn.getAssertionType());
				}
			}
		}

		// analyse connected nodes
		for (AssertionEntityNode n : nodesToVisit) {
			if (hasIntersectionWithPreviousNodes(n, newVisitList, isConstant)) {
				return true;
			}
		}
		return false;
	}
}
