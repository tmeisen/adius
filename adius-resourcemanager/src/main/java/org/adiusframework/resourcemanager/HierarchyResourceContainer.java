package org.adiusframework.resourcemanager;

/**
 * Extends the ResourceContainer interface by adding the possibility to set a
 * parent-object, also a HierarchyResourceContainer. The request of finding
 * Resources with special condition will be accomplished by both
 * ResourceContainers.
 */
public interface HierarchyResourceContainer extends ResourceContainer {

	/**
	 * Sets a new parent.
	 * 
	 * @param parent
	 *            The new parent.
	 */
	public void setParent(HierarchyResourceContainer parent);

	/**
	 * Return the ResourceContainer which is currently set to be the parent of
	 * the HierarchyResourceContainer.
	 * 
	 * @return The current parent.
	 */
	public ResourceContainer getParent();

	/**
	 * Returns a String which identifies the HierarchyResourceContainer.
	 * 
	 * @return The current identifier.
	 */
	public String getIdentifier();

}
