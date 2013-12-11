package org.adiusframework.serviceprovider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;
import org.adiusframework.util.xml.OxMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicServiceProviderDefinitionParser implements ServiceProviderDefinitionParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicServiceProviderDefinitionParser.class);

	private URL schemaUrl;

	private OxMapper mapper;

	public void setSchemaUrl(URL schemaUrl) {
		this.schemaUrl = schemaUrl;
	}

	public URL getSchemaUrl() {
		return schemaUrl;
	}

	public void setMapper(OxMapper mapper) {
		this.mapper = mapper;
	}

	public OxMapper getMapper() {
		return mapper;
	}

	@Override
	public ServiceProviderDefinition parse(InputStream xmlStream) {
		try {
			LOGGER.debug("Parsing input stream resource");
			return mapper.read(xmlStream, ServiceProviderDefinition.class);
		} catch (IOException e) {
			LOGGER.error("IOEException during unmarshal process: " + e.getMessage());
		}
		return null;
	}

	@Override
	public ServiceProviderDefinition parse(String xmlString) {
		try {
			LOGGER.debug("Parsing xmlString " + xmlString);
			return mapper.read(xmlString, ServiceProviderDefinition.class);
		} catch (IOException e) {
			LOGGER.error("IOEException during unmarshal process: " + e.getMessage());
		}
		return null;
	}

	@Override
	public ServiceProviderDefinition parse(File xmlFile) {
		try {
			LOGGER.debug("Parsing xmlFile " + xmlFile);
			return mapper.read(xmlFile, ServiceProviderDefinition.class);
		} catch (IOException e) {
			LOGGER.error("IOEException during unmarshal process: " + e.getMessage());
		}
		return null;
	}

}
