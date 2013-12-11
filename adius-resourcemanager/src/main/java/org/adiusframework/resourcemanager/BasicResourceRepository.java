package org.adiusframework.resourcemanager;

import java.util.List;
import java.util.Map;

import org.adiusframework.resource.Resource;

/**
 * The BasicResourceRepository fulfills the requirements of the
 * ResourceRepository interface by saving a Hierarchy object and store the
 * information in it. The implementation of the methods manage the access to
 * that object.
 */
public class BasicResourceRepository implements ResourceRepository {

	private Hierarchy hierarchy;

	/**
	 * Creates a new BasicResourceRepository with no ResourceContainerFactory.
	 */
	public BasicResourceRepository() {
		hierarchy = new Hierarchy();
	}

	/**
	 * Creates a new BasicResourceRepository with the specified
	 * ResourceContainerFactory. This object will passed through to the internal
	 * Hierarchy object.
	 * 
	 * @param rcf
	 *            The specified factory.
	 */
	public BasicResourceRepository(final ResourceContainerFactory rcf) {
		hierarchy = new Hierarchy(rcf);
	}

	/**
	 * This constructor initializes the Hierarchy moreover with the given
	 * Resource objects.
	 * 
	 * @param rcf
	 *            The specified factory.
	 * @param resources
	 *            A Map which stores Lists of Resources based on the identifier
	 *            of the related container.
	 */
	public BasicResourceRepository(final ResourceContainerFactory rcf, final Map<String, List<Resource>> resources) {
		this(rcf);
		setContainers(resources);
	}

	@Override
	public ResourceContainer getContainer(String uri, boolean parent) {
		return hierarchy.getContainer(uri, parent);
	}

	@Override
	public ResourceContainer setContainer(String uri) {
		return hierarchy.addContainer(uri);
	}

	/**
	 * Sets a new Hierarchy.
	 * 
	 * @param hierarchie
	 *            The new Hierarchy.
	 */
	public void setHierarchy(Hierarchy hierarchy) {
		this.hierarchy = hierarchy;
	}

	@Override
	public boolean checkConfiguration() {
		if (hierarchy == null)
			return false;

		return hierarchy.checkConfiguration();
	}

	@Override
	public void setContainers(final Map<String, List<Resource>> resources) {
		for (String uri : resources.keySet()) {
			ResourceContainer c = getContainer(uri, false);
			if (c == null)
				c = hierarchy.addContainer(uri);
			final List<Resource> list = resources.get(uri);
			for (final Resource r : list)
				c.addResource(r);
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Registered resources:\n");
		buffer.append(hierarchy.toString());
		return buffer.toString();
	}

	@Override
	public List<Resource> removeContainers(String uri, boolean removeChilds) {
		return hierarchy.removeContainers(uri, removeChilds);
	}
}
