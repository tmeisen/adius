package org.adiusframework.service.statedecomposer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class StateRepresentativeList {

	private StateRepresentativeTemplate template;

	private List<StateRepresentative> representatives;

	private Map<String, List<String>> objects;

	private boolean isChanged;

	public StateRepresentativeList(StateRepresentativeTemplate template) {
		this.template = template;
		objects = new HashMap<String, List<String>>();
		isChanged = true;
	}

	/**
	 * Filters all state representatives that have a part that matches the given
	 * type and representative part. If the list of state representatives an
	 * update is triggered.
	 * 
	 * @param type
	 *            name of the type that is addressed by the representative part
	 * @param representativePart
	 *            value of the representative part
	 * @return filtered list, so that each representative state in this list
	 *         contains a representative part of the given type.
	 */
	public List<StateRepresentative> getRepresentatives(String type, String representativePart) {

		// check if the current set is up-to-date, if not first update the set
		updateRepresentatives();

		// now we have to filter the set of representatives, according to the
		// filter criteria
		List<StateRepresentative> result = new Vector<StateRepresentative>();
		for (StateRepresentative representative : representatives) {
			if (representative.hasPart(type, representativePart))
				result.add(representative);
		}
		return result;
	}

	public void addRepresentivePart(String type, String representativePart) {
		isChanged = true;
		List<String> parts = objects.get(type);
		if (parts == null) {
			parts = new Vector<String>();
			objects.put(type, parts);
		}
		if (!parts.contains(representativePart))
			parts.add(representativePart);
	}

	protected void updateRepresentatives() {

		// check if update is required
		if (!isChanged)
			return;

		// lets check if objects are listed
		if (objects.isEmpty()) {
			representatives = new Vector<StateRepresentative>();
			return;
		}

		// lets create the set of representatives
		// first we create an initial list
		List<StateRepresentative> list = new Vector<StateRepresentative>();
		Iterator<String> iter = objects.keySet().iterator();
		String key = iter.next();
		for (String part : objects.get(key)) {
			StateRepresentative representative = new StateRepresentative(template);
			representative.addPart(key, part);
			list.add(representative);
		}

		// now we extend this list by the other types
		while (iter.hasNext()) {

			// getting next type
			key = iter.next();
			List<String> parts = objects.get(key);

			// for each existing state representative entry.size() clones have
			// to be created, where each clone represents one entry for this
			// type
			List<StateRepresentative> newList = new Vector<StateRepresentative>();
			for (StateRepresentative representative : list) {
				for (String part : parts) {
					StateRepresentative clone = (StateRepresentative) representative.clone();
					clone.addPart(key, part);
					newList.add(clone);
				}
			}
			list = newList;
		}
		representatives = list;

		// now we have the final set of representatives for the list of objects,
		// if nothing changes this set will not change
		isChanged = false;
	}

	public List<StateRepresentative> getRepresentatives() {
		updateRepresentatives();
		return representatives;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Up-to-date ").append(!isChanged).append("\n");
		builder.append(template.toString());
		if (representatives != null)
			builder.append("\n").append(representatives.toString());
		return builder.toString();
	}

}
