package org.adiusframework.resource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.adiusframework.util.datastructures.Pair;

public class BasicResourceTypeMapper implements ResourceTypeMapper {

	private ConcurrentMap<Pair<String, String>, Class<? extends Resource>> mapping;

	public BasicResourceTypeMapper() {
		setMapping(new ConcurrentHashMap<Pair<String, String>, Class<? extends Resource>>());

		// lets define the basic mapping rules
		mapping.put(new Pair<String, String>(Resource.TYPE_FILE, Resource.PROTOCOL_FTP), FtpFileResource.class);
		mapping.put(new Pair<String, String>(Resource.TYPE_FILE, Resource.PROTOCOL_LOCAL), LocalFileResource.class);
		mapping.put(new Pair<String, String>(Resource.TYPE_DIRECTORY, Resource.PROTOCOL_FTP),
				FtpDirectoryResource.class);
		mapping.put(new Pair<String, String>(Resource.TYPE_DIRECTORY, Resource.PROTOCOL_LOCAL),
				LocalDirectoryResource.class);
	}

	public ConcurrentMap<Pair<String, String>, Class<? extends Resource>> getMapping() {
		return mapping;
	}

	public void setMapping(ConcurrentMap<Pair<String, String>, Class<? extends Resource>> mapping) {
		this.mapping = mapping;
	}

	@Override
	public Class<? extends Resource> getClass(String type, String protocol) {
		return mapping.get(new Pair<String, String>(type, protocol));
	}

}
