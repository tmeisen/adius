package org.adiusframework.processmanager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The RegExpRELParser uses Regular Expression to identify valid REL-Expression
 * and extract the {@literal <name>} part. A the moment the prefix "resource" is
 * the only one supported.
 * 
 * @see RELParser
 */
public class RegExpRELParser implements RELParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegExpRELParser.class);

	private static final String RESOURCEPREFIX = "resource.";

	private Pattern PATTERN = Pattern.compile("\\$\\{([A-Za-z0-9._]+)\\}");

	@Override
	public String parse(String relExpr) {
		Matcher matcher = PATTERN.matcher(relExpr);
		LOGGER.debug("Testing " + relExpr + " for REL expression");
		if (matcher.matches() && matcher.groupCount() > 0) {
			String match = matcher.group(1);
			LOGGER.debug("Match found " + match);
			if (match.startsWith(RESOURCEPREFIX)) {
				match = match.substring(RESOURCEPREFIX.length());
				LOGGER.debug("Match found " + match);
				return match;
			} else
				throw new UnsupportedOperationException("Prefix is not supported in REL");
		}
		return null;
	}

}
