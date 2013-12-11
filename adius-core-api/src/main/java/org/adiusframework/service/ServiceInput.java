package org.adiusframework.service;

import java.io.Serializable;
import java.util.Set;

import org.adiusframework.resource.Resource;

public interface ServiceInput extends Serializable {

	public boolean contains(String key);

	public void add(String key, Resource resource);

	public Serializable get(String key);

	public Set<String> getKeys();

}
