package org.adiusframework.util.datastructures;

import java.util.Set;

/**
 * The Tree interface defines a element of a Tree-data-structure.
 */
public interface Tree {

	/**
	 * Returns all elements which are children of this Tree-element.
	 * 
	 * @return A Set containing all children.
	 */
	public Set<? extends Tree> getChilds();

	/**
	 * Returns all elements which are parents of this Tree-element.
	 * 
	 * @return A Set containing all parents.
	 */
	public Set<? extends Tree> getParents();
}
