package org.adiusframework.web.applicationlauncher.server.application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.adiusframework.web.applicationlauncher.server.exception.UnexpectedBehaviourException;

/**
 * Abstract template for an application manager to could control the internal
 * process created by the application, and its stream channels.
 * 
 * @author Tobias Meisen
 */
public abstract class AbstractApplicationManager implements ApplicationManager {

	/**
	 * Reference variable to create a native process and handle input/output
	 * operations.
	 */
	private Process process;

	/**
	 * Contained the commands for shutting down the process created by the
	 * application.
	 */
	private String shutdownParams;

	/**
	 * Support class to know if the state of the process are either finished or
	 * error.
	 */
	private ProcessThreadWatcher ptw;

	/** It handles the kind of shutdown for the application. */
	private ShutdownType shutdownType;

	/** It will perform the IO operations over the process. */
	private StreamManager streamManager;

	/**
	 * Constant that assigns this code to indicate the input channel for the
	 * selected stream.
	 */
	public final static int INPUTSTREAM_CHANNEL = 0;
	/**
	 * Constant that assigns this code to indicate the error channel for the
	 * selected stream.
	 */
	public final static int ERRORSTREAM_CHANNEL = 1;

	/**
	 * Default constructor, null reference to the stream manager.
	 */
	public AbstractApplicationManager() {
		this.streamManager = null;
	} // end : constructor

	// PROTECTED METHODS
	// __________________________________________________________________________
	// ********************************************************************************************

	/**
	 * This method will initialize the process, the process thread watcher, and,
	 * if it is also set, the stream manager.
	 * 
	 * @param commands
	 *            Commands to build a process builder useful for the process
	 *            initialization.
	 * @param execFolder
	 *            Folder in the systems referenced for the executable files.
	 * @param enableStreamManager
	 *            If it is set to true, it will initialize the stream manager,
	 *            and register its channels.
	 * @throws IllegalArgumentException
	 *             If there are not commands or execution folder to could build
	 *             the process.
	 * @throws UnexpectedBehaviourException
	 *             Thrown if an unexpected error happens in the initialization
	 *             of the process or the process thread watcher.
	 */
	protected void execute(String[] commands, File execFolder, boolean enableStreamManager) {

		// It will constructs a builder that will execute the process
		ProcessBuilder builder;

		// Java commands validation
		if ((commands != null) && (commands.length > 0)) {
			builder = new ProcessBuilder(commands);
		} else {
			throw new IllegalArgumentException("The commands for executing the application are not set");
		}
		// Execution folder validation
		if (execFolder != null) {
			builder.directory(execFolder);
		} else {
			throw new IllegalArgumentException("The execution folder is not available");
		}

		// Initialization of process and process thread watcher.
		try {
			this.process = builder.start();
			this.ptw = new ProcessThreadWatcher(this.process);
			this.ptw.setDaemon(true);
			this.ptw.start();
		} catch (IOException ioe) {
			throw new UnexpectedBehaviourException(ioe);
		}

		// Finally, lets initiate the stream manager if set
		if (enableStreamManager) {
			this.streamManager = new StreamManager();
			this.streamManager.register(getInputStream(), INPUTSTREAM_CHANNEL);
			this.streamManager.register(getErrorStream(), ERRORSTREAM_CHANNEL);
			this.streamManager.start();
		}
	} // end : execute method

	// PUBLIC METHODS
	// _____________________________________________________________________________
	// ********************************************************************************************

	/**
	 * It will indicates if the process thread watcher has finished.
	 * 
	 * @return True, if the process thread watcher has finished, false
	 *         otherwise.
	 */
	@Override
	public boolean isFinished() {
		if (this.ptw != null) {
			return this.ptw.isFinished();
		}
		return true;
	} // end : isFinished method

	/**
	 * It will indicates if the process thread watcher is in error state.
	 * 
	 * @return True, if the process thread watcher has an error, false
	 *         otherwise.
	 */
	@Override
	public boolean isError() {
		if (this.ptw != null) {
			return this.ptw.isError();
		}
		return false;
	} // end : isError method

	/**
	 * This method will initialize the process, the process thread watcher, and,
	 * if it is set the stream manager also.
	 * 
	 * @param channel
	 *            Indicates if the next data will be read from the input, error,
	 *            ..., etc stream.
	 * @return The next piece of data of the selected stream.
	 */
	public String readStreamData(int channel) {
		if (this.streamManager == null) {
			return ""; // Removed null to avoid NPE.
		}
		return streamManager.getNext(channel);
	} // end : readStreamData method

	/**
	 * It will read all the information contained in the specified channel.
	 * 
	 * @param channel
	 *            Indicates, if the next data will be read from the input,
	 *            error, ..., etc stream.
	 * @return An empty array list if the stream manager is null, otherwise, a
	 *         set of the strings with data.
	 */
	@Override
	public List<String> readOutput(int channel) {
		List<String> result = new ArrayList<String>();

		if (this.streamManager == null) {
			return result;
		}
		while (this.streamManager.hasNext(channel)) {
			result.add(this.streamManager.getNext(channel));
		}

		return result;
	} // end : readOutput method

	/**
	 * It will shutdown the process created by the application, and the stream
	 * manager if its necessary.
	 * 
	 * @throws IllegalArgumentException
	 *             Thrown if the factory could not create an instance of the
	 *             shutdown executor.
	 */
	@Override
	public void shutdown() {
		final ShutdownExecutor executor = ShutdownExecutorFactory.create(this);

		// Initial validation
		if (executor == null) {
			throw new IllegalArgumentException("Configuration of shutdown incomplete, failed getting the executor.");
		}

		executor.shutdown(this); // !!Shutdown!!

		// If the stream manager was initialized close it
		if (this.streamManager != null) {
			this.streamManager.close();
			this.streamManager = null;
		}
	} // end : shutdown method

	/**
	 * Destroy and remove from memory the process, process thread watcher, and
	 * stream manager instance variables.
	 */
	@Override
	public void destroy() {
		// Validation for a referenced process
		if (this.process != null) {
			this.process.destroy();
			this.process = null;
		}
		// Validation for a referenced process thread watcher
		if (this.ptw != null) {
			while (!this.ptw.isFinished()) {
				try {
					Thread.sleep(100/* mls */);
				} catch (InterruptedException ie) {
					// okay thread had been interrupted lets go on
				}
			}
			this.ptw = null;
		}
		// Validation for a referenced stream manager
		if (this.streamManager != null) {
			this.streamManager.close();
			this.streamManager = null;
		}
	} // end : destroy method

	/**
	 * It will return the exit value of the current referenced process.
	 * 
	 * @return Null if there is not an associated process, otherwise, the
	 *         process exit value.
	 */
	@Override
	public Integer getReturnCode() {
		if (this.process != null) {
			return this.process.exitValue();
		}
		return null;
	} // end : getReturnCode method

	/**
	 * It will return a reference for the input stream.
	 * 
	 * @return Null if there is not an associated process, otherwise, a
	 *         reference to the process input stream.
	 */
	public InputStream getInputStream() {
		if (this.process != null) {
			return this.process.getInputStream();
		}
		return null;
	} // end : getInputStream method

	/**
	 * It will return a reference for the output stream.
	 * 
	 * @return Null if there is not an associated process, otherwise, a
	 *         reference to the process output stream.
	 */
	@Override
	public OutputStream getOutputStream() {
		if (this.process != null) {
			return this.process.getOutputStream();
		}
		return null;
	} // end : getOutputStream method

	/**
	 * It will return a reference for the error stream.
	 * 
	 * @return Null if there is not an associated process, otherwise, a
	 *         reference to the process error stream.
	 */
	public InputStream getErrorStream() {
		if (this.process != null) {
			return this.process.getErrorStream();
		}
		return null;
	} // end : getErrorStream method

	/**
	 * This method will assigned the shutdown parameters for the current
	 * application manager.
	 * 
	 * @param config
	 *            String that contains shutdown parameters separated by the
	 *            characters "||".
	 * @throws IllegalArgumentException
	 *             Thrown if the parameter do not contain exactly two data
	 *             separated by a "||".
	 */
	public void setShutdown(String config) {
		String[] splConf = config.split("\\|\\|"); // !!Important!! in case the
													// OS changes

		if (splConf.length == 2) {
			this.shutdownType = ShutdownType.valueOf(splConf[0]);
			this.shutdownParams = splConf[1];
		} else {
			throw new IllegalArgumentException("The shutdown parameters were not set.");
		}
	} // end : setShutdown method

	// GETTERS AND SETTERS
	// ________________________________________________________________________
	// ********************************************************************************************

	// Protected //

	/** @return The current associated process. */
	protected Process getProcess() {
		return process;
	}

	/**
	 * @param process
	 *            Process to set.
	 */
	protected void setProcess(Process process) {
		this.process = process;
	}

	/** @return The current associated process thread watcher. */
	protected ProcessThreadWatcher getProcessThreadWatcher() {
		return ptw;
	}

	/**
	 * @param ptw
	 *            Process thread watcher to set.
	 */
	protected void setProcessThreadWatcher(ProcessThreadWatcher ptw) {
		this.ptw = ptw;
	}

	// Public //

	/** @return The current associated shutdown type. */
	@Override
	public ShutdownType getShutdownType() {
		return shutdownType;
	}

	/**
	 * @param shutdownType
	 *            Shutdown type to set.
	 */
	public void setShutdownType(ShutdownType shutdownType) {
		this.shutdownType = shutdownType;
	}

	/** @return The current associated shutdown parameters. */
	@Override
	public String getShutdownParams() {
		return shutdownParams;
	}

	/**
	 * @param shutdownType
	 *            Shutdown type to set.
	 */
	public void setShutdownParams(String shutdownParams) {
		this.shutdownParams = shutdownParams;
	}

}