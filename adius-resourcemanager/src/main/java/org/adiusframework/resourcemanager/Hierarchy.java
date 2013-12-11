package org.adiusframework.resourcemanager;

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.adiusframework.resource.Resource;
import org.adiusframework.util.IsConfigured;
import org.adiusframework.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Hierarchy manages several HierarchyResourceContainers which are addressed
 * by URIs. As URIs have a parent-child-structure, the containers are connected.
 * That means, that if a Resource was not found in a container, the Hierarchy
 * will search the parents until it reaches the root.
 */
public class Hierarchy implements IsConfigured {

	public static final String URIDELIMITER = ".";

	private static final Logger LOGGER = LoggerFactory.getLogger(Hierarchy.class);

	private HierarchyResourceContainer root;

	private ResourceContainerFactory rcf;

	private ConcurrentMap<String, HierarchyResourceContainer> cmap;

	/**
	 * Creates a new Hierarchy with no HierarchyResourceContaienrFactory.
	 */
	public Hierarchy() {
		cmap = new ConcurrentHashMap<String, HierarchyResourceContainer>();
	}

	/**
	 * Creates a new Hierarchy with a given HierarchyResourceContaienrFactory
	 * and initializes the root.
	 * 
	 * @param rcf
	 *            The given HierarchyResourceContaienrFactory.
	 */
	public Hierarchy(ResourceContainerFactory rcf) {
		root = rcf.create("root");
		cmap = new ConcurrentHashMap<String, HierarchyResourceContainer>();
		this.rcf = rcf;
	}

	/**
	 * Return the current root.
	 * 
	 * @return The current root.
	 */
	public HierarchyResourceContainer getRoot() {
		return root;
	}

	@Override
	public boolean checkConfiguration() {
		if (root == null || rcf == null || cmap == null)
			return false;

		return root.checkConfiguration() && rcf.checkConfiguration();
	}

	/**
	 * Returns the container for a given URI, and if it does not exist, tries to
	 * evaluate the next parent.
	 * 
	 * @param uri
	 *            The URI of the searched container.
	 * @param parent
	 *            True if the parent is wanted.
	 * @return The found container or null if no container was found.
	 */
	public ResourceContainer getContainer(String uri, boolean parent) {

		// check if root has to be returned
		LOGGER.debug("Searching for container " + uri);
		if (uri.isEmpty()) {
			LOGGER.debug("Returning root");
			return getRoot();
		}

		// check if a container exist for this uri
		ResourceContainer c = cmap.get(uri);
		if (c != null)
			return c;
		else if (!parent)
			return null;

		// check if a parent exist (parent is true)
		String puri = StringUtil.shortenUri(uri, URIDELIMITER);
		return getContainer(puri, parent);
	}

	/**
	 * Adds a new (empty) container to the Hierarchy.
	 * 
	 * @param uri
	 *            The URI, which should address the new container.
	 * @return The new container.
	 */
	public ResourceContainer addContainer(String uri) {

		// check if we have to return the root
		LOGGER.debug("Searching for container at uri " + uri);
		if (uri.isEmpty())
			return getRoot();

		// check if a container exists for the given uri
		ResourceContainer rc = cmap.get(uri);
		if (rc != null)
			return rc;

		// otherwise we create a new container and update all the children
		HierarchyResourceContainer hrc = rcf.create(StringUtil.getIdentifierInUri(uri, URIDELIMITER));
		updateParent(uri, hrc);
		updateChildren(uri, hrc);
		cmap.put(uri, hrc);
		return hrc;
	}

	/**
	 * Adds a HierarchyResourceContainer to the Hierarchy.
	 * 
	 * @param uri
	 *            The URI, which should address the new container.
	 * @param hrc
	 *            The container to be added.
	 * @return Null if the URI is empty or already used, the given container
	 *         otherwise.
	 */
	public ResourceContainer addContainer(String uri, HierarchyResourceContainer hrc) {

		// check if the uri is valid
		if (uri.isEmpty())
			return null;
		if (cmap.get(uri) != null)
			return null;

		// add container and update hierarchy
		cmap.put(uri, hrc);
		updateParent(uri, hrc);
		updateChildren(uri, hrc);
		return hrc;
	}

	/**
	 * Sets a new ResourceContainerFactory.
	 * 
	 * @param rcf
	 *            The new factory.
	 */
	public void setRcf(ResourceContainerFactory rcf) {
		this.root = rcf.create("root");
		this.rcf = rcf;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("root: ");
		buffer.append(root);
		buffer.append("\n");
		List<String> kl = new Vector<String>(cmap.keySet());
		Collections.sort(kl);
		for (String key : kl) {
			buffer.append(key);
			buffer.append(": ");
			buffer.append(cmap.get(key).toString());
			buffer.append("\n");
		}
		return buffer.toString();
	}

	/**
	 * Evaluates the next URi of the next parent, which addresses a container.
	 * 
	 * @param childUri
	 *            The URI of the child.
	 * @return An empty String if no parent was found, the URI of the parent
	 *         otherwise.
	 */
	private String findParentUri(String childUri) {
		String uri = StringUtil.shortenUri(childUri, URIDELIMITER);
		while (!uri.isEmpty()) {
			if (cmap.containsKey(uri))
				return uri;
			uri = StringUtil.shortenUri(uri, URIDELIMITER);
		}
		return "";
	}

	/**
	 * Updates the parent of a given container with a given URI.
	 * 
	 * @param childUri
	 *            The URI of the container.
	 * @param hrc
	 *            The container itself.
	 */
	private void updateParent(String childUri, HierarchyResourceContainer hrc) {
		String uri = findParentUri(childUri);
		HierarchyResourceContainer parent = null;
		if (uri.isEmpty())
			parent = getRoot();
		else
			parent = cmap.get(uri);
		LOGGER.debug("Setting parent of " + childUri + " to " + (uri.isEmpty() ? "root" : uri));
		hrc.setParent(parent);
	}

	/**
	 * Updates the children of a given container with a given URI.
	 * 
	 * @param parentUri
	 *            The URI of the container.
	 * @param hrc
	 *            The container itself.
	 */
	private void updateChildren(String parentUri, HierarchyResourceContainer hrc) {

		// find all uris that are 'after' the parentUri
		List<String> uris = new Vector<String>();
		for (String uri : cmap.keySet()) {
			if (uri.startsWith(parentUri + URIDELIMITER) || parentUri.isEmpty()) {
				LOGGER.debug("Found child candidate " + uri);
				boolean toAdd = true;
				for (int i = 0; i < uris.size(); i++) {
					String verify = uris.get(i);
					if (verify.startsWith(uri + URIDELIMITER)) {
						LOGGER.debug("Removing uri from candidate list: " + verify);
						uris.remove(i);
						i--;
					} else if (uri.startsWith(verify + URIDELIMITER)) {
						toAdd = false;
					}
				}
				if (toAdd) {
					LOGGER.debug("Adding uri to candidate list: " + uri);
					uris.add(uri);
				}
			}
		}

		// finally we have to set the new parent
		for (String uri : uris) {
			LOGGER.debug("Setting parent of " + uri + " to " + (parentUri.isEmpty() ? "root" : parentUri));
			cmap.get(uri).setParent(hrc);
		}
	}

	/**
	 * Removes a container and all children from the Hierarchy.
	 * 
	 * @param uri
	 *            The URI of the top-most container which should be removed.
	 * @param removeChilds
	 *            True, if children should be removed also.
	 * @return A List of all Resources which belong to removes containers.
	 */
	public List<Resource> removeContainers(String uri, boolean removeChilds) {

		// we need a list to store all resources within removed containers
		List<Resource> rlist = new Vector<Resource>();

		// lets start with the main container
		HierarchyResourceContainer hrc = cmap.get(uri);
		if (hrc != null)
			rlist.addAll(removeContainer(uri));

		// now lets move on with the children
		if (removeChilds) {
			for (String candUri : cmap.keySet()) {

				// lets check if we have a child
				if (candUri.startsWith(uri + URIDELIMITER))
					rlist.addAll(removeContainer(candUri));
			}
		}
		return rlist;
	}

	/**
	 * Removes a single container from the Hierarchy.
	 * 
	 * @param uri
	 *            The URI of the top-most container which should be removed.
	 * @return A List of all Resources which belong to the removed container.
	 */
	public List<Resource> removeContainer(String uri) {

		// first we search the container and if we found one we store the
		// resources
		List<Resource> rlist = new Vector<Resource>();
		HierarchyResourceContainer hrc = cmap.get(uri);
		if (hrc != null) {
			rlist.addAll(hrc.getAllResources());
			LOGGER.debug("Removing " + uri + ": " + hrc);
			cmap.remove(uri);

			// after we have removed the container we have to update the parent
			// references; therefore we have to identify the new parent and
			// update the children
			String parentUri = findParentUri(uri);
			updateChildren(parentUri, parentUri.isEmpty() ? getRoot() : cmap.get(parentUri));
		}
		return rlist;
	}

}
