package org.adiusframework.util;

/**
 * The SystemUtil class provides methods to get information about the current
 * system.
 */
public class SystemUtil {

	/**
	 * The default constructor is the only constructor and private, because this
	 * class should not be instantiated. It only contains static methods.
	 */
	private SystemUtil() {
	}

	/**
	 * Determines a String which contains the main properties of the current
	 * system.
	 * 
	 * @return The created String.
	 */
	public static String getSystemDescription() {
		StringBuffer buffer = new StringBuffer(100);
		buffer.append("Java Version: ");
		buffer.append(System.getProperty("java.version"));
		buffer.append(" at ");
		buffer.append(System.getProperty("java.home"));
		buffer.append("\n");
		buffer.append("OS: ");
		buffer.append(System.getProperty("os.name"));
		buffer.append(" (");
		buffer.append(System.getProperty("os.version"));
		buffer.append("/");
		buffer.append(System.getProperty("os.arch"));
		buffer.append(")");
		return buffer.toString();
	}
}
