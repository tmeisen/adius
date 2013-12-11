package org.adiusframework.web.applicationlauncher.server.application;

/**
 * This class monitor a process using a thread to know when
 * it will be in finished or error state.
 * 
 * @author Tobias Meisen
 */
public class ProcessThreadWatcher extends Thread {

	/** It will store if the process has an error. */
	private boolean error;
	
	/** It will store if the process is finished. */
	private boolean finished;
	
	/** Process that will be monitored by an instance of this class. */
	private Process process;

	/**
	 * Constructor that will assign the process to control its error
	 * and finish states.
	 * 
	 * @param process
	 * 		  It will assign the process to be monitored by an instance of this class.
	 */	
	public ProcessThreadWatcher(Process process) {
		this.process = process;	
	} // end : constructor
	
	
	// PUBLIC METHODS _____________________________________________________________________________
	// ********************************************************************************************

	/**
	 * It will begin a thread to put the current thread to wait until the associated process finish.
	 */
	@Override
	public void run() {		
		if (this.process != null) {
			try {
				this.finished = false;
				this.error = false;
				this.process.waitFor();
			} catch (InterruptedException ie) {
				this.error = true;
				ie.printStackTrace();
			}			
			this.finished = true;
		} else {
			System.out.println("There is not an associated process for the thread watcher.");
		}
	} // end : run method 
	
	
	// GETTERS AND SETTERS ________________________________________________________________________
	// ********************************************************************************************
	
	/** @return True if the process has finished, otherwise false. */
	public boolean isFinished() {
		return finished; // setFinished removed for being private, and used it just once
	}

	/** @return True if the process has an error, otherwise false. */
	public boolean isError() {
		return error; // setError removed for being private, and used it just once
	} 

}