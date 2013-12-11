package org.adiusframework.util;

/**
 * The LineChanger interface defines a class that can change a line that belongs
 * to a javadoc-html-file.
 */
public interface LineChanger {

	/**
	 * Determines if and to what a given line should be changed.
	 * 
	 * @param line
	 *            The lien to be checked.
	 * @return The line itself if it should not be changed, the replacement
	 *         otherwise (null means that the line shouldn't be considered).
	 */
	public abstract String changeTo(String line);
}
