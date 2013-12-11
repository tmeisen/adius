package org.adiusframework.util.db;

import java.util.Properties;

import org.adiusframework.util.datastructures.DbConnectionConstants;
import org.adiusframework.util.datastructures.DbConnectionContainer;
import org.adiusframework.util.datastructures.MySqlConnectionContainer;

/**
 * Factory to create database connectors using properties.
 * 
 * @author tmeisen
 * 
 */
public class DatabaseConnectorFactory {

	private static final String MYSQL_DIALECT = "mySQL";

	/**
	 * Factory method to create an instance of the database connector.
	 * 
	 * @param properties
	 *            Properties containing database connection information
	 *            according to the PropertyModel.
	 * @return DatabaseConnector the initialized connector object.
	 */
	public static DbConnectionContainer create(Properties properties) {
		String dialect = properties.getProperty(DbConnectionConstants.DB_DIALECT);
		DbConnectionContainer connector;
		if (dialect != null && dialect.equals(MYSQL_DIALECT))
			connector = new MySqlConnectionContainer();
		else
			throw new UnsupportedOperationException("The specified dialect is not supported.");
		connector.setProperties(properties);
		return connector;
	}
}
