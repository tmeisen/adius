package org.adiusframework.util.net;

public interface CallbackRepository {

	public Callback register(String identifier, Callback callback);

	public Callback unregister(String identifier);

	public Callback find(String identifier);

}
