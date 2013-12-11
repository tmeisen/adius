package org.adiusframework.service.excel.parser.cache;

import java.io.Serializable;
import java.util.List;

public interface ParserResultCache {

	public void registerSetCache(Class<? extends Serializable> elClass);

	public void registerKeyCache(Class<? extends Serializable> elClass);

	public Serializable put(Serializable element);

	public <T extends Serializable> T get(Serializable key, Class<T> elClass);

	public void registerKeyCache(Class<? extends Serializable> elClass, String value, String reference);

	public <T extends Serializable> List<T> getAll(Class<T> elClass);
	
	public void dispose();

	public <T extends Serializable> T get(Serializable key, Class<T> elClass, boolean qualified);

}
