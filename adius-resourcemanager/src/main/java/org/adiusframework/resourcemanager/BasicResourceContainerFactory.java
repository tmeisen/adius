package org.adiusframework.resourcemanager;

/**
 * The BasicResourceContainerFactory is very simple implementation of the
 * ResourceContainerFactory interface.
 */
public class BasicResourceContainerFactory implements ResourceContainerFactory {

	private CapabilityRuleValidator capabilityRuleValidator;

	/**
	 * Returns the CapabilityRuleValidator which is currently inserted in new
	 * created containers.
	 * 
	 * @return The current CapabilityRuleValidator.
	 */
	public CapabilityRuleValidator getCapabilityRuleValidator() {
		return capabilityRuleValidator;
	}

	/**
	 * Sets a new CapabilityRuleValidator which will be inserted in all
	 * containers that are created furthermore.
	 * 
	 * @param capabilityRuleValidator
	 *            The new CapabilityRuleValidator.
	 */
	public void setCapabilityRuleValidator(CapabilityRuleValidator capabilityRuleValidator) {
		this.capabilityRuleValidator = capabilityRuleValidator;
	}

	@Override
	public HierarchyResourceContainer create(String identifier) {
		return new BasicResourceContainer(identifier, getCapabilityRuleValidator());
	}

	@Override
	public boolean checkConfiguration() {
		if (capabilityRuleValidator == null)
			return false;

		return capabilityRuleValidator.checkConfiguration();
	}
}
