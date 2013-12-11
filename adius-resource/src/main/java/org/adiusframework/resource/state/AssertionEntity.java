package org.adiusframework.resource.state;

import java.io.Serializable;

/**
 * @author Tobias Meisen
 */
public abstract class AssertionEntity implements Serializable {

	private static final long serialVersionUID = -3304496980061257143L;

	private String typeName;

	private String name;

	public AssertionEntity(String typeName, String name) {
		this.typeName = typeName;
		this.name = name;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getName()).append(":").append(getTypeName());
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
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
		AssertionEntity other = (AssertionEntity) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}

}
