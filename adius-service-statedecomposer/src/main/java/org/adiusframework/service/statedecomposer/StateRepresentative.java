package org.adiusframework.service.statedecomposer;

import java.util.List;
import java.util.Vector;

public class StateRepresentative {

	private StateRepresentativeTemplate template;

	private List<String> parts;

	public StateRepresentative(StateRepresentativeTemplate template) {
		this.template = template;
		parts = new Vector<String>(template.size());
		for (int i = 0; i < template.size(); i++)
			parts.add(null);
	}

	public void addPart(String type, String part) {
		int index = template.indexOf(type);
		if (index < 0)
			return;
		parts.add(index, part);
	}

	public boolean hasPart(String type, String part) {
		int index = template.indexOf(type);
		if (index > -1) {
			String value = parts.get(index);
			return (value == null ? false : value.equals(part));
		}
		return false;
	}

	@Override
	public Object clone() {
		StateRepresentative clone = new StateRepresentative(template);
		for (int i = 0; i < parts.size(); i++) {
			clone.addPart(template.getType(i), parts.get(i));
		}
		return clone;
	}

	@Override
	public String toString() {
		return parts.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parts == null) ? 0 : parts.hashCode());
		result = prime * result + ((template == null) ? 0 : template.hashCode());
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
		StateRepresentative other = (StateRepresentative) obj;
		if (parts == null) {
			if (other.parts != null)
				return false;
		} else if (!parts.equals(other.parts))
			return false;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		return true;
	}

}
