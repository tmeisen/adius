package org.adiusframework.util.datastructures;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The FtpConnectionContainer is an abstract class which gives base
 * functionality to connect to a specific FTP-server.
 */
public class FtpConnectionContainer implements Serializable {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 3552386473310491071L;

	/** The port where a FTP-server runs normally. */
	private static final int DEFAULT_FTP_PORT = 21;

	/** The protocol for a FTP-connection. */
	private static final String FTP_PROTOCOL = "ftp";

	private static final Logger LOGGER = LoggerFactory.getLogger(FtpConnectionContainer.class);

	/**
	 * This object stores the name of the host, which offers the FTP-server.
	 */
	private String host;

	/**
	 * The port where the server can be accessed.
	 */
	private int port;

	/**
	 * The user-name which is used to log-in on the server.
	 */
	private String user;

	/**
	 * The password which is needed for logging-in.
	 */
	private String password;

	/**
	 * Create a FtpConnectionContainer and sets the default port. To set another
	 * port use {@link #setPort(int)}.
	 */
	public FtpConnectionContainer() {
		setPort(DEFAULT_FTP_PORT);
	}

	/**
	 * Sets a new host.
	 * 
	 * @param host
	 *            The new host.
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Returns the host which is currently used.
	 * 
	 * @return The current host.
	 */
	public String getHost() {
		return this.host;
	}

	/**
	 * Sets a new port, if the given parameter is greater than zero.
	 * 
	 * @param port
	 *            The new port.
	 */
	public void setPort(int port) {
		if (port > 0)
			this.port = port;
	}

	/**
	 * Returns the port which is currently used.
	 * 
	 * @return The current port.
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Sets a new user-name.
	 * 
	 * @param user
	 *            The new user-name.
	 */
	public void setUser(String user) {
		this.user = user;
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
	 * Sets a new password.
	 * 
	 * @param password
	 *            The new password.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Return the password which is currently used.
	 * 
	 * @return The current password.
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Creates a URL with the parameters which are currently set.
	 * 
	 * @return The created URL for this connection or null of an error occurs.
	 */
	public URL getURL() {
		String url = new StringBuffer(FTP_PROTOCOL + "://").append(getUser()).append(":").append(getPassword())
				.append("@").append(getHost()).append(":").append(getPort()).toString();
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			LOGGER.error("Url " + url + " is malformed: " + e.getMessage());
			return null;
		}
	}

	@Override
	public String toString() {
		URL url = getURL();
		if (url == null)
			return "Malformed data";
		return url.toString();
	}

	/**
	 * Sets the parameters host, port, user-name and password based on a given
	 * URL.
	 * 
	 * @param url
	 *            The URL which is the base for this connection.
	 */
	public void setData(URL url) {
		setHost(url.getHost());
		setPort(url.getPort());
		if (url.getUserInfo() != null) {
			String[] userData = url.getUserInfo().split(":");
			setUser(userData[0]);
			if (userData.length > 1)
				setPassword(userData[1]);
		}
	}

}
