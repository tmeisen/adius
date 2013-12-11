package org.adiusframework.resource.state;

import java.util.List;

/**
 * The state of a quantifier predicate.
 * 
 * @author Alexander Tenbrock
 * 
 */
public class QuantifierAssertion implements Assertion {
	private static final long serialVersionUID = 683624938159079456L;

	/**
	 * An enum describing the quantifier type
	 */
	public enum QuantifierType {
		EXISTS, FORALL
	}

	/**
	 * The type of this quantifier.
	 */
	private QuantifierType type;

	private List<AssertionVariable> parameters;

	private List<Assertion> preconditions;

	private List<Assertion> effects;

	/**
	 * The constructor.
	 * 
	 * @param type
	 *            the type of this qualifier
	 * @param parameters
	 *            the parameters for this quantifier
	 * @param preconditions
	 *            the preconditions
	 * @param effects
	 *            the effects
	 */
	public QuantifierAssertion(QuantifierType type, List<AssertionVariable> parameters, List<Assertion> preconditions,
			List<Assertion> effects) {
		this.type = type;
		this.parameters = parameters;
		this.preconditions = preconditions;
		this.effects = effects;
	}

	/**
	 * Returns the type of this quantifier.
	 * 
	 * @return the quantifier type
	 */
	public QuantifierType getType() {
		return this.type;
	}

	public List<AssertionVariable> getParameters() {
		return this.parameters;
	}

	public List<Assertion> getPreconditions() {
		return this.preconditions;
	}

	public List<Assertion> getEffects() {
		return this.effects;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		// variables
		builder.append(getType().toString()).append(": Variables (");
		for (AssertionVariable variable : getParameters()) {
			builder.append(variable.toString()).append(", ");
		}
		builder.delete(builder.length() - 2, builder.length());
		builder.append(")");

		// precondition
		builder.append(getType().toString()).append(": Precondition (");
		for (Assertion assertion : getPreconditions()) {
			builder.append(assertion.toString()).append(", ");
		}
		builder.delete(builder.length() - 2, builder.length());
		builder.append(")");

		// effect
		builder.append(getType().toString()).append(": Effect (");
		for (Assertion assertion : getEffects()) {
			builder.append(assertion.toString()).append(", ");
		}
		builder.delete(builder.length() - 2, builder.length());
		builder.append(")");

		return builder.toString();
	}
}
