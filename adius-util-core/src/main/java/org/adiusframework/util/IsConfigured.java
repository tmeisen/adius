package org.adiusframework.util;

/**
 * Indicates that the class implementing this interface is a infrastructure
 * object and provides the possibility to check if the class itself was
 * configured properly.
 */
public interface IsConfigured {

	/**
	 * Determines if this class was configured properly.
	 * 
	 * @return True if the configuration is proper, false otherwise.
	 */
	public abstract boolean checkConfiguration();
}
