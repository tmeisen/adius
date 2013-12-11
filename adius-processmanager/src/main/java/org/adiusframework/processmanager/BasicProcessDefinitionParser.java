package org.adiusframework.processmanager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.adiusframework.processmanager.xml.ProcessDefinition;
import org.adiusframework.util.xml.OxMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The BasicProcessDefinitionParser provides several methods to create a
 * ProcessDefinition from XML-Data. The XML-Data can be, as the
 * ProcessDefinitionParse interface requires, represented by a InputStream, a
 * String or a File, which contains the data.
 */
public class BasicProcessDefinitionParser implements ProcessDefinitionParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicProcessDefinitionParser.class);

	private URL schemaUrl;

	private OxMapper mapper;

	/**
	 * Sets the location of the schema-file, represented by a URL, of a XML-File
	 * which contains a ProcessDefinition.
	 * 
	 * @param schemaUrl
	 *            The URL to be set
	 */
	public void setSchemaUrl(URL schemaUrl) {
		this.schemaUrl = schemaUrl;
	}

	/**
	 * @return The current URL
	 */
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
	public ProcessDefinition parse(InputStream xmlStream) {
		try {
			LOGGER.debug("Parsing input stream resource");
			ProcessDefinition pd = mapper.read(xmlStream, ProcessDefinition.class);
			LOGGER.debug(pd.toString());
			return pd;
		} catch (IOException e) {
			LOGGER.error("IOEException during unmarshal process: " + e.getMessage());
		}
		return null;
	}

	@Override
	public ProcessDefinition parse(String xmlString) {
		try {
			LOGGER.debug("Parsing xmlString " + xmlString);
			ProcessDefinition pd = mapper.read(xmlString, ProcessDefinition.class);
			LOGGER.debug(pd.toString());
			return pd;
		} catch (IOException e) {
			LOGGER.error("IOEException during unmarshal process: " + e.getMessage());
		}
		return null;
	}

	@Override
	public ProcessDefinition parse(File xmlFile) {
		try {
			LOGGER.debug("Parsing xmlFile " + xmlFile);
			ProcessDefinition pd = mapper.read(xmlFile, ProcessDefinition.class);
			LOGGER.debug(pd.toString());
			return pd;
		} catch (IOException e) {
			LOGGER.error("IOEException during unmarshal process: " + e.getMessage());
		}
		return null;
	}

}
