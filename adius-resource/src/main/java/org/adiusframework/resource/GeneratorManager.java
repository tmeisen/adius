package org.adiusframework.resource;

public interface GeneratorManager {

	public abstract Generator<? extends Resource> get(Class<? extends Resource> c);

}
