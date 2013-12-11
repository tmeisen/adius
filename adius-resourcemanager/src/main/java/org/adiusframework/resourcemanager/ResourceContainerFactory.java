package org.adiusframework.resourcemanager;

import org.adiusframework.util.IsConfigured;

/**
 * The ResourceContainerFactory provides the possibility to create
 * ResourceCOntainers.
 */
public interface ResourceContainerFactory extends IsConfigured {

	/**
	 * Create a new HierarchyResourceContainer with the given parameter.
	 * 
	 * @param identifier
	 *            A String identifying the new container.
	 * @return The created container.
	 */
	public HierarchyResourceContainer create(String identifier);

}
