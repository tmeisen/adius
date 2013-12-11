package org.adiusframework.web.applicationlauncher.server.application;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Concrete implementation of a shutdown executor, useful for setting the
 * shutdown commands for closing the application.
 * 
 * @author Tobias Meisen
 */
public class ConsoleShutdownExecutor implements ShutdownExecutor {

	/** It contains the shutdown command(s) associated with this shutdown executor. */
	private String shutdownCommand;
	
	/** Instance for all the logs related with this specific shutdown executor. */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleShutdownExecutor.class);
	
	
	// PUBLIC METHODS _____________________________________________________________________________
	// ********************************************************************************************

	/**
	 * It will shutdown the application applying the shutdown commands
	 * with the application manager.
	 * 
	 * @param manager  
	 * 		  Manager that will handle the closing of the application. 
	 */		
	@Override
	public void shutdown(ApplicationManager manager) {
		
		// Checks if there is a application manager assigned
		if (manager == null) {
			LOGGER.error("Application manager is not available.");
			return;
		}
		
		final OutputStream os = manager.getOutputStream();
		final PrintWriter pw = new PrintWriter(new PrintStream(os));
		
		try {
			LOGGER.debug("Shutting down application...");
			
			if ( (this.shutdownCommand != null) || !this.shutdownCommand.isEmpty() ) {
				pw.write(this.shutdownCommand);
			} else {
				throw new IllegalArgumentException("Shutdown parameters null or incomplete.");
			}			
		} catch(Exception e) {
			LOGGER.error("Shutting down application failed, due to exception: " + e.getMessage());			
		} finally {
			pw.close();			
		}
	} // end : shutdown method 
	
	
	// GETTERS AND SETTERS ________________________________________________________________________
	// ********************************************************************************************
	
	/** @return The current associated shutdown command(s). */
	public String getShutdownCommand() {
		return shutdownCommand;
	}

	/** @param shutdownCommand Shutdown command(s) to set. */
	public void setShutdownCommand(String shutdownCommand) {
		this.shutdownCommand = shutdownCommand;
	}
	
}