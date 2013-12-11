package org.adiusframework.util.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BasicCallbackRepository implements CallbackRepository {

	private ConcurrentMap<String, Callback> repository;

	public BasicCallbackRepository() {
		this.setRepository(new ConcurrentHashMap<String, Callback>());
	}

	@Override
	public Callback register(String identifier, Callback callback) {
		return getRepository().put(identifier, callback);
	}

	@Override
	public Callback unregister(String identifier) {
		return getRepository().remove(identifier);
	}

	protected ConcurrentMap<String, Callback> getRepository() {
		return repository;
	}

	protected void setRepository(ConcurrentMap<String, Callback> repository) {
		this.repository = repository;
	}

	@Override
	public Callback find(String identifier) {
		return getRepository().get(identifier);
	}

}
