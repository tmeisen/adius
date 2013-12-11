package org.adiusframework.service.excel.parser;

import org.adiusframework.service.excel.parser.cache.ParserResultCache;

public class StringParserMapping<T extends ParserResultCache> implements SheetParserMapping<T> {

	private SheetParser<T> parser;

	private String match;

	public StringParserMapping() {
		// use setters
	}

	/**
	 * Convenient constructor to initialize the mapping without to call setters.
	 * 
	 * @param parser
	 *            the parser to use
	 * @param match
	 *            the string representing the required match
	 */
	public StringParserMapping(SheetParser<T> parser, String match) {
		this();
		setParser(parser);
		setMatch(match);
	}

	@Override
	public SheetParser<T> getParser() {
		return parser;
	}

	public void setParser(SheetParser<T> parser) {
		this.parser = parser;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	/**
	 * Checks if the input matches the match, if so true is returned.
	 * 
	 * @param input
	 *            the input to check
	 * @return true if the input is equal to the match, otherwise false
	 */
	@Override
	public boolean matches(String input) {
		if (match == null) {
			return false;
		}
		return match.equals(input);
	}

}
