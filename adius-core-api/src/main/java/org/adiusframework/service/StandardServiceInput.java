package org.adiusframework.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.adiusframework.resource.Resource;

public class StandardServiceInput implements ServiceInput {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 2357119344164508050L;

	private ConcurrentHashMap<String, Resource> resources;

	public StandardServiceInput() {
		resources = new ConcurrentHashMap<String, Resource>();
	}

	@Override
	public void add(String key, Resource resource) {
		resources.put(key, resource);
	}

	@Override
	public Resource get(String key) {
		return resources.get(key);
	}

	@Override
	public Set<String> getKeys() {
		return resources.keySet();
	}

	@Override
	public boolean contains(String key) {
		return resources.containsKey(key);
	}

}
