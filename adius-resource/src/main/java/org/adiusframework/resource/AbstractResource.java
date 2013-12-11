package org.adiusframework.resource;

import java.util.UUID;

import org.adiusframework.util.datastructures.SystemData;

public abstract class AbstractResource implements Resource {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -5236609649994979941L;

	private ResourceCapability capability;

	private UUID uuid;

	private String type;

	private String protocol;

	private SystemData systemData;

	public AbstractResource() {
		setUUID(UUID.randomUUID());
		setType(null);
		setProtocol(null);
		setSystemData(new SystemData());
	}

	@Override
	public UUID getUUID() {
		return uuid;
	}

	public void setUUID(UUID uuid) {
		this.uuid = uuid;
	}

	protected void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public boolean hasProtocol() {
		return getProtocol() != null;
	}

	protected void setSystemData(SystemData systemData) {
		this.systemData = systemData;
	}

	@Override
	public SystemData getSystemData() {
		return systemData;
	}

	@Override
	public String getType() {
		return type;
	}

	protected void setType(String type) {
		this.type = type;
	}

	@Override
	public ResourceCapability getCapability() {
		return capability;
	}

	public void setCapability(ResourceCapability capability) {
		this.capability = capability;
	}

	@Override
	public String toString() {
		return this.type + " (" + this.protocol + ") " + this.capability;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((capability == null) ? 0 : capability.hashCode());
		result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractResource other = (AbstractResource) obj;
		if (capability == null) {
			if (other.capability != null)
				return false;
		} else if (!capability.equals(other.capability))
			return false;
		if (protocol == null) {
			if (other.protocol != null)
				return false;
		} else if (!protocol.equals(other.protocol))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
