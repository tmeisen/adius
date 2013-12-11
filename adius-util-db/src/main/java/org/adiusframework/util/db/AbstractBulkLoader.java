package org.adiusframework.util.db;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.MissingResourceException;

import org.adiusframework.util.exception.IORuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractBulkLoader implements BulkLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBulkLoader.class);

	private static final String FILEEXTENSION = ".blf", FILEPREFIX = "dbl";

	private static final String DEFAULT_FIELDTERMINATOR = "|", DEFAULT_LINETERMINATOR = "\n";

	protected static final String COLUMN_SEPARATOR = ",";

	private Boolean isOpened;

	private String fieldTerminator, lineTerminator, table, columns;

	private Integer columnCount;

	private File file;

	private FileWriter out;

	private int expectedRowCount;

	private boolean rowCountControl;

	protected AbstractBulkLoader() {
		this(null, null, DEFAULT_FIELDTERMINATOR, DEFAULT_LINETERMINATOR, false);
	}

	public AbstractBulkLoader(String table, String columns, Boolean open) {
		this(table, columns, DEFAULT_FIELDTERMINATOR, DEFAULT_LINETERMINATOR, open);
	}

	public AbstractBulkLoader(String table, String columns, String fieldTerminator, String lineTerminator) {
		this.table = table;
		this.setColumns(columns);
		this.fieldTerminator = fieldTerminator;
		this.lineTerminator = lineTerminator;
		this.isOpened = false;
		this.setRowCountControlOff();
	}

	public AbstractBulkLoader(String table, String columns, String fieldTerminator, String lineTerminator, Boolean open) {
		this(table, columns, fieldTerminator, lineTerminator);
		if (open)
			this.open();
	}

	@Override
	public String getAbsolutePath(boolean escape) {
		if (escape)
			return this.file.getAbsolutePath().replace("\\", "\\\\");
		return this.file.getAbsolutePath();
	}

	@Override
	public String getFieldTerminator() {
		return this.fieldTerminator;
	}

	@Override
	public String getLineTerminator() {
		return this.lineTerminator;
	}

	@Override
	public String getTable() {
		return this.table;
	}

	protected void setTable(String table) {
		this.table = table;
	}

	@Override
	public String getColumns() {
		return this.columns;
	}

	protected void setColumns(String columns) {
		this.columns = columns;
		this.columnCount = (this.columns == null ? 0 : this.columns.split(COLUMN_SEPARATOR).length);
	}

	protected void setColumns(List<String> columns) {
		this.columns = "";
		for (int i = 0; i < columns.size(); i++)
			this.columns += (columns.get(i) + (i < columns.size() - 1 ? COLUMN_SEPARATOR : ""));
		this.columnCount = columns.size();
	}

	protected int getColumnCount() {
		return this.columnCount;
	}

	@Override
	public void open() {
		if (!this.isOpened) {

			// create a new file
			try {
				this.file = File.createTempFile(AbstractBulkLoader.FILEPREFIX, AbstractBulkLoader.FILEEXTENSION);
				this.file.deleteOnExit();
				LOGGER.debug("Created temporary file: " + this.getAbsolutePath(false));

				// open output stream and set append
				this.out = new FileWriter(this.file, true);
				LOGGER.debug("Created outputstream for file: " + this.file.getName());
				this.isOpened = true;
			} catch (IOException e) {

				// normally this exception should not occur, hence a runtime
				// exception is thrown instead;
				LOGGER.error("An error occured during bulk loader creation: " + e.getMessage());
				throw new MissingResourceException("The bulk loader ressource could not be created, because of "
						+ e.getMessage(), SimpleBulkLoader.class.getName(), this.getTable());
			}
		}
	}

	@Override
	public void delete() {
		if (!this.file.delete())
			LOGGER.info("The file " + this.getAbsolutePath(false) + " has not been deleted.");
		else
			LOGGER.debug("File " + this.file.getName() + " successfully deleted.");
	}

	@Override
	public void close() {

		// close output stream
		try {
			this.out.close();
			this.isOpened = false;
			LOGGER.debug("Closed outputstream for file: " + this.file.getName());
		} catch (IOException e) {

			// normally this exception should not occur, hence a runtime
			// exception is thrown instead;
			LOGGER.error("An error occured during bulk loader closing: " + e.getMessage());
			throw new MissingResourceException("The bulk loader ressource could not be created, because of "
					+ e.getMessage(), SimpleBulkLoader.class.getName(), this.getTable());
		}
	}

	protected void writeData(Object... values) {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (Object obj : values) {
			i++;
			if (obj != null)
				buf.append(obj.toString());
			buf.append((i % this.columnCount != 0) ? this.fieldTerminator : (this.lineTerminator));
		}
		this.writeData(buf);
	}

	protected void writeData(StringBuffer buffer) {
		this.writeData(buffer.toString());
	}

	protected void writeData(String value) {
		this.writeData(value, false);
	}

	@Override
	public boolean isOpened() {
		return this.isOpened;
	}

	protected void writeData(String value, boolean separator) {
		if (!this.isOpened)
			return;
		try {
			this.out.write(value);
			if (separator)
				this.out.write(this.fieldTerminator);
		} catch (IOException e) {
			LOGGER.error("Error writing data to bulk loader file " + this.file.getAbsolutePath());
			throw new IORuntimeException(e);
		}
	}

	@Override
	public void setRowCountControlOn(int expectedRowCount) {
		this.rowCountControl = true;
		this.expectedRowCount = expectedRowCount;
	}

	@Override
	public void setRowCountControlOff() {
		this.rowCountControl = false;
		this.expectedRowCount = 0;
	}

	@Override
	public void increaseExpectedRowCount(int incValue) {
		if (this.isRowCountControlOn())
			this.expectedRowCount += incValue;
		else
			throw new UnsupportedOperationException(
					"Increasing the row count is only possible, if row count control is on.");
	}

	@Override
	public boolean isRowCountControlOn() {
		return this.rowCountControl;
	}

	@Override
	public int getExpectedRowCount() {
		return this.expectedRowCount;
	}
}
