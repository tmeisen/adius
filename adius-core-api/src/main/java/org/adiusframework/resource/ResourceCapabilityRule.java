package org.adiusframework.resource;

import java.io.Serializable;

public interface ResourceCapabilityRule extends Serializable {

	/**
	 * Checks if the given capability satisfies the rule.
	 * 
	 * @param capability
	 *            capability that has to be validated
	 * @return true if and only if the given capability satisfies the rule.
	 */
	public boolean satisfiedBy(ResourceCapability capability);

}
