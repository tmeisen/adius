package org.adiusframework.util.datastructures;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PropertyCliParser defines a method to create a Properties object based on
 * an Arrays of Strings where every String represents the pair of a key and a
 * value.
 */
public class PropertyCliParser {

	/**
	 * This pattern describes the structure of strings which contains a pair of
	 * key and value.
	 */
	private static final Pattern FIND_PROPERTY = Pattern.compile("-([A-Za-z]*[^:\\s]*)\\s*:\\s*\"?(.+[^\"])\"?$");

	/**
	 * When a String matches the {@link #FIND_PROPERTY} pattern it should have
	 * two groups.
	 */
	private static final int GROUP_COUNT = 2;

	/**
	 * The key is the first group.
	 */
	private static final int GROUP_KEY_INDEX = 1;

	/**
	 * The value is the second group.
	 */
	private static final int GROUP_VALUE_INDEX = 2;

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertyCliParser.class);

	/**
	 * The default constructor is the only constructor and private, because this
	 * class should not be instantiated. It only contains static methods.
	 */
	private PropertyCliParser() {
	}

	/**
	 * Parses an Arrays of String to a Properties object. If all Strings match
	 * the structure for a key and value pair, one property will be added for
	 * every element in the Array.
	 * 
	 * @param arguments
	 *            The Array which contains a String for every property.
	 * @return The generated Properties object.
	 */
	public static Properties parse(String... arguments) {

		// create new property map
		Properties properties = new Properties();

		// check each argument
		for (String s : arguments) {
			LOGGER.debug("Checking " + s);
			Matcher match = FIND_PROPERTY.matcher(s);
			while (match.find()) {
				LOGGER.debug("Found " + match.groupCount() + " groups");
				if (match.groupCount() == GROUP_COUNT)
					properties.setProperty(match.group(GROUP_KEY_INDEX), match.group(GROUP_VALUE_INDEX));
			}
		}
		return properties;
	}
}
