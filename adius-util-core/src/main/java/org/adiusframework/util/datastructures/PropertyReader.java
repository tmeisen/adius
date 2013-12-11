package org.adiusframework.util.datastructures;

import java.io.Serializable;

/**
 * The PropertyReader interface defines a class which manages properties.
 */
public interface PropertyReader extends Serializable {

	/**
	 * Return the value which is related to the given key. Therefore the key
	 * must be converted into a String.
	 * 
	 * @param key
	 *            The given key.
	 * @return The value or null if the key does not exist.
	 */
	public abstract String getProperty(Object key);

	/**
	 * Evaluates if a given key exists.
	 * 
	 * @param key
	 *            The given key.
	 * @return True if it exists, false otherwise.
	 */
	public abstract boolean containsKey(Object key);

	/**
	 * Evaluates if all keys of a given Array exist.
	 * 
	 * @param keys
	 *            The Array of keys.
	 * @return True if all exist, false otherwise.
	 */
	public abstract boolean containsKeys(String... keys);

	/**
	 * Evaluates if all keys of a given Array exist. Therefore the Objects in
	 * the Arrays must be converted into a String.
	 * 
	 * @param keys
	 *            The Array of keys.
	 * @return True if all exist, false otherwise.
	 */
	public abstract boolean containsKeys(Object... keys);

	/**
	 * Return the value which is related to the given key.
	 * 
	 * @param property
	 *            The given key.
	 * @return The value or null if the key does not exist.
	 */
	public abstract String getProperty(String property);

	/**
	 * Return the value which is related to the given key.
	 * 
	 * @param property
	 *            The given key.
	 * @param defaultValue
	 *            The default-value if the key does not exist.
	 * @return The value or the default-value if the key does not exist.
	 */
	public abstract String getProperty(String property, String defaultValue);
}
