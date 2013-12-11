package org.adiusframework.util;

/**
 * The PatternBasedChanger changes a line to a replacement if it matches a
 * specific pattern.
 */
public class PatternBasedChanger implements LineChanger {
	private String pattern;
	private String replacement;

	/**
	 * Creates a new PatternBasedChanger with the given argument.
	 * 
	 * @param pattern
	 *            The pattern that is used to identify lines to change.
	 * @param replacement
	 *            The replacement that is used instead of changed lines.
	 */
	public PatternBasedChanger(String pattern, String replacement) {
		this.pattern = pattern;
		this.replacement = replacement;
	}

	@Override
	public String changeTo(String line) {
		if (line.matches(pattern)) {
			return replacement;
		}

		return line;
	}
}
