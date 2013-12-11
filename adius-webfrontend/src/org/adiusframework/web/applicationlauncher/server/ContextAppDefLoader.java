package org.adiusframework.web.applicationlauncher.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;

/**
 * According to the context, this class will be helpful to load 
 * the data related with an application, the data will be retrieved
 * from a input stream.
 * 
 * @author Tobias Meisen
 */
public class ContextAppDefLoader implements AppDefLoader {

	/** Property related with the identifications of the loaded applications. */
	private static final String CONFIG_APPS_ID = "apps.id";
	/** Type defined as a property of the application definition. */
	private static final String CONFIG_TYPE_PREFIX = ".type";

	/**
	 * Creation of an application manager for a java application definition.
	 * 
	 * @param stream
	 *  	  Input stream with all the data related to the properties loaded from the server.
	 * @return A map with the application's id related with its application definition.
	 * @throws IllegalArgumentException 
	 * 		   If the type of the application is null or is empty. 
	 */	
	@Override
	public Map<String, ApplicationDefinition> load(InputStream stream) throws IOException {

		final String appIds;
		ApplicationDefinition appDef;		
		Properties config = new Properties();
		Map<String, ApplicationDefinition> applicationDefinitions = new LinkedHashMap<String, ApplicationDefinition>();

		// Load configuration and get the apps		
		config.load(stream); 
		appIds = config.getProperty(CONFIG_APPS_ID); 
		
		// Creates a new definition for each application
		if (appIds != null) {
			
			// Get all the needed information
			for (String appId : appIds.split("\\|\\|")) {
				
				final String type = config.getProperty(appId + CONFIG_TYPE_PREFIX);
				
				if (type == null || type.isEmpty()) {
					throw new IllegalArgumentException("Application type is missing!!");
				}
		
				appDef = AppDefFactory.create(type, appId, config);
				applicationDefinitions.put(appId, appDef); // Add it to the defs
			}
		}
		
		return applicationDefinitions;
	} // end : load method 	

}