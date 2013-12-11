package org.adiusframework.service.pddlgenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.resource.state.AssertionObject;
import org.adiusframework.resource.state.AssertionVariable;
import org.adiusframework.resource.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.plexus.util.CollectionUtils;

public class PDDLProblemBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(PDDLProblemBuilder.class);

	private Domain domain;

	/**
	 * Constructor.
	 * 
	 * @param domain
	 *            the domain this problem belongs to
	 */
	public PDDLProblemBuilder(PDDLDomainBuilder domainBuilder) {
		setDomain(domainBuilder.getDomain());
	}

	public Domain getDomain() {
		return domain;
	}

	protected void setDomain(Domain domain) {
		this.domain = domain;
	}

	// Write the list of all used objects into the problem file
	private String writeObjects(Collection<AssertionObject> objects) {
		StringBuilder result = new StringBuilder();
		for (AssertionObject obj : objects)
			result.append("	" + obj.getName() + " - " + obj.getTypeName() + "\n");
		return result.toString();
	}

	private String writeInit(State initialState) throws UnsupportedExpressionException {
		StringBuilder result = new StringBuilder();
		PDDLStateBuilder.writeAssertions(initialState, result, null, 1);
		return result.toString();
	}

	private String writeGoal(State targetState, Collection<AssertionObject> objects)
			throws UnsupportedExpressionException {
		StringBuilder result = new StringBuilder();

		// we have to generate a goal description for each possible variable
		// assignment of the target state, therefore we have to identify the
		// possible assignments
		// first we initialize an assignment mapping list
		Map<String, List<AssertionObject>> assignments = new HashMap<String, List<AssertionObject>>();
		for (AssertionVariable variable : targetState.getVariables()) {
			if (assignments.containsKey(variable.getTypeName()))
				throw new UnsupportedExpressionException("Variable '" + variable.getName() + "' has the type '"
						+ variable.getTypeName() + "' which already existing in goal state");
			LOGGER.debug("Adding assignment list for type " + variable.getTypeName());
			assignments.put(variable.getTypeName(), new Vector<AssertionObject>());
		}

		// second we fill the assignments with values
		for (AssertionObject object : objects) {
			if (assignments.containsKey(object.getTypeName())) {
				LOGGER.debug("Adding object " + object.getName() + " of type " + object.getTypeName()
						+ " to assignment list");
				assignments.get(object.getTypeName()).add(object);
			}
		}
		int count = 1;
		for (List<AssertionObject> list : assignments.values())
			count *= list.size();

		// third we can build the object list that are used to set the variables
		// in the goal state
		List<Set<AssertionObject>> objectList = new Vector<Set<AssertionObject>>(count);
		for (int i = 0; i < count; i++)
			objectList.add(new HashSet<AssertionObject>());
		for (AssertionVariable variable : targetState.getVariables()) {
			for (int i = 0; i < count; i++) {
				List<AssertionObject> assignment = assignments.get(variable.getTypeName());
				objectList.get(i).add(assignment.get(i % assignment.size()));
			}
		}

		// finally we create a goal entry for each found assignment
		result.append("\t(AND \n");
		for (Set<AssertionObject> assertion : objectList) {
			LOGGER.debug("Generating goal state entry for set " + assertion);
			PDDLStateBuilder.writeAssertions(targetState, result, assertion, 2);
		}
		result.append("\t)\n");
		return result.toString();
	}

	/**
	 * Generates the description of this problem.
	 * 
	 * @param problemName
	 *            name of the problem
	 * @param currentState
	 *            the current state
	 * @param targetState
	 *            the target state
	 * @return the description as a string
	 * @throws UnsupportedExpressionException
	 */
	@SuppressWarnings("unchecked")
	public String buildPDDL(String problemName, State axiomState, State currentState, State targetState)
			throws UnsupportedExpressionException {

		// prepare object list (that means all the objects within the problems
		// minus the constants of the domain, because these constants are also
		// referenced but have already been defined within the domain)
		Set<AssertionObject> objectSet = new HashSet<AssertionObject>();
		objectSet.addAll(axiomState.getObjects());
		objectSet.addAll(currentState.getObjects());
		objectSet.addAll(targetState.getObjects());
		Collection<AssertionObject> objects = CollectionUtils.subtract(objectSet, getDomain().getConstants());

		// write all sections
		String initSectionAxiomState = writeInit(axiomState);
		String initSectionCurrentState = writeInit(currentState);
		String goalSection = writeGoal(targetState, objects);

		// it is important to generate the objects section last, because the
		// object list is updated during the other sections
		String objectSection = writeObjects(objects);

		// build the complete problem description
		StringBuilder result = new StringBuilder();

		// write problem definition
		result.append("(define (problem ").append(problemName).append(")\n");
		result.append("(:domain " + getDomain().getName() + ")\n");

		// write objects
		result.append("\n(:objects\n");
		result.append(objectSection);
		result.append(")\n");

		// write init statement
		result.append("\n(:init\n");
		result.append(initSectionAxiomState);
		result.append(initSectionCurrentState);
		result.append(")\n");

		// write goal definition
		result.append("\n(:goal\n");
		result.append(goalSection);
		result.append(")\n");

		// close problem definition
		result.append("\n)");
		return result.toString();
	}

}
