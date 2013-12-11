package org.adiusframework.util.datastructures;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A SystemData object stores information about the host which created it.
 */
public class SystemData implements Serializable {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 8523395054047838330L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemData.class);

	/**
	 * The IP of the host.
	 */
	private String ip;

	/**
	 * The name of the host.
	 */
	private String name;

	/**
	 * Creates a new SystemData object and tries the initialize it with the
	 * proper values.
	 */
	public SystemData() {
		try {
			InetAddress i = InetAddress.getLocalHost();
			setIp(i.getHostAddress());
			setName(i.getHostName());
		} catch (UnknownHostException e) {
			LOGGER.error("UnknownHostException initializing system data: " + e.getMessage());
			setIp(null);
			setName(null);
		}
	}

	/**
	 * Sets a new IP.
	 * 
	 * @param ip
	 *            The new IP.
	 */
	protected void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * Return the IP of the host.
	 * 
	 * @return The host-IP.
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * Sets a new name.
	 * 
	 * @param name
	 *            The host-name.
	 */
	protected void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name + " " + this.ip;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Tests if a given SystemData object is equals to this one.
	 * 
	 * @param data
	 *            The given object.
	 * @return True if they are equal, false otherwise.
	 */
	public boolean validate(SystemData data) {
		LOGGER.debug("Validating system data [" + data + "] for [" + this + "]");
		return equals(data);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SystemData other = (SystemData) obj;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
