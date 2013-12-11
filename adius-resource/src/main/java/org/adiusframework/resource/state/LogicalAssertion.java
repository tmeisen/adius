package org.adiusframework.resource.state;

import java.util.List;

public class LogicalAssertion implements Assertion {
	private static final long serialVersionUID = 4697962955027004238L;

	public enum LogicalConnectiveType {
		AND, OR, NOT
	}

	private LogicalConnectiveType type;

	private List<Assertion> arguments;

	public LogicalAssertion(LogicalConnectiveType type, List<Assertion> arguments) {
		this.type = type;
		this.arguments = arguments;
	}

	public LogicalConnectiveType getType() {
		return type;
	}

	public List<Assertion> getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getType().toString()).append(" (");
		for (Assertion assertion : getArguments()) {
			builder.append(assertion).append(", ");
		}
		builder.delete(builder.length() - 2, builder.length());
		builder.append(")");
		return builder.toString();
	}
}
