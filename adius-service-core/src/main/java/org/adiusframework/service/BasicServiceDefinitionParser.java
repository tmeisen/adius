package org.adiusframework.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.adiusframework.service.xml.ServiceDefinition;
import org.adiusframework.util.xml.OxMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.UnmarshallingFailureException;

public class BasicServiceDefinitionParser implements ServiceDefinitionParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicServiceDefinitionParser.class);

	private URL schemaUrl;

	private OxMapper mapper;

	private boolean errorHandling;

	public BasicServiceDefinitionParser() {
		schemaUrl = null;
		enableErrorHandling();
	}

	public void setMapper(OxMapper mapper) {
		this.mapper = mapper;
	}

	public OxMapper getMapper() {
		return mapper;
	}

	@Override
	public void enableErrorHandling() {
		errorHandling = true;
	}

	@Override
	public void disableErrorHandling() {
		errorHandling = false;
	}

	public void setSchemaUrl(URL schemaUrl) {
		this.schemaUrl = schemaUrl;
	}

	public URL getSchemaUrl() {
		return schemaUrl;
	}

	@Override
	public ServiceDefinition parse(InputStream xmlStream) {
		try {
			return mapper.read(xmlStream, ServiceDefinition.class);
		} catch (IOException e) {
			if (errorHandling) {
				LOGGER.error("IOException during unmarshal process: " + e.getMessage());
				throw new IllegalArgumentException(e);
			}
		} catch (UnmarshallingFailureException e) {
			if (errorHandling) {
				LOGGER.error("UnmarshallingFailureException during unmarshal process: " + e.getMessage());
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}

	@Override
	public ServiceDefinition parse(String xmlString) {
		try {
			return mapper.read(xmlString, ServiceDefinition.class);
		} catch (IOException e) {
			if (errorHandling) {
				LOGGER.error("IOException during unmarshal process: " + e.getMessage());
				throw new IllegalArgumentException(e);
			}
		} catch (UnmarshallingFailureException e) {
			if (errorHandling) {
				LOGGER.error("UnmarshallingFailureException during unmarshal process: " + e.getMessage());
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}

	@Override
	public ServiceDefinition parse(File xmlFile) {
		try {
			return mapper.read(xmlFile, ServiceDefinition.class);
		} catch (IOException e) {
			if (errorHandling) {
				LOGGER.error("IOException during unmarshal process: " + e.getMessage());
				throw new IllegalArgumentException(e);
			}
		} catch (UnmarshallingFailureException e) {
			if (errorHandling) {
				LOGGER.error("UnmarshallingFailureException during unmarshal process: " + e.getMessage());
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}

}
