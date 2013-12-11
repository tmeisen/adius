package org.adiusframework.resource.state;

public class AssertionObject extends AssertionEntity {
	private static final long serialVersionUID = 683624938159079456L;

	private boolean constant;

	public AssertionObject(String typeName, String name, boolean constant) {
		super(typeName, name);
		setConstant(constant);
	}

	public boolean isConstant() {
		return constant;
	}

	public void setConstant(boolean constant) {
		this.constant = constant;
	}

	@Override
	public String toString() {
		if (isConstant()) {
			StringBuilder builder = new StringBuilder();
			builder.append("CONST_");
			builder.append(super.toString());
			return builder.toString();
		}
		return super.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (constant ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssertionObject other = (AssertionObject) obj;
		if (constant != other.constant)
			return false;
		return true;
	}

}
