package org.adiusframework.resource.state;

import java.util.List;

public class Predicate implements Assertion {
	private static final long serialVersionUID = -1198270956410707522L;

	private String name;

	private List<AssertionEntity> arguments;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            name
	 * @param objects
	 *            tuples of individuals that fulfill the argument restrictions
	 */
	public Predicate(String name, List<AssertionEntity> arguments) {
		this.name = name;
		setArguments(arguments);
	}

	/**
	 * Returns the name of the predicate.
	 * 
	 * @return name of the feature
	 */
	public String getName() {
		return this.name;
	}

	public List<AssertionEntity> getArguments() {
		return arguments;
	}

	protected void setArguments(List<AssertionEntity> arguments) {
		this.arguments = arguments;
	}

	/**
	 * Checks if the predicate only contains objects as arguments and no
	 * variables.
	 * 
	 * @return true if the predicate is concrete
	 */
	public boolean isConcrete() {
		for (AssertionEntity entity : arguments) {
			if (AssertionVariable.class.isInstance(entity))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getName() + " (");

		for (int c = 0; c < getArguments().size(); c++) {
			if (c > 0)
				result.append(", ");
			if (getArguments().get(c) != null)
				result.append(getArguments().get(c));
			else
				result.append("null");
		}
		result.append(")");
		return result.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Predicate other = (Predicate) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
