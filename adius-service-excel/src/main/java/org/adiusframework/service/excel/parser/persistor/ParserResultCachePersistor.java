package org.adiusframework.service.excel.parser.persistor;

import org.adiusframework.service.excel.parser.cache.ParserResultCache;

public interface ParserResultCachePersistor {

	public void persist(ParserResultCache cache);

}
