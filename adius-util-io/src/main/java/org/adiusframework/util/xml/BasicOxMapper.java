package org.adiusframework.util.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

public class BasicOxMapper implements OxMapper {
	private Marshaller marshaller;

	private Unmarshaller unmarshaller;

	public Marshaller getMarshaller() {
		return marshaller;
	}

	public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}

	public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}

	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	@Override
	public void write(Object object, String filename) throws IOException {
		marshaller.marshal(object, new StreamResult(new FileOutputStream(filename)));
	}

	@Override
	public <T> T read(String filename, Class<T> target) throws IOException {
		return read(new FileInputStream(filename), target);
	}

	@Override
	public <T> T read(File file, Class<T> target) throws IOException {
		return read(new FileInputStream(file), target);
	}

	@Override
	public <T> T read(InputStream input, Class<T> target) throws IOException {
		Object data = unmarshaller.unmarshal(new StreamSource(input));
		if (target.isInstance(data))
			return target.cast(data);

		throw new IOException("Wrong object type");
	}
}
