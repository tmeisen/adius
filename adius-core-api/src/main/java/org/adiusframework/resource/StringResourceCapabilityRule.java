package org.adiusframework.resource;

public class StringResourceCapabilityRule implements ResourceCapabilityRule {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -4631338555885255588L;

	private String rule;

	public StringResourceCapabilityRule() {
		rule = "";
	}

	public StringResourceCapabilityRule(String rule) {
		this.rule = rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getRule() {
		return rule;
	}

	@Override
	public boolean satisfiedBy(ResourceCapability capability) {
		if (StringResourceCapability.class.isInstance(capability)) {
			StringResourceCapability strCap = (StringResourceCapability) capability;
			return rule.equals(strCap.getDescription());
		}
		return false;
	}

	@Override
	public String toString() {
		return this.rule;
	}
}
