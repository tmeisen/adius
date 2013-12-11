package org.adiusframework.util.datastructures;

import java.io.IOException;
import java.net.URL;

import org.adiusframework.util.exception.UnexpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of java.util.Properties to enable adding of object. Objects are
 * simple added by using the toString()-Method.
 */
public class Properties extends java.util.Properties implements PropertyReader {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -5399938947034779422L;

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Properties.class);

	/** A flag which indicates that a property is optional. */
	private static final String OPTIONAL_FLAG = "OPTIONAL";

	/**
	 * Creates a new Properties object without any properties.
	 */
	public Properties() {
	}

	/**
	 * Creates a new Properties object and loads its content from an URL.
	 * 
	 * @param url
	 *            The URL to be load from.
	 */
	public Properties(URL url) {
		super();
		try {
			LOGGER.debug("Loading properties from " + url.toString());
			this.load(url.openStream());
		} catch (IOException e) {
			LOGGER.error("Property loading failed");
			throw new UnexpectedException(e);
		}
	}

	/**
	 * Sets all properties of an Array which has the structure [KEY_1; VALUE_1;
	 * KEY_2; ...]. Therefore it should have an even number of elements.
	 * 
	 * @param props
	 *            The Arrays with properties.
	 * @exception UnexpectedException
	 *                Is thrown when the number of elements is odd.
	 */
	public void setProperties(Object... props) {
		if (props.length % 2 != 0)
			throw new UnexpectedException("At least one property value is missing in arry of length " + props.length);
		for (int i = 0; i < props.length; i += 2) {
			this.setProperty(props[i], props[i + 1]);
		}
	}

	/**
	 * Adds a new properties by converting the key and value into Strings using
	 * the {@link Object#toString()} method.
	 * 
	 * @param key
	 *            Object representing the key.
	 * @param value
	 *            Object representing the value.
	 */
	public void setProperty(Object key, Object value) {
		super.setProperty(key.toString(), value.toString());
	}

	/**
	 * Adds a list of given properties to the properties.
	 * 
	 * @param properties
	 *            List of properties to add.
	 */
	public void setProperties(java.util.Properties properties) {
		for (String key : properties.stringPropertyNames())
			this.setProperty(key, properties.getProperty(key));
	}

	/**
	 * Returns the value of the property that corresponds to the given key.
	 * 
	 * @param key
	 *            The Object representing the key or null if no property can be
	 *            found.
	 * @return The Value of the key.
	 */
	@Override
	public String getProperty(Object key) {
		return super.getProperty(key.toString());
	}

	/**
	 * Checks if the property map contains the list of the given keys.
	 * 
	 * @param keys
	 *            List of keys to check.
	 * @return True if all keys are contained, otherwise false.
	 */
	@Override
	public boolean containsKeys(String... keys) {
		for (String key : keys)
			if (!this.containsKey(key))
				return false;
		return true;
	}

	/**
	 * Checks if the property map contains the list of the given keys. Therefore
	 * the objects in the list are converted to string using the
	 * {@link Object#toString()} method.
	 * 
	 * @param keys
	 *            The List of keys to check.
	 * @return True if all keys are contained, otherwise false.
	 */
	@Override
	public boolean containsKeys(Object... keys) {
		for (Object key : keys)
			if (!this.containsKey(key.toString()))
				return false;
		return true;
	}

	/**
	 * Adds all properties which's values are not equal to the optional flag.
	 * 
	 * @param properties
	 *            The properties to be added.
	 */
	public void setOptionalProperties(java.util.Properties properties) {
		for (String property : properties.stringPropertyNames()) {
			String value = properties.getProperty(property);
			LOGGER.debug("Parsing for optional properties, checking " + property + " with value " + value);
			if (value != null && !value.equals(OPTIONAL_FLAG))
				this.setProperty(property, value);
		}
	}
}
