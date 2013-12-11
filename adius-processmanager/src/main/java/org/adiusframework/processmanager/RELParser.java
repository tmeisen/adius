package org.adiusframework.processmanager;

/**
 * The RELParser interface provides a method to parse a REL-Expression to the
 * name of the referenced resource. The format of a REL-String is ${
 * {@literal <prefix>.<name>}
 */
public interface RELParser {

	/**
	 * Parses the given REL (resource expression language) expression and
	 * returns the name of the resource that is referenced.
	 * 
	 * @param relExpr
	 *            REL expression that have to be parsed
	 * @return null if no REL expression has been given or the extracted
	 *         resource name
	 */
	public abstract String parse(String relExpr);

}
