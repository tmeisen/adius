package org.adiusframework.web.applicationlauncher.server;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener that will load all the properties stored in the server,
 * it executes first, and he guarantees that the data will be available
 * when the client uses the entry point.
 * 
 * @author Tobias Meisen
 */
public class WALLifeCycleListener implements ServletContextListener {

	/** Name of the context loader. */
	public static final String CONTEXT_LOADER = "context";	
	/** Instance for all the logs related with this listener. */
	private final static Logger LOGGER = LoggerFactory.getLogger(WALLifeCycleListener.class);
	
	/**
	 * Builds a loader object according to the application type.
	 * 
	 * @param apploaderType
	 *  	  According to the type, a context loader will be created.
	 * @return A default application definition loader, or another one according to the specified type.
	 */	
	private AppDefLoader buildLoader(String apploaderType) {		
		// If there are more context types, add them like this: if (apploaderType.equals(CONTEXT_LOADER)) { return new ContextAppDefLoader();		
		return new ContextAppDefLoader(); // Default type (Context)
	} // end : buildLoader method 
	
	/**
	 * Method executed when the listener starts, it will load the applications
	 * properties contained in the configuration.
	 * 
	 * @param event
	 *  	  Event according to the servlet context.
	 */		
	@Override
	public void contextInitialized(ServletContextEvent event) {
		
		LOGGER.info("And when the Dragon saw that he was cast unto the earth, he persecuted the Woman which brought forth the Man-Child.");
		
		AppDefLoader loader;
		final String apploaderType; 
		final String apploaderParam;		
		final ServletContext context;
		final Map<String, ApplicationDefinition> appDefList;
		
		// Load the application configuration
		context = event.getServletContext();
		apploaderType = context.getInitParameter("config.apploader.type");
		apploaderParam = context.getInitParameter("config.apploader.param");
		
		LOGGER.info("Loading configuration with loader [" + apploaderType + "] and param [" + apploaderParam + "]");

		loader = buildLoader(apploaderType);
		
		if (loader != null) {
			try {
				// Load and assign the properties load from the configuration file.
				appDefList = loader.load(context.getResourceAsStream(apploaderParam));
				WALServerManager.get().setAppDefData(appDefList);

				// Creation of the instances counter for each application definition
				// Transform the set of ids directly into an array of Strings.
				InstanceSupervisor instanceSupervisor = InstanceSupervisor.getInstance();				
				instanceSupervisor.initialize(appDefList.keySet().toArray(new String[0]));				
			} catch (IOException ioe) {
				LOGGER.error("Loading of application definition failed, due to loader exception: " + ioe.getMessage());
			}
		}
		
	} // end : contextInitialized method 	
	
	/**
	 * Method executed when the listener finishes.
	 * 
	 * @param event
	 *  	  Event according to the servlet context.
	 */			
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		LOGGER.info("And the Dragon died, cause he was to old to satisfy the needs of human kind.");
	} // end : contextDestroyed method 	
	
}