package org.adiusframework.service;

import java.io.File;
import java.io.InputStream;

import org.adiusframework.service.xml.ServiceDefinition;

public interface ServiceDefinitionParser {

	public abstract ServiceDefinition parse(InputStream xmlStream);

	public abstract ServiceDefinition parse(String xmlString);

	public abstract ServiceDefinition parse(File xmlFile);

	public abstract void enableErrorHandling();

	public abstract void disableErrorHandling();

}