package org.adiusframework.util.db;

public class DataManipulationException extends RuntimeException {

	/**
	 * Version for serialization
	 */
	private static final long serialVersionUID = 5214231545846974631L;

	public DataManipulationException(String description) {
		super(description);
	}
}
