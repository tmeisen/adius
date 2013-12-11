package org.adiusframework.service.excel;

import java.io.File;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.Date;

import org.adiusframework.util.ArrayUtil;
import org.adiusframework.util.file.FileUtil;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelUtil.class);

	private static final String[] EXCEL97_EXTENSIONS = { "xls" };

	private static final String[] EXCEL2007_EXTENSIONS = { "xlsx", "xlsm" };

	public static boolean isExcelFileFormat(File file) {
		return getExcelVersion(file) != null;
	}

	public static boolean isExcelFileFormat(String name) {
		return getExcelVersion(name) != null;
	}

	public static SpreadsheetVersion getExcelVersion(File file) {
		String extension = FileUtil.getFileExtension(file);
		return getExcelVersionByExtension(extension);
	}

	public static SpreadsheetVersion getExcelVersion(String name) {
		String extension = FileUtil.getFileExtension(name);
		return getExcelVersionByExtension(extension);
	}

	public static SpreadsheetVersion getExcelVersionByExtension(String extension) {
		if (ArrayUtil.contains(EXCEL97_EXTENSIONS, extension))
			return SpreadsheetVersion.EXCEL97;
		else if (ArrayUtil.contains(EXCEL2007_EXTENSIONS, extension))
			return SpreadsheetVersion.EXCEL2007;
		return null;
	}

	public static <T> T getCellValue(Sheet sheet, CellReference ref, Class<T> type, boolean evaluateFormula) {
		return getCellValue(sheet.getRow(ref.getRow()).getCell(ref.getCol()), type, evaluateFormula);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getCellValue(Cell cell, Class<T> type, boolean evaluateFormula) {

		// if evaluation of formulas is set, we have to create a evaluator
		if (evaluateFormula) {
			FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
			CellValue cellValue = evaluator.evaluate(cell);
			return getCellValue(cellValue, type);
		}

		// otherwise we do it the hard way
		switch (cell.getCellType()) {

		case Cell.CELL_TYPE_BOOLEAN:
			return (T) Boolean.valueOf(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_NUMERIC:

			// we have to check if a date type is required, if so we have to
			// transform the numeric data correctly
			if (type.equals(Date.class))
				return (T) DateUtil.getJavaDate(cell.getNumericCellValue());
			return convertDoubleValue(Double.valueOf(cell.getNumericCellValue()), type);
		case Cell.CELL_TYPE_STRING:
			if (type.isAssignableFrom(String.class))
				return (T) String.valueOf(cell.getStringCellValue());
			else
				return convertStringCellValue(cell.getStringCellValue(), type);
		case Cell.CELL_TYPE_BLANK:
			return (T) "";
		default:
			return null;
		}
	}

	public static <T> T convertStringCellValue(String value, Class<T> type) {
		try {
			Constructor<T> c = type.getConstructor(String.class);
			return c.newInstance(value);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getCellValue(CellValue cellValue, Class<T> type) {
		if (cellValue == null)
			return null;

		switch (cellValue.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			return (T) Boolean.valueOf(cellValue.getBooleanValue());
		case Cell.CELL_TYPE_NUMERIC:
			return convertDoubleValue(Double.valueOf(cellValue.getNumberValue()), type);
		case Cell.CELL_TYPE_STRING:
			return (T) String.valueOf(cellValue.getStringValue());
		case Cell.CELL_TYPE_BLANK:
			return (T) "";
		default:
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	protected static <T> T convertDoubleValue(Double value, Class<T> type) {
		if (Integer.class.equals(type))
			return (T) Integer.valueOf(value.intValue());
		else if (String.class.equals(type))
			return (T) new DecimalFormat("0").format(value);
		else if (Boolean.class.equals(type))
			return (T) Boolean.valueOf(value > 0 || value < 0);
		return (T) value;
	}

	public static int findRowByContent(Sheet sheet, String regExp, int cellCount) {
		return findRowByContent(sheet, regExp, 0, cellCount);
	}

	public static int findRowByContent(Sheet sheet, String regExp, int cellStart, int cellCount) {
		boolean match = false;
		int rowIndex = 0;
		for (; rowIndex < sheet.getPhysicalNumberOfRows() && !match; rowIndex++) {

			// lets get the row and check if some data is present
			Row row = sheet.getRow(rowIndex);
			if (row == null)
				continue;

			// now we have to check the cells
			int cellIndex = findCellByContent(row, regExp, cellStart, cellCount);
			if (cellIndex > -1)
				match = true;
		}
		if (!match)
			return -1;
		return rowIndex - 1;
	}

	public static int findCellByContent(Row row, String regExp, int cellStart, int cellCount) {
		for (int cellIndex = cellStart; cellIndex < cellCount; cellIndex++) {
			Cell cell = row.getCell(cellIndex);
			if (cell == null)
				continue;
			String value = getCellValue(cell, String.class, true);
			if (value != null && value.matches(regExp))
				return cellIndex;
		}
		return -1;
	}

	public static int findRowStartingWith(Sheet sheet, String[] values) {
		return findRowStartingWith(sheet, values, 0);
	}

	public static int findRowStartingWith(Sheet sheet, String[] values, int offset) {

		// lets start with the row set by the offset parameter
		boolean match = false;
		int startRow = offset;
		for (; startRow < sheet.getPhysicalNumberOfRows() && !match; startRow++) {

			// check if a row exists, if not we can continue with the next row
			Row row = sheet.getRow(startRow);
			if (row == null)
				continue;

			// check if a cell exists at 0, if not we can continue with the next
			// row
			Cell cell = row.getCell(0);
			if (cell == null) {
				continue;
			}

			// finally we can read the cell value
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				String cellValue = cell.getStringCellValue();
				if (ArrayUtil.contains(values, cellValue)) {
					match = true;
				}
			}
		}

		// if we reached the end of the sheet we return -1
		if (startRow == sheet.getPhysicalNumberOfRows())
			return -1;
		return startRow - 1;
	}

	public static <T> boolean isCellContentEquals(Sheet sheet, int row, int col, T content, Class<T> type) {
		return isCellContentEquals(sheet, new CellReference(row, col), content, type);
	}

	public static <T> boolean isCellContentEquals(Sheet sheet, CellReference reference, T content, Class<T> type) {
		final Row row = sheet.getRow(reference.getRow());
		if (row == null) {
			return false;
		} else {
			return isCellContentEquals(row.getCell(reference.getCol()), content, type);
		}
	}

	public static <T> boolean isCellContentEquals(Cell cell, T content, Class<T> type) {
		if (cell == null || content == null || type == null) {
			throw new IllegalArgumentException("Arguments of isCellContentEquals cannot be null");
		}
		T value = ExcelUtil.getCellValue(cell, type, true);
		return value.equals(content);
	}

	public static boolean isEmptyCell(Cell cell) {
		return cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK
				|| ExcelUtil.getCellValue(cell, String.class, true).isEmpty();
	}

	public static boolean isRangeEmpty(Row row, int startIndex, int endIndex) {
		for (int index = startIndex; index <= endIndex; index++) {
			final Cell cell = row.getCell(index);
			if (cell != null && !isEmptyCell(cell)) {
				return false;
			}
		}
		return true;
	}

}
