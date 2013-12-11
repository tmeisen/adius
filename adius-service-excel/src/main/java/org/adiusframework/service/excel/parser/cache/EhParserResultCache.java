package org.adiusframework.service.excel.parser.cache;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.adiusframework.service.excel.exception.ParserConfigException;
import org.adiusframework.util.reflection.AnnotationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EhParserResultCache implements ParserResultCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(EhParserResultCache.class);

	private static final String DEFAULT_CACHE_NAME = "base_cache";

	private static final String DEFAULT_CACHE_CONFIG = "/ehcache-parser-cfg.xml";

	private static final String KEY_DELIMITER = "|";

	private CacheManager manager;

	private Map<Class<? extends Serializable>, KeyGenerator<?>> keyMap;

	private Map<Class<? extends Serializable>, Cache> cacheMap;

	public EhParserResultCache() {
		this(DEFAULT_CACHE_CONFIG);
	}

	public EhParserResultCache(String cacheConfigFile) {

		// load the config from the passed cache config or use default settings
		if (cacheConfigFile != null && !cacheConfigFile.isEmpty()) {
			cacheConfigFile = DEFAULT_CACHE_CONFIG;
		}
		URL urlCacheConfig = getClass().getResource(cacheConfigFile);
		manager = CacheManager.newInstance(urlCacheConfig);

		// initialize the maps
		cacheMap = new HashMap<Class<? extends Serializable>, Cache>();
		keyMap = new HashMap<Class<? extends Serializable>, KeyGenerator<?>>();
	}

	@Override
	public void registerSetCache(Class<? extends Serializable> elClass) {
		registerKeyGenerator(elClass, new UniqueCounterGenerator());
		registerCache(elClass);
		LOGGER.debug("Registration of key and cache for " + elClass + " successful");
	}

	@Override
	public void registerKeyCache(Class<? extends Serializable> elClass) {

		// lets check if a valid key template is defined
		if (!AnnotationUtil.hasAnnotation(elClass, CacheKey.class)) {
			throw new ParserConfigException(elClass.getCanonicalName() + " has no cache key defined");
		}
		CacheKey keyTemplate = elClass.getAnnotation(CacheKey.class);
		if (keyTemplate.value() == null || keyTemplate.value().isEmpty()) {
			throw new ParserConfigException(elClass.getCanonicalName() + " has no value for cache key defined");
		}
		registerKeyCache(elClass, keyTemplate.value(), keyTemplate.reference());
	}

	@Override
	public void registerKeyCache(Class<? extends Serializable> elClass, String value, String reference) {
		KeyGenerator<?> generator = new SpelKeyGenerator(value, reference);
		registerKeyGenerator(elClass, generator);
		registerCache(elClass);
		LOGGER.debug("Registration of key and cache for " + elClass + " successful");
	}

	protected void registerKeyGenerator(Class<? extends Serializable> elClass, KeyGenerator<?> generator) {
		keyMap.put(elClass, generator);
		LOGGER.debug("Key generator of type " + generator.getClass() + " for " + elClass + " registered");
	}

	protected void registerCache(Class<? extends Serializable> elClass) {
		final String cacheName = elClass.getCanonicalName();
		if (manager.cacheExists(cacheName)) {
			LOGGER.debug("Cache named " + cacheName + " found");
			cacheMap.put(elClass, manager.getCache(cacheName));
		} else {
			LOGGER.debug("No cache found using default cache for " + elClass);
			if (!manager.cacheExists(DEFAULT_CACHE_NAME)) {
				manager.addCache(DEFAULT_CACHE_NAME);
			}
			cacheMap.put(elClass, manager.getCache(DEFAULT_CACHE_NAME));
		}
	}

	@Override
	public Serializable put(Serializable element) {
		final Class<? extends Serializable> elClass = element.getClass();
		KeyGenerator<?> generator = keyMap.get(elClass);
		Cache cache = cacheMap.get(elClass);
		if (cache == null || generator == null) {
			LOGGER.error("Cache " + (cache == null) + ", generator " + (generator == null) + " registration for "
					+ elClass + " not done correctly");
			throw new ParserConfigException(elClass.getCanonicalName() + " has not been registered in cache");
		}
		final String key = getClassifiedKey(element, elClass, generator);
		if (key == null) {
			throw new NullPointerException("Failed to generate key for " + element);
		}
		cache.put(new Element(key, element));
		return key;
	}

	public void putAll(Serializable... elements) {
		for (Serializable element : elements) {
			put(element);
		}
	}

	@Override
	public <T extends Serializable> T get(Serializable key, Class<T> elClass) {
		return get(key, elClass, true);
	}

	@Override
	public <T extends Serializable> T get(Serializable key, Class<T> elClass, boolean qualified) {
		Cache cache = lookUpCacheByClass(elClass);
		Serializable lookUpKey = key;
		if (!qualified) {
			lookUpKey = elClass.getCanonicalName() + KEY_DELIMITER + key.toString();
		}
		Element element = cache.get(lookUpKey);
		if (element == null) {
			return null;
		}
		final Object object = element.getObjectValue();
		if (elClass.isInstance(object)) {
			return elClass.cast(object);
		} else {
			LOGGER.error("Type mismatch occured between " + object.getClass() + " and " + elClass);
			throw new ParserConfigException("Type mismatch " + object.getClass().getCanonicalName()
					+ " cannot be cast to " + elClass.getCanonicalName());
		}
	}

	@Override
	public <T extends Serializable> List<T> getAll(Class<T> elClass) {
		Cache cache = lookUpCacheByClass(elClass);

		// get all keys
		List<T> result = new ArrayList<T>();
		List<?> keys = cache.getKeys();
		LOGGER.debug("Looking for all elements of class " + elClass);
		for (Object key : keys) {
			Element el = cache.get(key);
			if (el != null) {
				Object element = el.getObjectValue();
				if (elClass.isInstance(element)) {
					result.add(elClass.cast(element));
				}
			}
		}
		LOGGER.debug("Found " + result.size() + " elements");
		return result;
	}

	@Override
	public void dispose() {
		for (Cache cache : cacheMap.values()) {
			cache.dispose();
		}
		manager.shutdown();
	}

	protected String getClassifiedKey(Serializable element, Class<? extends Serializable> elClass,
			KeyGenerator<?> generator) {
		Serializable keySuffix = generator.generate(element);
		if (keySuffix == null) {
			LOGGER.error("Key generator returned null value");
			throw new ParserConfigException("Key generation for " + elClass.getCanonicalName() + " failed");
		}
		return elClass.getCanonicalName() + KEY_DELIMITER + keySuffix.toString();
	}

	/**
	 * Convenient method to lookup the cache associated to a class. If no cache
	 * has been registered for the class a runtime exception is thrown.
	 * 
	 * @param elClass
	 *            the class whose cache has to be lookup
	 * @return the cache associated to the class
	 */
	protected Cache lookUpCacheByClass(Class<?> elClass) {
		Cache cache = cacheMap.get(elClass);
		if (cache == null) {
			throw new ParserConfigException(elClass.getCanonicalName() + " has not been registered in cache");
		}
		return cache;
	}

}
