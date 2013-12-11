package org.adiusframework.util.datastructures;

/**
 * Defines constant names of properties which are used to connect to databases.
 */
public class DbConnectionConstants {

	/** The host, identified by its IP, the database is running. */
	public static final String DB_IP = "database.ip";

	/** The port, identified by its IP, the database is running. */
	public static final String DB_PORT = "database.port";

	/** The schema which is used for this connection. */
	public static final String DB_SCHEMA = "database.schema";

	/** The user-name which is used for the connection. */
	public static final String DB_USER = "database.user";

	/** The password which is needed to login. */
	public static final String DB_PASSWORD = "database.password";

	/** The dialect which is used for this connection. */
	public static final String DB_DIALECT = "database.dialect";
}
