package org.adiusframework.util.db;

public interface BulkLoader {

	public abstract String getAbsolutePath(boolean escape);

	public abstract String getFieldTerminator();

	public abstract String getLineTerminator();

	public abstract String getTable();

	public abstract String getColumns();

	public abstract void open();

	public abstract void delete();

	public abstract void close();

	public abstract boolean isOpened();

	public abstract void setRowCountControlOn(int expectedRowCount);

	public abstract void setRowCountControlOff();

	public abstract void increaseExpectedRowCount(int incValue);

	public abstract boolean isRowCountControlOn();

	public abstract int getExpectedRowCount();

}