package org.adiusframework.web.applicationlauncher.server;

import org.adiusframework.web.applicationlauncher.server.application.ApplicationManager;
import org.adiusframework.web.applicationlauncher.server.application.JavaApplicationManager;
import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;
import org.adiusframework.web.applicationlauncher.shared.JavaApplicationDefinition;

/**
 * This class will be necessary when it is required to create a
 * manager object for an application depending of its type.
 * 
 * @author Tobias Meisen
 */
public class ApplicationManagerFactory {
	
	/**
	 * Creation of an application manager for a java application definition.
	 * 
	 * @param appDef
	 *  	  Java application definition for which a manager will be created.
	 * @return A manager with all the necessary data to handle the application.
	 */		
	private static ApplicationManager createJavaManager(JavaApplicationDefinition appDef) {
		
		final JavaApplicationManager manager = new JavaApplicationManager();

		// Transfer the properties from the definition into the launcher	
		manager.setJavaAgent(appDef.getAgent());
		manager.setShutdown(appDef.getShutdown());
		manager.setArguments(appDef.getArguments());
		manager.setClassName(appDef.getClassName());
		manager.setClassPath(appDef.getClassPath());		
		manager.setExecFolder(appDef.getExecFolder());
	
		return manager;
	} // end : createJavaManager method 	

	/**
	 * Creation of a manager object related with an application definition for
	 * all the operations supported according to its type.
	 * 
	 * @param appDef
	 *  	  Application definition for which a manager will be created.
	 * @return A manager with all the necessary data to manipulate the application. 
	 * @throws IllegalArgumentException 
	 * 		   If the type of the application definition is not supported.
	 */	
	public static ApplicationManager createManager(final ApplicationDefinition appDef) {

		// Checks the type of definition
		if (JavaApplicationDefinition.class.isInstance(appDef)) {
			return createJavaManager(JavaApplicationDefinition.class.cast(appDef));
		} else {
			throw new IllegalArgumentException("Definitions of type " + appDef.getClass() + " not supported");
		}

	} // end : createManager method 	

}