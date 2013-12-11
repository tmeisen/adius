package org.adiusframework.ontology;

/**
 * Persistence information of an object propery.
 * 
 * @author Alexander Tenbrock
 */
public class PropertyPersistenceInfo {
	private String dbForeignKey, dbForeignKeyInverse;

	/**
	 * Constructor
	 * 
	 * @param dbForeignKey
	 *            name of the column that acts as the foreign key
	 * @param dbForeignKeyInverse
	 *            name of the column that acts as the foreign key of the target
	 *            object
	 */
	public PropertyPersistenceInfo(String dbForeignKey, String dbForeignKeyInverse) {
		this.dbForeignKey = dbForeignKey;
		this.dbForeignKeyInverse = dbForeignKeyInverse;
	}

	public String getDbForeignKey() {
		return this.dbForeignKey;
	}

	public String getDbForeignKeyInverse() {
		return this.dbForeignKeyInverse;
	}
}
