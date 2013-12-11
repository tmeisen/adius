package org.adiusframework.resource;

public interface Generator<T extends Resource> {

	public T generate(Resource base);

	public Class<T> getResourceClass();

}
