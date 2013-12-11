package org.adiusframework.service.excel.parser;

import org.adiusframework.service.excel.parser.cache.ParserResultCache;

public interface SheetParserMapping<T extends ParserResultCache> {

	/**
	 * Checks if the input is a match according to the implemented matching
	 * function.
	 * 
	 * @param input
	 *            the input to check
	 * @return true if the input matches, otherwise false
	 */
	public abstract boolean matches(String input);

	/**
	 * @return the parser mapped by the mapping
	 */
	public abstract SheetParser<T> getParser();

}