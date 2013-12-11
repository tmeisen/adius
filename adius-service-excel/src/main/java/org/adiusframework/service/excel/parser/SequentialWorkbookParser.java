package org.adiusframework.service.excel.parser;

import org.adiusframework.service.excel.exception.ExcelParseException;
import org.adiusframework.service.excel.parser.cache.ParserResultCache;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * {@link SequentialWorkbookParser} is an abstract implementation of the
 * {@link AbstractWorkbookParser}. The order of the parsing is derived from the
 * sequential order of the sheets as they are ordered in the {@link Workbook}.
 * 
 * @author tm807416
 * 
 */
public abstract class SequentialWorkbookParser<T extends ParserResultCache> extends AbstractWorkbookParser<T> {

	/**
	 * Default constructor no further initialization by setters required
	 */
	public SequentialWorkbookParser() {
		super();
	}

	/**
	 * Extended constructor enables the setting of a default parser
	 * 
	 * @param defaultParser
	 *            the default parser to use
	 */
	public SequentialWorkbookParser(SheetParser<T> defaultParser) {
		super(defaultParser);
	}

	/**
	 * @param workbook
	 *            the workbook that have to be parsed
	 * @param result
	 *            the result cache enriched by the additional elements generated
	 *            or updated due to the parsing
	 * @throws ExcelParseException
	 *             thrown if the parser fails, because of syntax or semantic
	 *             mismatches
	 */
	@Override
	public void parseSheets(Workbook workbook, T result) throws ExcelParseException {
		for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
			Sheet sheet = workbook.getSheetAt(index);
			parseSheet(sheet, result);
		}
	}

}
