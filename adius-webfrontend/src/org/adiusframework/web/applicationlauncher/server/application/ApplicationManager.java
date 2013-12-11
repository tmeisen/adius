package org.adiusframework.web.applicationlauncher.server.application;

import java.io.OutputStream;
import java.util.List;

/**
 * This interface should be implemented when is required to create an
 * application manager to handle the basic operations over the process
 * that will be created by a specified application.
 *
 * @author Tobias Meisen
 */
public interface ApplicationManager {

	/**
	 * Useful when a class wants to implement behavior for executing an application
	 * manager, and enables also the initialization of the stream manager.
	 * 
	 * @param enableStreamManager 
	 * 		  True, if i will be required a stream manager with associated channels, otherwise false.
	 */
	public void execute(boolean enableStreamManager);

	/**
	 * If a class wants to implement a specific way to shutdown a process 
	 * created by an application.
	 */	
	public void shutdown();

	/**
	 * When it will be necessary to set the required way to destroy a process 
	 * created by an application.
	 */	
	public void destroy();

	/**
	 * Specific behavior to be defined when all the information contained in the 
	 * specified channel is required to be retrieved.
	 * 
	 * @param channel
	 * 		  Indicates, if the next data will be read from the input, error, ..., etc stream.
	 * @return A set of strings with data defined in the detailed specification. 
	 */	
	public List<String> readOutput(int channel);

	/**
	 * It will indicates under which conditions the process will finish.
	 * 
	 * @return A boolean value according to the proper implementation.
	 */
	public boolean isFinished();

	/**
	 * It will indicates under which conditions the process will get an error.
	 * 
	 * @return A boolean value according to the proper implementation.
	 */	
	public boolean isError();

	/**
	 * It will return a specific code related with the process.
	 * 
	 * @return An integer according to the specific implementation.
	 */	
	public Integer getReturnCode();
	
	/**
	 * The method will return the shutdown parameters associated with 
	 * the created process.
	 * 
	 * @return Commands to do a proper shutdown of the process.
	 */		
	public String getShutdownParams();
	
	/**
	 * The method will return the shutdown type of the created process.
	 * 
	 * @return A enumeration type for the shutdown of the application process.
	 */	
	public ShutdownType getShutdownType();

	/**
	 * The method will return a output stream reference related with 
	 * the created process.
	 * 
	 * @return Output stream reference for the specified process.
	 */		
	public OutputStream getOutputStream();

}