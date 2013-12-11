package org.adiusframework.service.excel.parser;

import java.util.ArrayList;
import java.util.Collection;

import org.adiusframework.service.excel.exception.ExcelParseException;
import org.adiusframework.service.excel.parser.cache.ParserResultCache;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AbstractWorkbookParser} is an abstract implementation of the
 * {@link WorkbookParser} interface that enables the parsing of a workbook by
 * defining a mapping of parsers to sheets. The mapping is resolved by the name
 * of the sheet. Therefore a regular expression has to be set, so that if the
 * regular expression matches, the parser assigned to the expression is
 * executed. An abstract parsing process is defined that is divided into three
 * parts. A pre-processing, a processing of the single sheets and a
 * post-processing. The concrete process of the sheet parsing (e.g. the order)
 * has to be defined by the upper class. The class provides the necessary base
 * structures to hold relevant data as well as the
 * {@link #parseSheet(Sheet, ParserResultCache)} method, to find the parser of a
 * sheet and process it.
 * 
 * @author tm807416
 * 
 */
public abstract class AbstractWorkbookParser<T extends ParserResultCache> implements WorkbookParser<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWorkbookParser.class);

	private Collection<SheetParserMapping<T>> parserMappings;

	private SheetParser<T> defaultParser;

	public AbstractWorkbookParser() {
		parserMappings = new ArrayList<SheetParserMapping<T>>();
	}

	public AbstractWorkbookParser(SheetParser<T> defaultParser) {
		this();
		setDefaultParser(defaultParser);
	}

	public SheetParser<T> getDefaultParser() {
		return defaultParser;
	}

	public void setDefaultParser(SheetParser<T> defaultParser) {
		this.defaultParser = defaultParser;
	}

	protected Collection<SheetParserMapping<T>> getParserMappings() {
		return parserMappings;
	}

	public void addParserMapping(SheetParserMapping<T> parserMapping) {
		getParserMappings().add(parserMapping);
	}

	@Override
	public ParserResultCache parse(Workbook workbook) throws ExcelParseException {
		T result = createResultCache();
		preprocess(workbook, result);
		parseSheets(workbook, result);
		postprocess(workbook, result);
		return result;
	}

	protected void preprocess(Workbook workbook, T result) throws ExcelParseException {
		// Method stub can be overwritten if required
	}

	protected void postprocess(Workbook workbook, T result) throws ExcelParseException {
		// Method stub can be overwritten if required
	}

	protected void parseSheet(Sheet sheet, T result) throws ExcelParseException {
		SheetParser<T> parser = null;
		LOGGER.debug("Lookup parser for sheet: " + sheet.getSheetName());
		for (SheetParserMapping<T> mapping : parserMappings) {
			if (mapping.matches(sheet.getSheetName())) {
				LOGGER.debug("Registered parser for sheet " + sheet.getSheetName() + " identified");
				parser = mapping.getParser();
				break;
			}
		}
		if (parser == null) {
			LOGGER.debug("Using default parser for sheet (if registered)");
			parser = getDefaultParser();
		}
		if (parser != null) {
			parser.parse(sheet, result);
		} else {
			LOGGER.debug("Skipping sheet " + sheet.getSheetName() + " no parser registered");
		}
	}

	protected abstract void parseSheets(Workbook workbook, T result) throws ExcelParseException;

	protected abstract T createResultCache();

}
