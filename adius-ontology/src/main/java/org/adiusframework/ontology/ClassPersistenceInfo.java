package org.adiusframework.ontology;

/**
 * Persistence information for an OWL class.
 * 
 * @author Alex
 */
public class ClassPersistenceInfo {
	/**
	 * The annotation value for "dbTable", "dbIDColumn" and "dbNameColumn".
	 */
	private String dbTable, dbIDColumn, dbNameColumn;

	/**
	 * Constructor.
	 * 
	 * @param dbTable
	 *            name of the database table
	 * @param dbIDColumn
	 *            name of the primary id column
	 * @param dbNameColumn
	 *            name of the column with the human readable name
	 */
	public ClassPersistenceInfo(String dbTable, String dbIDColumn, String dbNameColumn) {
		this.dbTable = dbTable;
		this.dbIDColumn = dbIDColumn;
		this.dbNameColumn = dbNameColumn;
	}

	/**
	 * Returns the table that instances of this class expressions are located
	 * in.
	 * 
	 * @return table name
	 */
	public String getDbTable() {
		return this.dbTable;
	}

	/**
	 * Returns the column that acts as the primary key.
	 * 
	 * @return name of the primary key column
	 */
	public String getDbIDColumn() {
		return this.dbIDColumn;
	}

	/**
	 * Returns the column that contains a human readable name of every entry.
	 * May be null.
	 * 
	 * @return column of the human readable name
	 */
	public String getDbNameColumn() {
		return this.dbNameColumn;
	}
}
