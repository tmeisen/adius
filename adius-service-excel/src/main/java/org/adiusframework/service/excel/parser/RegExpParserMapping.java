package org.adiusframework.service.excel.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.adiusframework.service.excel.parser.cache.ParserResultCache;

public class RegExpParserMapping<T extends ParserResultCache> implements SheetParserMapping<T> {

	private SheetParser<T> parser;

	private Pattern pattern;

	public RegExpParserMapping() {
		// use setters
	}

	/**
	 * Convenient constructor to initialize the mapping without to call setters.
	 * 
	 * @param parser
	 *            the parser to use
	 * @param regExp
	 *            the regular expression to match the sheet name to the parser
	 */
	public RegExpParserMapping(SheetParser<T> parser, String regExp) {
		this();
		setParser(parser);
		setRegExp(regExp);
	}

	@Override
	public SheetParser<T> getParser() {
		return parser;
	}

	public void setParser(SheetParser<T> parser) {
		this.parser = parser;
	}

	protected Pattern getPattern() {
		return pattern;
	}

	public void setRegExp(String regExp) {
		this.pattern = Pattern.compile(regExp);
	}

	/* (non-Javadoc)
	 * @see org.adiusframework.service.excel.parser.SheetParserMapping#matches(java.lang.String)
	 */
	@Override
	public boolean matches(String input) {
		if (pattern == null) {
			return false;
		}
		Matcher matcher = pattern.matcher(input);
		return matcher.matches();
	}

}
