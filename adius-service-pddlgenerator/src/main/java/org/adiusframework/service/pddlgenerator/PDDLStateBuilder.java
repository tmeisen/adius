package org.adiusframework.service.pddlgenerator;

import java.util.List;
import java.util.Set;

import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.resource.state.Assertion;
import org.adiusframework.resource.state.AssertionEntity;
import org.adiusframework.resource.state.AssertionObject;
import org.adiusframework.resource.state.AssertionVariable;
import org.adiusframework.resource.state.LogicalAssertion;
import org.adiusframework.resource.state.Predicate;
import org.adiusframework.resource.state.QuantifierAssertion;
import org.adiusframework.resource.state.State;
import org.apache.commons.lang3.StringUtils;

/**
 * Class that provides static methods to write state information into a pddl
 * file.
 * 
 * @author Alexander Tenbrock
 * 
 */
public class PDDLStateBuilder {

	/**
	 * Writes a list of variables into a string builder
	 * 
	 * @param variables
	 *            the variable list
	 * @param result
	 *            the resulting string builder
	 */
	public static void writeVariables(List<AssertionVariable> variables, StringBuilder result) {
		for (AssertionVariable var : variables)
			result.append(" ?" + var.getName() + " - " + var.getTypeName());
	}

	public static void writeAssertions(State state, StringBuilder result, Set<AssertionObject> objects, int depth)
			throws UnsupportedExpressionException {
		for (Assertion assertion : state.getAssertions()) {
			result.append(StringUtils.repeat("\t", depth));
			writeAssertion(assertion, result, objects, depth);
			result.append("\n");
		}
	}

	private static void writeAssertion(Assertion assertion, StringBuilder result, Set<AssertionObject> objects,
			int depth) throws UnsupportedExpressionException {
		if (assertion instanceof LogicalAssertion)
			writeLogicalAssertion(LogicalAssertion.class.cast(assertion), result, objects, depth);
		else if (assertion instanceof QuantifierAssertion)
			writeQuantifierAssertion(QuantifierAssertion.class.cast(assertion), result, objects, depth);
		else if (assertion instanceof Predicate)
			writePredicate(Predicate.class.cast(assertion), result, objects, depth);
	}

	private static void writePredicate(Predicate predicate, StringBuilder result, Set<AssertionObject> objects,
			int depth) throws UnsupportedExpressionException {

		// if objects are defined we have to replace variables with
		// corresponding objects, if more than one assignment exists an error
		// has to be thrown
		if (objects != null) {

			// first lets identify constants (which are already defined in the
			// domain) and objects within the predicate
			result.append("(" + predicate.getName());
			for (AssertionEntity entity : predicate.getArguments()) {

				// check if a variable is given as entity (which results in an
				// error, because there should be no variables here
				if (AssertionVariable.class.isInstance(entity)) {
					AssertionVariable variable = AssertionVariable.class.cast(entity);

					// lets try to identify the matching object
					AssertionObject object = null;
					for (AssertionObject candidate : objects) {
						if (candidate.getTypeName().equals(variable.getTypeName())) {
							if (object != null)
								throw new UnsupportedExpressionException("More than one assignment for variable "
										+ variable.getName() + " with type " + variable.getTypeName() + " exists.");
							object = candidate;
						}
					}
					if (object == null)
						throw new UnsupportedExpressionException("No matching object for variable "
								+ variable.getName() + " found.");
					result.append(" " + object.getName());
				} else if (AssertionObject.class.isInstance(entity)) {
					result.append(" " + entity.getName());
				}
			}
			result.append(")");
		}

		// if no objects are given, we can easily use the variables instead
		else {
			result.append("(" + predicate.getName());
			for (AssertionEntity entity : predicate.getArguments()) {
				result.append(" ");
				if (AssertionVariable.class.isInstance(entity))
					result.append("?");
				result.append(entity.getName());
			}
			result.append(")");
		}
	}

	private static void writeLogicalAssertion(LogicalAssertion logicalAssertion, StringBuilder result,
			Set<AssertionObject> objects, int depth) throws UnsupportedExpressionException {

		// write connective type
		switch (logicalAssertion.getType()) {
		case AND:
			result.append("(AND");
			break;
		case OR:
			result.append("(OR");
			break;
		case NOT:
			result.append("(NOT");
			break;
		}

		// write arguments
		for (Assertion assertion : logicalAssertion.getArguments()) {
			result.append(" ");
			writeAssertion(assertion, result, objects, depth + 1);
		}
		result.append(")");
	}

	private static void writeQuantifierAssertion(QuantifierAssertion assertion, StringBuilder result,
			Set<AssertionObject> objects, int depth) throws UnsupportedExpressionException {

		// write quantifier type
		switch (assertion.getType()) {
		case EXISTS:
			result.append("(EXIST (");
			break;
		case FORALL:
			result.append("(FORALL (");
			break;
		}
		// write variables
		writeVariables(assertion.getParameters(), result);
		result.append(") ");

		// write preconditions
		if (assertion.getPreconditions().size() > 0) {
			result.append("(WHEN (AND");
			for (Assertion argument : assertion.getPreconditions()) {
				result.append(" ");
				writeAssertion(argument, result, objects, depth + 1);
			}
			result.append(")");
		}

		// write effects
		result.append("(AND");
		for (Assertion argument : assertion.getEffects()) {
			result.append(" ");
			writeAssertion(argument, result, objects, depth + 1);
		}
		result.append("))");

		// close "(WHEN" bracket
		if (assertion.getPreconditions().size() > 0)
			result.append(")");
	}
}
