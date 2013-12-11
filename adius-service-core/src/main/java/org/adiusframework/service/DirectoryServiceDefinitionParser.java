package org.adiusframework.service;

import java.io.File;
import java.util.List;

import org.adiusframework.service.xml.ServiceDefinition;

/**
 * Interface of a directory-based service definition parser. A class
 * implementing the interface has to provide the capability to identify service
 * definitions within a given directory and load it.
 * 
 * @author tm807416
 * 
 */
public interface DirectoryServiceDefinitionParser {

	/**
	 * Parses the passed directory for service definitions.
	 * 
	 * @param directory
	 *            String-Literal containing a directory reference
	 * @return list of identified and parsed service definitions
	 * @throws IllegalArgumentException
	 *             if the passed argument does not represent a valid directory
	 */
	public List<ServiceDefinition> parse(String directory);

	/**
	 * Parses the passed directory for service definitions
	 * 
	 * @param directory
	 *            {@link File} object containing a directory reference
	 * @return list of identified and parsed service definitions
	 * @throws IllegalArgumentException
	 *             if the passed argument does not represent a valid directory
	 */
	public List<ServiceDefinition> parse(File directory);

}
