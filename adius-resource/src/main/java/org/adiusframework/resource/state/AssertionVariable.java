package org.adiusframework.resource.state;

public class AssertionVariable extends AssertionEntity {
	private static final long serialVersionUID = 683624938159079456L;

	public AssertionVariable(String typeName, String name) {
		super(typeName, name);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VAR_");
		builder.append(super.toString());
		return builder.toString();
	}

}
