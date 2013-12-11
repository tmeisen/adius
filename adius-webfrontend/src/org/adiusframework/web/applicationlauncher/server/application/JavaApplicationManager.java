package org.adiusframework.web.applicationlauncher.server.application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Java application manager to encapsulate extra useful data to
 * store the common features related with a java application.
 * 
 * @author Tobias Meisen
 */
public class JavaApplicationManager extends AbstractApplicationManager {

	/** The java application class name. */
	private String className;
	
	/** The classpath associated to the java application. */
	private String classpath;

	/** The java agent for the application manager. */
	private String javaAgent;

	/** The path where the execution files are located. */
	private String execFolder;

	/** The input arguments for the java application. */
	private String[] arguments;
	
	
	// PUBLIC METHODS _____________________________________________________________________________
	// ********************************************************************************************
	
	/**
	 * This method will build the commands for executing a java application,
	 * and enables also the initialization of the stream manager.
	 * 
	 * @param enableStreamManager 
	 * 		  True, if i will be required a stream manager with associated channels, otherwise false.
	 * @throws IllegalArgumentException
	 * 		   Thrown if is not possible to build the java command, because a important attribute of this class is missing. 
	 */	
	@Override
	public void execute(boolean enableStreamManager) {
		
		List<String> commands = new ArrayList<String>();

		// If a process is currently executed, just return
		if (getProcess() != null) {
			System.out.println("There is already an associated process to execute the java application");
			return;
		}

		// Building the Command	//	
		
		commands.add("java");		
		commands.add("-cp"); // classpath
		
		// Classpath validation and addition to the commands
		if ( (this.classpath != null) || !this.classpath.isEmpty() ) {
			commands.add("\"" + this.classpath + "\"");
		} else {
			throw new IllegalArgumentException("The java classpath is not correctly set");
		}
		
		// If required the java agent
		// WARNING - It could be an error initializing the JVM, if the Java Agent is incorrect.
		if ( (this.javaAgent != null) && !this.javaAgent.isEmpty() ) {
			commands.add("-javaagent:" + this.javaAgent);
		}
		 
		// Class name validation and addition to the commands
		if ( (this.className != null) || !this.className.isEmpty() ) {
			commands.add(this.className);
		} else {
			throw new IllegalArgumentException("The java class name is not correctly set");
		}
		
		// Java application arguments
		if (this.arguments != null) {
			for (String argument : this.arguments) {
				commands.add(argument);
			}
		}

		// -> Lets execute the process, if the last validation succeeds
		if ( (this.execFolder != null) && !this.execFolder.isEmpty() ) {
			execute(commands.toArray(new String[0]), new File(this.execFolder), enableStreamManager);
		} else {
			throw new IllegalArgumentException("The execution folder is not available");
		}		
	} // end : execute method 
	
	
	// GETTERS AND SETTERS ________________________________________________________________________
	// ********************************************************************************************

	/** @return The current associated java class name. */
	public String getClassName() {
		return className;
	}

	/** @param className Java class name to set. */
	public void setClassName(String className) {
		this.className = className;
	}
	
	/** @return The current associated java classpath. */
	public String getClasspath() {
		return classpath;
	}

	/** @param classpath Java classpath to set. */
	public void setClassPath(String classpath) {
		this.classpath = classpath;
	}
	
	/** @return The current associated java agent. */
	public String getJavaAgent() {
		return javaAgent;
	}

	/** @param javaAgent Java agent to set. */
	public void setJavaAgent(String javaAgent) {
		this.javaAgent = javaAgent;
	}

	/** @return The current associated execution folder. */
	public String getExecFolder() {
		return execFolder;
	}

	/** @param execFolder Execution folder to set. */
	public void setExecFolder(String execFolder) {
		this.execFolder = execFolder;
	}

	/** @return The current associated console arguments. */
	public String[] getArguments() {
		return arguments;
	}

	/** @param arguments Console arguments to set. */
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

}