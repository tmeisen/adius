package org.adiusframework.util.net;

import java.io.Serializable;

public interface CallbackExecutor {

	public abstract boolean execute(Callback callback, Serializable data);

}
