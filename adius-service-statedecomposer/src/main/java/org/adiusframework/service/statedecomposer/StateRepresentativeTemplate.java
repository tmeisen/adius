package org.adiusframework.service.statedecomposer;

import java.util.List;

public class StateRepresentativeTemplate {

	private List<String> types;

	public StateRepresentativeTemplate(List<String> types) {
		this.types = types;
	}

	public int size() {
		return types.size();
	}

	public String getType(int index) {
		return types.get(index);
	}

	public int indexOf(String type) {
		return types.indexOf(type);
	}

	@Override
	public String toString() {
		return types.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((types == null) ? 0 : types.hashCode());
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
		StateRepresentativeTemplate other = (StateRepresentativeTemplate) obj;
		if (types == null) {
			if (other.types != null)
				return false;
		} else if (!types.equals(other.types))
			return false;
		return true;
	}

}
