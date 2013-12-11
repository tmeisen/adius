package org.adiusframework.resource;

import org.typetools.TypeResolver;

public abstract class AbstractGenerator<T extends Resource> implements Generator<T> {

	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getResourceClass() {
		return (Class<T>) TypeResolver.resolveArguments(this.getClass(), Generator.class)[0];
	}

}
