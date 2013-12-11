package org.adiusframework.util.datastructures;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The MySqlConnectionContainer extends the DbConnectionContainer class by
 * configuring it with value for a MySQL connection.
 */
public class MySqlConnectionContainer extends DbConnectionContainer {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 6437128459963428485L;

	private static final Logger LOGGER = LoggerFactory.getLogger(MySqlConnectionContainer.class);

	/** The prefix for URLs that direct to a MySQL database. */
	private static final String URLPREFIX = "jdbc:mysql://";

	/** The schema of all MySQL-databases. */
	private static final String MASTERSCHEMA = "mysql";

	/** The class-name of the driver-class. */
	protected static final String DRIVER_CLASS = "com.mysql.jdbc.Driver";

	/** The dialect-class which is needed for the driver. */
	protected static final String DRIVER_DIALECT = "org.hibernate.dialect.MySQLDialect";

	/**
	 * Stores the IP of the host which runs the database.
	 */
	private String ip;

	/**
	 * Stores the port where the database can be found.
	 */
	private Integer port;

	/**
	 * Create a new MySqlConnectionContainer without any special configuration.
	 */
	public MySqlConnectionContainer() {
	}

	/**
	 * Creates a new MySqlConnectionContainer and configures it with the given
	 * parameters.
	 * 
	 * @param ip
	 *            The IP of the host.
	 * @param port
	 *            The port of the database.
	 * @param schema
	 *            The schema which is used.
	 * @param user
	 *            The user-name to log-in with.
	 * @param password
	 *            The password for the user.
	 */
	public MySqlConnectionContainer(String ip, int port, String schema, String user, String password) {
		super(schema, user, password);
		this.setIp(ip);
		this.setPort(port);
	}

	@Override
	public String getDriverClass() {
		return DRIVER_CLASS;
	}

	/**
	 * Return the IP which is currently used.
	 * 
	 * @return The current IP.
	 */
	public String getIp() {
		return this.ip;
	}

	/**
	 * Sets a new IP.
	 * 
	 * @param ip
	 *            The new IP.
	 */
	public void setIp(String ip) {
		LOGGER.debug("Setting ip to: " + ip);
		this.ip = ip;
	}

	/**
	 * Return the port which is currently used.
	 * 
	 * @return The current port.
	 */
	public Integer getPort() {
		return this.port;
	}

	/**
	 * Sets a new port.
	 * 
	 * @param port
	 *            The new port.
	 */
	public void setPort(Integer port) {
		LOGGER.debug("Setting port to: " + port);
		this.port = port;
	}

	@Override
	public String getUrl() {
		if (this.getIp() == null || this.getSchema() == null || this.getPort() == null)
			return null;
		return MySqlConnectionContainer.URLPREFIX + this.getIp() + ":" + this.getPort() + "/" + this.getSchema();
	}

	@Override
	public String getAdminUrl() {
		if (this.getIp() == null || this.getPort() == null)
			return null;
		return MySqlConnectionContainer.URLPREFIX + this.getIp() + ":" + this.getPort() + "/" + MASTERSCHEMA;
	}

	@Override
	public void setProperties(Properties properties) {

		// setting super properties
		super.setProperties(properties);

		// setting mysql specific values
		String ip = properties.getProperty(DbConnectionConstants.DB_IP);
		if (ip != null)
			this.setIp(ip);
		Integer port = Integer.valueOf(properties.getProperty(DbConnectionConstants.DB_PORT));
		if (port != null)
			this.setPort(port);
		String schema = properties.getProperty(DbConnectionConstants.DB_SCHEMA);
		if (schema != null)
			this.setSchema(schema);
	}

	@Override
	public String getDialect() {
		return DRIVER_DIALECT;
	}

	@Override
	public DbConnectionContainer clone() {
		return new MySqlConnectionContainer(this.getIp(), this.getPort(), this.getSchema(), this.getUser(),
				this.getPassword());
	}
}
