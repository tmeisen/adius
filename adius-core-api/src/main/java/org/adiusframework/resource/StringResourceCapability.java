package org.adiusframework.resource;

public class StringResourceCapability implements ResourceCapability {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 8092945934860659650L;

	private String description;

	public StringResourceCapability() {
		description = "";
	}

	public StringResourceCapability(String description) {
		this.description = description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		StringResourceCapability other = (StringResourceCapability) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

}
