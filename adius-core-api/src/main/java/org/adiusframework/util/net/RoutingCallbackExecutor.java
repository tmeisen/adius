package org.adiusframework.util.net;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoutingCallbackExecutor implements CallbackExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoutingCallbackExecutor.class);

	private ConcurrentMap<Class<? extends Callback>, CallbackExecutor> route;

	private CallbackExecutor defaultExecutor;

	public RoutingCallbackExecutor() {
		setRoute(new ConcurrentHashMap<Class<? extends Callback>, CallbackExecutor>());
	}

	public CallbackExecutor getDefaultExecutor() {
		return defaultExecutor;
	}

	public void setDefaultExecutor(CallbackExecutor defaultExecutor) {
		this.defaultExecutor = defaultExecutor;
	}

	public ConcurrentMap<Class<? extends Callback>, CallbackExecutor> getRoute() {
		return route;
	}

	public void setRoute(ConcurrentMap<Class<? extends Callback>, CallbackExecutor> route) {
		this.route = route;
	}

	@Override
	public boolean execute(Callback callback, Serializable data) {
		LOGGER.debug("Searching for executor of class " + callback.getClass() + " in " + getRoute());
		CallbackExecutor executor = getRoute().get(callback.getClass());
		LOGGER.debug("Found executor " + executor);
		if (executor == null && getDefaultExecutor() == null)
			throw new UnsupportedOperationException("No callback executor configured for " + callback.getClass()
					+ " and no default executor defined");
		else if (executor == null)
			return getDefaultExecutor().execute(callback, data);
		LOGGER.debug("Executing executor with callback...");
		return executor.execute(callback, data);
	}

}
