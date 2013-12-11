package org.adiusframework.util;

/**
 * The MultiLineExcluder enables the possibility to exclude all lines, that are
 * between a start- line and a end-line whether this range contains another
 * line.
 */
public class MultiLineExcluder implements LineChanger {
	/** Stores the pattern of the line that indicates the start of the range. */
	private String startPattern;

	/**
	 * The pattern of the line that must be included in the range in order to
	 * exclude the lines.
	 */
	private String containingPattern;

	/** Stores the pattern of the line that indicates the end of the range. */
	private String endPattern;

	/** Stores the lines of the range. */
	private String line;

	/** Indicates if the range should be excluded. */
	private boolean exclude = false;

	/** Indicates if the current status is "in a determined range". */
	private boolean inPassage = false;

	/**
	 * Creates a new MultiLineExclider with the following patterns.
	 * 
	 * @param startPattern
	 *            The pattern for the start-line.
	 * @param containingPattern
	 *            The pattern for the line that must be included in the range.
	 * @param endPattern
	 *            The pattern of the end-line.
	 */
	public MultiLineExcluder(String startPattern, String containingPattern, String endPattern) {
		this.startPattern = startPattern;
		this.containingPattern = containingPattern;
		this.endPattern = endPattern;
	}

	@Override
	public String changeTo(String line) {
		if (!inPassage) {
			if (line.matches(startPattern)) {
				this.line = line + "\n";
				inPassage = true;
				return null;
			}

			return line;
		} else if (line.matches(containingPattern)) {
			exclude = true;
			return null;
		} else if (line.matches(endPattern)) {
			inPassage = false;

			if (exclude) {
				exclude = false;
				return "";
			} else {
				return this.line + line;
			}
		} else {
			this.line += line + "\n";
			return null;
		}
	}
}
