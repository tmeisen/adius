package org.adiusframework.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The StringUtil class provides several methods to deal easily with Strings.
 */
public class StringUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringUtil.class);

	/**
	 * This Pattern is used to extract the number out of a String, which has the
	 * structure {@literal <name>_<number>.<name>}.
	 */
	private static final Pattern NUMBER_SUFFIX_PATTERN = Pattern.compile("(.?[^\\d])*_(\\d+)\\.[^\\.]+$");

	/**
	 * This integer indicates which group, represented by it's number, must be
	 * extracted from a String with the structure of the pattern.
	 */
	private static final int GROUP_COUNT = 2;

	/**
	 * The default constructor is the only constructor and private, because this
	 * class should not be instantiated. It only contains static methods.
	 */
	private StringUtil() {
	}

	/**
	 * Joins all String in a given arrays and separates them with a special
	 * delimiter.
	 * 
	 * @param array
	 *            The arrays, containing the Strings.
	 * @param delimiter
	 *            The used delimiter.
	 * @return The result.
	 */
	public static String join(String[] array, String delimiter) {
		String join = "";
		for (int i = 0; i < array.length; i++) {
			join += array[i] + (i < array.length - 1 ? delimiter : "");
		}
		return join;
	}

	/**
	 * Swaps two elements in a given String-Array.
	 * 
	 * @param array
	 *            The given String-Array.
	 * @param i1
	 *            The index of the first element to be swapped.
	 * @param i2
	 *            The index of the second element to be swapped.
	 * @return The arrays itself, now with swapped elements.
	 */
	public static String[] swap(String[] array, int i1, int i2) {
		return ArrayUtil.swap(array, i1, i2);
	}

	/**
	 * Connects the String representing the given objects and separates them
	 * with a comma.
	 * 
	 * @param values
	 *            The objects which should be connected.
	 * @return The result String.
	 */
	public static String concat(Object... values) {
		String concat = "";
		int i = 0;
		for (Object value : values) {
			concat += value.toString();
			if (i < values.length - 1)
				concat += ",";
			i++;
		}
		return concat;
	}

	/**
	 * Determines the parent URI of a given URI by separating it from it's
	 * identifier.
	 * 
	 * @param uri
	 *            The given URI as a String.
	 * @param delimiter
	 *            The delimiter which splits the parent and the identifier.
	 * @return The parent URI as a String.
	 */
	public static String shortenUri(String uri, String delimiter) {
		int pos = uri.lastIndexOf(delimiter);
		if (pos < 0)
			return "";
		return uri.substring(0, pos);
	}

	/**
	 * Determines the identifier of a given URI by separating it from it's
	 * parent.
	 * 
	 * @param uri
	 *            The given URI as a String.
	 * @param delimiter
	 *            The delimiter which splits the parent and the identifier.
	 * @return The identifier as a String.
	 */
	public static String getIdentifierInUri(String uri, String delimiter) {
		int pos = uri.lastIndexOf(delimiter);
		if (pos < 0)
			return uri;
		return uri.substring(pos + 1);
	}

	/**
	 * Extracts the number out of a String, which has the pattern
	 * {@literal <name>_<number>.<name>}.
	 * 
	 * @param str
	 *            The string which contains the number.
	 * @param defValue
	 *            The default value if the String doesn't match the pattern.
	 * @return The extracted number or the default value if the string doesn't
	 *         match the pattern.
	 */
	public static int numberSuffix(String str, int defValue) {
		LOGGER.debug("Number suffix for " + str + " and default value " + defValue + " called.");
		int suffix = defValue;
		Matcher match = NUMBER_SUFFIX_PATTERN.matcher(str);
		if (match.find() && match.groupCount() == GROUP_COUNT) {
			try {
				suffix = Integer.valueOf(match.group(GROUP_COUNT));
			} catch (NumberFormatException e) {
				suffix = defValue;
			}
		}
		LOGGER.debug("Found suffix " + suffix);
		return suffix;
	}

	/**
	 * Determines if a given String is not null and not empty.
	 * 
	 * @param str
	 *            The String to be checked.
	 * @return True, if it isn't empty, false otherwise.
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 * Checks if both strings are equal (either both null or equal according to
	 * the equal function).
	 * 
	 * @param str1
	 *            first string to compare
	 * @param str2
	 *            second string to compare
	 * @return true if both strings are null or equal, otherwise false
	 */
	public static boolean equals(String str1, String str2) {
		if (str1 == null && str2 == null) {
			return true;
		} else if (str1 != null) {
			return str1.equals(str2);
		} else {
			return false;
		}
	}
}
