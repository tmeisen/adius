package org.adiusframework.resource;

public interface ResourceTypeMapper {

	public Class<? extends Resource> getClass(String type, String protocol);

}
