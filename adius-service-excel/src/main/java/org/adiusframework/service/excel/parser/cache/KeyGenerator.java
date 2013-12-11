package org.adiusframework.service.excel.parser.cache;

import java.io.Serializable;

public interface KeyGenerator<T extends Serializable> {

	public T generate(Object object);
	
}
