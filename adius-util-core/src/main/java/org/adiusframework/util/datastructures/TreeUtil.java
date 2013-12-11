package org.adiusframework.util.datastructures;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * The TreeUtil class provides static methods to deal easily with Trees.
 * 
 * @see Tree
 */
public class TreeUtil {

	/**
	 * Determines if a given Tree contains cycles or is acyclic.
	 * 
	 * @param root
	 *            The root element of the Tree which is analyzed.
	 * @param steps
	 *            The elements of the Tree.
	 * @return True if the Tree is acyclic, false if it contains cycles.
	 */
	public static <T extends Tree> Boolean isAcyclic(T root, Collection<T> steps) {
		// determine root of process, if more than one root exists validation
		// failed
		if (root == null)
			return false;

		// generate temporary map
		Map<Tree, Integer> parentCounter = new HashMap<Tree, Integer>();
		for (Tree step : steps)
			parentCounter.put(step, step.getParents().size());

		// remove entries as long as an element with zero parents exists
		boolean found = true;
		while (found && parentCounter.size() > 0) {
			found = false;
			Vector<Tree> deleteMarker = new Vector<Tree>();
			for (Map.Entry<Tree, Integer> entry : parentCounter.entrySet()) {
				if (entry.getValue() == 0) {
					for (Tree child : entry.getKey().getChilds())
						if (parentCounter.containsKey(child)) {
							Integer count = parentCounter.get(child);
							count = count - 1;
							parentCounter.put(child, count);
						}
					deleteMarker.add(entry.getKey());
					found = true;
				}
			}
			for (Tree delete : deleteMarker)
				parentCounter.remove(delete);
		}
		return found;
	}

	/**
	 * Determines all elements that are leaves, that mean that their have not
	 * children.
	 * 
	 * @param steps
	 *            The Tree to be analyzed.
	 * @return A Set containing all leaves.
	 */
	public static <T extends Tree> Set<T> getLeaves(Collection<T> steps) {
		Set<T> leaves = new HashSet<T>();
		for (T step : steps) {
			if (step.getChilds().size() == 0)
				leaves.add(step);
		}
		return leaves;
	}

	/**
	 * Determines the root of a given Tree.
	 * 
	 * @param steps
	 *            The elements of the Tree.
	 * @return The root if an unique was found, false if not root was found or
	 *         there is more than one.
	 */
	public static <T extends Tree> T getRoot(Map<String, T> steps) {
		T root = null;
		for (T step : steps.values()) {
			if (step.getParents().size() == 0) {
				if (root != null)
					return null;
				root = step;
			}
		}
		return root;
	}
}
