package org.adiusframework.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface OxMapper {
	public abstract void write(Object object, String filename) throws IOException;

	public abstract <T> T read(String filename, Class<T> target) throws IOException;

	public abstract <T> T read(File file, Class<T> target) throws IOException;

	public abstract <T> T read(InputStream input, Class<T> target) throws IOException;

}
