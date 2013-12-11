package org.adiusframework.util.datastructures;

import java.io.Serializable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DbConnectionContainer is an abstract class which gives base functionality
 * to connect to a Hibernate database.
 */
public abstract class DbConnectionContainer implements Serializable {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 929590229203441455L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DbConnectionContainer.class);

	/** The user-name which is used to connect to Hibernate. */
	protected final String HIBERNATE_CONNECTION_USERNAME = "hibernate.connection.username";

	/** The password which is required to login as the selected user. */
	protected final String HIBERNATE_CONNECTION_PASSWORD = "hibernate.connection.password";

	/** The class which stores the drivers for the connection. */
	protected final String HIBERNATE_CONNECTION_DRIVER_CLASS = "hibernate.connection.driver_class";

	/** The dialect which is used for the connection. */
	protected final String HIBERNATE_CONNECTION_DIALECT = "hibernate.connection.dialect";

	/** The URL which identifies the database. */
	protected final String HIBERNATE_CONNECTION_URL = "hibernate.connection.url";

	/**
	 * This object stores the user-name which is used to login at the database.
	 */
	private String user;

	/**
	 * This object stores the password which is used for logging in.
	 */
	private String password;

	/**
	 * This schema is used for this specific connection.
	 */
	private String schema;

	/**
	 * Create a DbConnectionContainer without any parameters.
	 */
	public DbConnectionContainer() {
	}

	/**
	 * Create a DbConnectionContainer and configures it with given parameters.
	 * 
	 * @param schema
	 *            The schema to configure.
	 * @param user
	 *            The user-name to configure.
	 * @param password
	 *            The password to configure.
	 */
	public DbConnectionContainer(String schema, String user, String password) {
		this.setSchema(schema);
		this.setUser(user);
		this.setPassword(password);
	}

	/**
	 * Return the user-name which is currently used.
	 * 
	 * @return The current user-name.
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * Sets a new user-name.
	 * 
	 * @param user
	 *            The new user-name.
	 */
	public void setUser(String user) {
		LOGGER.debug("Setting user to: " + user);
		this.user = user;
	}

	/**
	 * Returns the password which is currently used.
	 * 
	 * @return The current password.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sets a new password.
	 * 
	 * @param password
	 *            The new password.
	 */
	public void setPassword(String password) {
		LOGGER.debug("Setting password to: " + password);
		this.password = password;
	}

	/**
	 * Returns the schema which is currently used.
	 * 
	 * @return The current schema.
	 */
	public String getSchema() {
		return this.schema;
	}

	/**
	 * Sets a new schema.
	 * 
	 * @param schema
	 *            The new schema.
	 */
	public void setSchema(String schema) {
		LOGGER.debug("Setting schema to: " + schema);
		this.schema = schema;
	}

	/**
	 * Return a Properties object which contains all properties that are needed
	 * for this Hibernate connection.
	 * 
	 * @return The Properties of this connection.
	 */
	public Properties getHibernateConnectionProperties() {
		Properties properties = new Properties();
		properties.setProperty(this.HIBERNATE_CONNECTION_USERNAME, this.getUser());
		properties.setProperty(this.HIBERNATE_CONNECTION_PASSWORD, this.getPassword());
		properties.setProperty(this.HIBERNATE_CONNECTION_DRIVER_CLASS, this.getDriverClass());
		properties.setProperty(this.HIBERNATE_CONNECTION_DIALECT, this.getDialect());
		properties.setProperty(this.HIBERNATE_CONNECTION_URL, this.getUrl());
		return properties;
	}

	/**
	 * Sets new Properties for this connection. Currently only the user-name and
	 * the password are considered.
	 * 
	 * @param properties
	 *            The new properties.
	 * @see DbConnectionConstants
	 */
	public void setProperties(Properties properties) {
		String user = properties.getProperty(DbConnectionConstants.DB_USER);
		if (user != null)
			this.setUser(user);
		String pwd = properties.getProperty(DbConnectionConstants.DB_PASSWORD);
		if (pwd != null)
			this.setPassword(pwd);
	}

	@Override
	/**
	 * Return the URL of this connection as a String-representative.
	 */
	public String toString() {
		return this.getUrl();
	}

	/**
	 * Returns the class which stores the driver that connects to the database.
	 * 
	 * @return The full-specified class-name of the driver-class.
	 */
	public abstract String getDriverClass();

	/**
	 * Returns the dialect which his used to establish the connection.
	 * 
	 * @return The used dialect.
	 */
	public abstract String getDialect();

	/**
	 * Return the URL which identifies the location of the database.
	 * 
	 * @return The database-URL.
	 */
	public abstract String getUrl();

	/**
	 * Return the URL which directs to the administrator-interface of the
	 * database.
	 * 
	 * @return The administrator-URL.
	 */
	public abstract String getAdminUrl();
}
