package org.adiusframework.serviceprovider;

import java.io.File;
import java.io.InputStream;

import org.adiusframework.serviceprovider.xml.ServiceProviderDefinition;

public interface ServiceProviderDefinitionParser {

	public abstract ServiceProviderDefinition parse(InputStream xmlStream);

	public abstract ServiceProviderDefinition parse(String xmlString);

	public abstract ServiceProviderDefinition parse(File xmlFile);

}
