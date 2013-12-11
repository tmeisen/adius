package org.adiusframework.resourcemanager;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.adiusframework.resource.ResourceCapabilityRule;
import org.adiusframework.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BasicResourceContainer is a simple implementation of the
 * HierarchyResourceContainer interface. Therefore it has several members which
 * store the information which are wanted.
 */
public class BasicResourceContainer implements HierarchyResourceContainer {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -5436720657334671310L;

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicResourceContainer.class);

	/**
	 * A List to store all Resource objects which belong to this
	 * ResourceContainer.
	 */
	private List<Resource> rlist;

	private ResourceContainer parent;

	private String identifier;

	private CapabilityRuleValidator capabilityRuleValidator;

	/**
	 * Create a new BasicResourceContainer object with no parameters. The parent
	 * will be null and the inner List, which stores the Resources, will be
	 * empty.
	 */
	public BasicResourceContainer() {
		setParent(null);
		rlist = new Vector<Resource>();
	}

	/**
	 * Create a new BasicResourceContainer object with the given parameters. The
	 * parent will be null and the inner List, which stores the Resources, will
	 * be empty.
	 * 
	 * @param identifier
	 *            The String identifying this ResoruceContainer in a hierarchy.
	 * @param capabilityRuleValidator
	 *            The CapabilityRuleValidator which is used to find Resources
	 *            with conditions.
	 */
	public BasicResourceContainer(String identifier, CapabilityRuleValidator capabilityRuleValidator) {
		setParent(null);
		setIdentifier(identifier);
		setCapabilityRuleValidator(capabilityRuleValidator);
		rlist = new Vector<Resource>();
	}

	@Override
	public void setParent(HierarchyResourceContainer parent) {
		this.parent = parent;
	}

	@Override
	public ResourceContainer getParent() {
		return parent;
	}

	/**
	 * Return the CapabilityRuleValidator which is currently used by the
	 * BasicResourceContainer.
	 * 
	 * @return The current CapabilityRuleValidator.
	 */
	public CapabilityRuleValidator getCapabilityRuleValidator() {
		return capabilityRuleValidator;
	}

	/**
	 * Sets a new CapabilityRuleValidator.
	 * 
	 * @param capabilityRuleValidator
	 *            The new CapabilityRuleValidator.
	 */
	protected void setCapabilityRuleValidator(CapabilityRuleValidator capabilityRuleValidator) {
		this.capabilityRuleValidator = capabilityRuleValidator;
	}

	@Override
	public boolean addResource(Resource resource) {
		LOGGER.debug("Adding resource " + resource + " to " + getIdentifier());
		return rlist.add(resource);
	}

	@Override
	public boolean removeResource(Resource resource) {
		return rlist.remove(resource);
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Sets a new String which identifies this HierarchyResourceContainer.
	 * 
	 * @param identifier
	 *            The new identifier.
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public boolean checkConfiguration() {
		if (rlist == null || identifier == null || capabilityRuleValidator == null)
			return false;

		return (parent == null || parent.checkConfiguration()) && capabilityRuleValidator.checkConfiguration();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[");
		buffer.append(getIdentifier());
		buffer.append(getIdentifier().isEmpty() ? "" : " - ");
		buffer.append(rlist.size());
		buffer.append(" entries, parent ");
		buffer.append(getParent() == null ? "null" : ((BasicResourceContainer) getParent()).getIdentifier());
		buffer.append("] ");
		for (Resource r : rlist) {
			buffer.append(r.toString());
			buffer.append(", ");
		}
		if (rlist.size() > 0)
			buffer.deleteCharAt(buffer.length() - 2);
		return buffer.toString();
	}

	@Override
	public List<Resource> getResources(ResourceCapabilityRule rule, boolean fullScan) {
		return getResources(rule, new Properties(), fullScan);
	}

	@Override
	public List<Resource> getResources(ResourceCapabilityRule rule, Properties execConditions, boolean fullScan) {
		LOGGER.debug("Searching for resources satisfying rule " + rule);
		List<Resource> rs = new Vector<Resource>();
		for (Resource r : rlist) {
			if (getCapabilityRuleValidator().isSatisfied(rule, execConditions, r.getCapability())) {
				LOGGER.debug("Rule is satsified by " + r);
				rs.add(r);
			}
		}
		if (parent != null && (fullScan || rs.size() == 0))
			rs.addAll(parent.getResources(rule, fullScan));
		LOGGER.debug("Found " + rs.size() + " resources");
		return rs;
	}

	@Override
	public List<Resource> getResourcesByClass(Class<? extends Resource> c, boolean fullScan) {
		LOGGER.debug("Searching for resources of class " + c);
		List<Resource> rs = new Vector<Resource>();
		for (Resource r : rlist) {
			if (c.isAssignableFrom(r.getClass()))
				rs.add(r);
		}
		if (parent != null && (fullScan || rs.size() == 0))
			rs.addAll(parent.getResourcesByClass(c, fullScan));
		return rs;
	}

	@Override
	public List<Resource> getAllResources() {
		List<Resource> dlist = new Vector<Resource>(rlist);
		Collections.copy(dlist, rlist);
		return dlist;
	}

}
