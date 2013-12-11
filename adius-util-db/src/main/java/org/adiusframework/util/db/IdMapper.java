package org.adiusframework.util.db;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IdMapper {
	private Map<Integer, Integer> mapper;

	public IdMapper() {
		this.mapper = new ConcurrentHashMap<Integer, Integer>();
	}

	public int size() {
		return this.mapper.size();
	}

	public void addId(Integer oldId, Integer newId) throws NonUniqueIndexException {
		if (oldId == null || newId == null)
			throw new NullPointerException("The id mapping cannot be null.");
		if (this.containsMapping(oldId))
			throw new NonUniqueIndexException("The id " + oldId + " is already mapped.");
		this.mapper.put(oldId, newId);
	}

	public boolean containsMapping(Integer id) {
		return this.mapper.containsKey(id);
	}

	public Integer getMapping(Integer id) {
		return this.mapper.get(id);
	}

	public void clear() {
		this.mapper.clear();
	}

	@Override
	public String toString() {
		return "Number of mappings: " + this.mapper.size();
	}
}
