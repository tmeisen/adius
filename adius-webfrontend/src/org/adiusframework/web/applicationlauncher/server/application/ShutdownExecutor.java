package org.adiusframework.web.applicationlauncher.server.application;

/**
 * This interface should be implemented when is required to execute
 * a shutdown over a specified process, application, etc.
 *
 * @author Tobias Meisen
 */
public interface ShutdownExecutor {

	/**
	 * Shutdown process behavior to be implemented over an application
	 * manager.
	 * 
	 * @param manager 
	 * 		  Application manager that will shutdown an application.
	 */
	public abstract void shutdown(ApplicationManager manager);

}