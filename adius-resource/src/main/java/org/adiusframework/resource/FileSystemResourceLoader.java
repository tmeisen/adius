package org.adiusframework.resource;

import java.net.URI;

public interface FileSystemResourceLoader {

	public abstract FileSystemResource create(URI uri, ResourceCapability rc);

	public abstract FileSystemResource create(String url, ResourceCapability rc);

	public abstract boolean validate(String url);

	public abstract boolean supports(String scheme);

}
