package org.adiusframework.resource;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurableGeneratorManager implements GeneratorManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurableGeneratorManager.class);

	private ConcurrentMap<Class<? extends Resource>, Generator<? extends Resource>> mapping;

	public ConfigurableGeneratorManager() {
		setMapping(new ConcurrentHashMap<Class<? extends Resource>, Generator<?>>());
	}

	public void setGenerators(List<Generator<? extends Resource>> generators) {
		for (Generator<? extends Resource> generator : generators) {
			Generator<?> oldGenerator = getMapping().put(generator.getResourceClass(), generator);
			if (oldGenerator != null)
				LOGGER.error("For class " + generator.getClass() + " a generator has already been registered.");
		}
	}

	public ConcurrentMap<Class<? extends Resource>, Generator<? extends Resource>> getMapping() {
		return mapping;
	}

	public void setMapping(ConcurrentMap<Class<? extends Resource>, Generator<? extends Resource>> mapping) {
		this.mapping = mapping;
	}

	@Override
	public Generator<? extends Resource> get(Class<? extends Resource> c) {
		return getMapping().get(c);
	}

}
