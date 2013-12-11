package org.adiusframework.resource;

public interface Converter<F extends Resource, T extends Resource> {

	public abstract Resource convert(Resource from, Resource to);

	public abstract Class<F> getClassFrom();

	public abstract Class<T> getClassTo();

}
