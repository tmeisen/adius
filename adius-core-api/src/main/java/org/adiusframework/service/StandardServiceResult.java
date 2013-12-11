package org.adiusframework.service;

import java.util.List;
import java.util.Vector;

import org.adiusframework.resource.Resource;

public class StandardServiceResult implements ServiceResult {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 3793924644675664477L;

	private List<Resource> resources;

	public StandardServiceResult() {
		setResources(new Vector<Resource>());
	}

	protected void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void addResource(Resource resource) {
		if (resource.getCapability() == null)
			return;
		this.resources.add(resource);
	}

}
