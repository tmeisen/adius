package org.adiusframework.service.excel.parser.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractParserResultCache implements ParserResultCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractParserResultCache.class);

	private ConcurrentMap<Class<?>, ConcurrentMap<?, ?>> indexed;

	private ConcurrentMap<Class<?>, Collection<?>> listed;

	public AbstractParserResultCache(Class<?>[] listClasses, Class<?>[] keyClasses, Class<?>[] indexedClasses) {

		// make sure parameters are valid
		if (indexedClasses.length != keyClasses.length)
			throw new UnsupportedOperationException("IndexedClasses and KeyClasses length mismatch");
		if (!Collections.disjoint(Arrays.asList(listClasses), Arrays.asList(indexedClasses)))
			throw new UnsupportedOperationException("Class can only be indexed or listed but not both");

		// create the listed and indexes
		listed = new ConcurrentHashMap<Class<?>, Collection<?>>();
		for (Class<?> type : listClasses) {
			listed.put(type, createCollection(type));
		}
		indexed = new ConcurrentHashMap<Class<?>, ConcurrentMap<?, ?>>();
		for (int i = 0; i < indexedClasses.length; i++) {
			LOGGER.debug("Registering indexed class " + indexedClasses[i]);
			indexed.put(indexedClasses[i], createIndex(keyClasses[i], indexedClasses[i]));
		}

	}

	protected <T> void addListedObjects(Class<T> type, Collection<T> objects) {
		Collection<T> collection = getList(type);
		for (T object : objects) {
			collection.add(object);
		}
	}

	protected <T> void addListedObject(Class<T> type, T object) {
		Collection<T> collection = getList(type);
		collection.add(object);
	}

	protected <K, V> void addIndexedObject(Class<K> keyClass, Class<V> valueClass, K key, V value) {
		ConcurrentMap<K, V> index = getIndex(keyClass, valueClass);
		index.put(key, value);
	}

	protected <K, V> V getIndexedObject(Class<V> valueClass, K key) {
		ConcurrentMap<?, V> index = getIndex(key.getClass(), valueClass);
		return index.get(key);
	}

	protected <V> Collection<V> createCollection(Class<V> type) {
		return new Vector<V>();
	}

	protected <K, V> ConcurrentMap<K, V> createIndex(Class<K> keyType, Class<V> valueType) {
		return new ConcurrentHashMap<K, V>();
	}

	@SuppressWarnings("unchecked")
	protected <T> Collection<T> getList(Class<T> type) {
		return (Collection<T>) listed.get(type);
	}

	@SuppressWarnings("unchecked")
	protected <K, V> ConcurrentMap<K, V> getIndex(Class<K> keyClass, Class<V> valueClass) {
		return (ConcurrentMap<K, V>) indexed.get(valueClass);
	}

	@SuppressWarnings("unchecked")
	protected <T> Collection<T> getObjects(Class<T> type) {
		if (listed.containsKey(type))
			return (Collection<T>) listed.get(type);
		else if (indexed.containsKey(type)) {
			LOGGER.debug("Returning values of indexed map of class " + type);
			return (Collection<T>) (indexed.get(type).values());
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Class<?> c : listed.keySet()) {
			buf.append(c).append(":").append(listed.get(c)).append("\n");
		}
		for (Class<?> c : indexed.keySet()) {
			buf.append(c).append(":").append(indexed.get(c)).append("\n");
		}
		return buf.toString();
	}

}
