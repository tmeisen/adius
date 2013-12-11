package org.adiusframework.web.applicationlauncher.server;

import java.util.Properties;

import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;
import org.adiusframework.web.applicationlauncher.shared.JavaApplicationDefinition;

/**
 * Information container of all the attributes of a application that
 * will be created with this factory.
 * 
 * @author Tobias Meisen
 */
public class AppDefFactory {

	/** Application property type. */
	protected static final String JAVA_APP_TYPE = "JAVA";
	/** Application property for the name displayed in the UI. */
	protected static final String CONFIG_DISPLAYNAME_SUFFIX = ".display";
	/** File for the application execution. */
	protected static final String CONFIG_EXECFILE_SUFFIX = ".execfile";
	/** Folder for the application execution. */
	protected static final String CONFIG_EXECFOLDER_SUFFIX = ".execfolder";
	/** Application property for the maximum number of instances that could be created. */
	protected static final String CONFIG_INSTANCEMAX_SUFFIX = ".instancemax";
	/** Property for the application shutdown. */
	protected static final String CONFIG_SHUTDOWN_SUFFIX = ".shutdown";
	/** Application property for the class name. */
	protected static final String CONFIG_JAVA_CLASSNAME_SUFFIX = ".classname";
	/** Property for the classpath of the application. */
	protected static final String CONFIG_JAVA_CLASSPATH_SUFFIX = ".classpath";
	/** Java agent related with the application. */
	protected static final String CONFIG_JAVA_AGENT_SUFFIX = ".javaagent";
	/** Arguments for the execution of the application. */
	protected static final String CONFIG_JAVA_ARGUMENTS_SUFFIX = ".arguments";
    /** Separator for the arguments of the application. */
	protected static final String CONFIG_JAVA_ARGUMENTS_DELIMITER = "\\|\\|";
	
	/**
	 * Additional method to finish the creation of an application definition,
	 * in case that it will be a Java.
	 * 
	 * @param appId
	 * 		  Identification of the java application.
	 * @param appDef 
	 * 		  Reference of the casted application definition.	  
	 * @param config
	 * 		  Configuration properties loaded from the server.	
	 */		
	protected static void enrichJavaAppDef(final String appId, final JavaApplicationDefinition appDef, final Properties config) {

		// Java specific properties
		final String agent;
		final String classname;
		final String classpath;
		final String argumentsStr;		
		
		agent        = config.getProperty(appId + CONFIG_JAVA_AGENT_SUFFIX);		
		classname    = config.getProperty(appId + CONFIG_JAVA_CLASSNAME_SUFFIX);		
		classpath 	 = config.getProperty(appId + CONFIG_JAVA_CLASSPATH_SUFFIX);		
		argumentsStr = config.getProperty(appId + CONFIG_JAVA_ARGUMENTS_SUFFIX);
		
		appDef.setAgent(agent);
		appDef.setClassName(classname);
		appDef.setClassPath(classpath);		
		appDef.setArguments(argumentsStr.split(CONFIG_JAVA_ARGUMENTS_DELIMITER));
		
	} // end : create enrichJavaAppDef
	
	/**
	 * Method to create a complete application definition according to the data
	 * specified to the factory.
	 * 
	 * @param type
	 *  	  Application type wanted in the creation of the application definition.
	 * @param appId
	 * 		  Identification of the application.
	 * @param config
	 * 		  Configuration properties loaded from the server.
	 * @return An completed application definition according to the specified type. 
	 * @throws IllegalArgumentException 
	 * 		   If the type of the argument is not supported for the factory, or, the validation
	 * 		   of the application attributes was not successful.
	 */	
	public static ApplicationDefinition create(final String type, final String appId, final Properties config) {
		
		// Configuration Properties	
		final String shutdown;
		final String execFile; 
		final String execFolder;
		final String displayName;
		final String instanceMax;
		final ApplicationDefinition appDef;
		
		// Creates the definition, depending on its type
		if (type.equals(JAVA_APP_TYPE)) {
			appDef = new JavaApplicationDefinition();
		} else {
			throw new IllegalArgumentException(type + " is not supported by the factory.");
		}
		
		appDef.setId(appId);

		// Setups the general properties
		shutdown   	= config.getProperty(appId + CONFIG_SHUTDOWN_SUFFIX);
		execFolder 	= config.getProperty(appId + CONFIG_EXECFOLDER_SUFFIX);
		displayName = config.getProperty(appId + CONFIG_DISPLAYNAME_SUFFIX);		
		instanceMax = config.getProperty(appId + CONFIG_INSTANCEMAX_SUFFIX);
		
		if (appDef.getExecFile() == null || appDef.getExecFile().isEmpty()) {
			execFile = config.getProperty(appId + CONFIG_EXECFILE_SUFFIX);
			appDef.setExecFile(execFile);
		}		
		
		appDef.setShutdown(shutdown);
		appDef.setExecFolder(execFolder);
		appDef.setDisplayName(displayName);		
		appDef.setInstanceMax(Integer.valueOf(instanceMax));

		// Adds the specific type properties
		if (JavaApplicationDefinition.class.isInstance(appDef)) {
			enrichJavaAppDef(appId, JavaApplicationDefinition.class.cast(appDef), config);
		} 

		// Final settings validation
		if (!appDef.validate()) {
			throw new IllegalArgumentException("Validation of the application definition failed!!");
		}
		
		return appDef;
	} // end : create method 

}