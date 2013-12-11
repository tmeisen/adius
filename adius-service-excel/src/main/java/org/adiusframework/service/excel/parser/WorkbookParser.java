package org.adiusframework.service.excel.parser;

import org.adiusframework.service.excel.exception.ExcelParseException;
import org.adiusframework.service.excel.parser.cache.ParserResultCache;
import org.apache.poi.ss.usermodel.Workbook;

public interface WorkbookParser<T extends ParserResultCache> {

	public ParserResultCache parse(Workbook workbook) throws ExcelParseException;

}
