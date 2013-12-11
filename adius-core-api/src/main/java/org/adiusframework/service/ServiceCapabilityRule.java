package org.adiusframework.service;

import java.io.Serializable;

import org.adiusframework.service.xml.ServiceCapability;

public interface ServiceCapabilityRule extends Serializable {

	public abstract boolean satisfiedBy(ServiceCapability capability);

}
