package org.adiusframework.web.applicationlauncher.server.application;

/**
 * Design in this class is based according to the shutdown type, 
 * to produce the appropriate console shutdown executor.
 * 
 * @author Tobias Meisen
 */
public class ShutdownExecutorFactory {

	/**
	 * This method will create a shutdown executor according to the
	 * shutdown type associated with the application manager.
	 * 
	 * @param manager 
	 * 		  Application manager that contains the shutdown type.
	 * @throws IllegalArgumentException
	 * 		   Thrown if the manager shutdown type is not supported. 
	 */	
	public static ShutdownExecutor create(ApplicationManager manager) {
		
		final ConsoleShutdownExecutor executor = new ConsoleShutdownExecutor();
		
		if (ShutdownType.CONSOLE.equals(manager.getShutdownType())) {			
			executor.setShutdownCommand(manager.getShutdownParams());
			return executor;
		} else {
			throw new IllegalArgumentException("Unsupported shutdown type: " + manager.getShutdownType());
		}
	} // end : create method

}