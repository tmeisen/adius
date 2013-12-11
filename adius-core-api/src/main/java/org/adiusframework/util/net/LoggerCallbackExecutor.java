package org.adiusframework.util.net;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerCallbackExecutor implements CallbackExecutor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerCallbackExecutor.class);

	@Override
	public boolean execute(Callback callback, Serializable data) {
		LOGGER.debug("Callback triggered " + callback);
		LOGGER.debug("Data " + data);
		return true;
	}

}
