package org.adiusframework.resource;

public class UnclassifiedCapability implements ResourceCapability {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 1802124079462639417L;

	@Override
	public boolean equals(Object o) {
		return o.getClass().equals(UnclassifiedCapability.class);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
