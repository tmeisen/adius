package org.adiusframework.service.excel.parser;

import org.adiusframework.service.excel.exception.ExcelParseException;
import org.adiusframework.service.excel.parser.cache.ParserResultCache;
import org.apache.poi.ss.usermodel.Sheet;

public interface SheetParser<T extends ParserResultCache> {

	public void parse(Sheet sheet, T cache) throws ExcelParseException;

}
