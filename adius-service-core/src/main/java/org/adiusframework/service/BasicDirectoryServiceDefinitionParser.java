package org.adiusframework.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.adiusframework.service.xml.ServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic implementation of a {@link DirectoryServiceDefinitionParser}.
 * 
 * @author tm807416
 * 
 */
public class BasicDirectoryServiceDefinitionParser implements DirectoryServiceDefinitionParser {

	/**
	 * Static logger for this class
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(BasicDirectoryServiceDefinitionParser.class);

	/**
	 * The default service definition file extension used to identify service
	 * definition files within a directory
	 */
	public static final String DEFAULT_SD_FILE_EXT = ".xml";

	private ServiceDefinitionParser parser;

	public ServiceDefinitionParser getParser() {
		return parser;
	}

	public void setParser(ServiceDefinitionParser parser) {
		this.parser = parser;
	}

	@Override
	public List<ServiceDefinition> parse(String directory) {
		return parse(new File(directory));
	}

	@Override
	public List<ServiceDefinition> parse(File directory) {

		// check if a valid directory is given
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Parameter: " + directory.getPath() + " does not represent a directory");
		}

		// prepare parser and list
		parser.disableErrorHandling();
		List<ServiceDefinition> resultList = new ArrayList<ServiceDefinition>();

		// check for each file and candidate
		File[] files = directory.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (name.toLowerCase().endsWith(DEFAULT_SD_FILE_EXT) && !(new File(dir, name).isDirectory()));
			}
		});
		LOGGER.debug("Identified " + files.length + " possible definition files");
		for (File file : files) {

			// parse the file and check result
			ServiceDefinition definition = parser.parse(file);
			if (definition != null) {
				LOGGER.debug("Adding loaded definition from file " + file.getName());
				resultList.add(definition);
			} else {
				LOGGER.info("File: " + file + " has been skipped, validation failed.");
			}
		}
		return resultList;
	}

}
