package org.adiusframework.service.excel.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.adiusframework.service.excel.exception.ExcelParseException;
import org.adiusframework.service.excel.parser.cache.ParserResultCache;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link OrderedWorkbookParser} is an abstract implementation of the
 * {@link AbstractWorkbookParser}. The order in which the sheets are parsed is
 * defined by the value of the sheet order field.
 * 
 * @author tm807416
 * 
 */
public abstract class OrderedWorkbookParser<T extends ParserResultCache> extends AbstractWorkbookParser<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderedWorkbookParser.class);

	private List<String> sheetOrder;

	public OrderedWorkbookParser() {
		super();
		setSheetOrder(new ArrayList<String>());
	}

	public OrderedWorkbookParser(String... sheetOrder) {
		this();
		setSheetOrder(Arrays.asList(sheetOrder));
	}

	public OrderedWorkbookParser(List<String> sheetOrder, SheetParser<T> defaultParser) {
		super(defaultParser);
		setSheetOrder(sheetOrder);
	}

	public List<String> getSheetOrder() {
		return sheetOrder;
	}

	public void setSheetOrder(List<String> sheetOrder) {
		this.sheetOrder = sheetOrder;
	}

	@Override
	public void parseSheets(Workbook workbook, T result) throws ExcelParseException {
		for (String sheetName : getSheetOrder()) {
			int sheetIndex = workbook.getSheetIndex(sheetName);
			Sheet sheet = null;
			if (sheetIndex > -1) {
				sheet = workbook.getSheetAt(sheetIndex);
			}
			if (sheet != null) {
				parseSheet(sheet, result);
			} else {
				LOGGER.debug("Skipping sheet " + sheetName + " no matching sheet found");
			}
		}
	}

}
