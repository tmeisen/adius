package org.adiusframework.query;

import java.io.Serializable;

import org.adiusframework.resource.FileSystemResource;
import org.adiusframework.resource.FileSystemResourceLoader;
import org.adiusframework.resource.ObjectResource;
import org.adiusframework.resource.Resource;
import org.adiusframework.resource.StringResourceCapability;
import org.adiusframework.resource.DefaultFileSystemResourceLoader;

public class QueryResourceFactory {

	// TODO: POTENTIAL OPTIMIZATION (move to resource)
	
	private static FileSystemResourceLoader fileSystemResourceFactory;

	protected static FileSystemResourceLoader getFileSystemResourceFactoryInstance() {
		if (fileSystemResourceFactory == null)
			fileSystemResourceFactory = new DefaultFileSystemResourceLoader();
		return fileSystemResourceFactory;
	}

	public static <T extends Serializable> Resource createObjectResource(T object, String capability) {
		ObjectResource<T> r = new ObjectResource<T>();
		r.setCapability(new StringResourceCapability(capability));
		r.setObject(object);
		return r;
	}

	public static FileSystemResource createFileSystemResource(String url, String capability) {
		FileSystemResource fr = getFileSystemResourceFactoryInstance().create(url,
				new StringResourceCapability(capability));
		if (fr != null && fr.getType().equals(Resource.TYPE_FILE))
			return fr;
		return null;
	}

	public static boolean isFileResource(String url) {
		return getFileSystemResourceFactoryInstance().validate(url);
	}

}
