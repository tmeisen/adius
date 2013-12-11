package org.adiusframework.resourcemanager;

import java.util.Properties;

import org.adiusframework.resource.ResourceCapability;
import org.adiusframework.resource.ResourceCapabilityRule;
import org.adiusframework.util.IsConfigured;

/**
 * The CapabilityRuleValidator defines a method to verify that a rule which is
 * used to find Resources is fulfilled.
 */
public interface CapabilityRuleValidator extends IsConfigured {

	/**
	 * Checks if a given rule is satisfied for a given capability.
	 * 
	 * @param rule
	 *            The rule which should be checked.
	 * @param execConditions
	 *            Additional conditions.
	 * @param capability
	 *            The given capability.
	 * @return True if the rule is satisfied, false otherwise.
	 */
	public boolean isSatisfied(ResourceCapabilityRule rule, Properties execConditions, ResourceCapability capability);

}
