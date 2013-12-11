package org.adiusframework.web.applicationlauncher.shared;

/**
 * Class that handle all the properties for defining a java  
 * application(with extra attributes) to be executed and 
 * stopped by the Web Application Launcher.
 * 
 * @author Tobias Meisen
 */
public class JavaApplicationDefinition extends ApplicationDefinition {
	
	/** Java agent for this application definition. */
	private String agent;

	/** Class name for the java application definition. */
	private String className;

	/** Java classpath for the application definition. */
	private String classPath;

	/** Input arguments for the java application. */
	private String[] arguments;
	
	/** Constant property for the java executable file. */
	private static final String JAVA_EXEC_FILE = "java";
	
	/** UID used for serialization. */
	private static final long serialVersionUID = -799674098391274155L;

	/**
	 * Default constructor, assign the default java property to the
	 * executable file.
	 */	
	public JavaApplicationDefinition() {		
		setExecFile(JAVA_EXEC_FILE);
	} // end : constructor
	
	
	// PUBLIC METHODS _____________________________________________________________________________
	// ********************************************************************************************
	
	/** 
	 * Validates if all the important attributes for defining an application are assigned. 
	 * 
	 * @return False if one validation is not successful, true, if everything is set correctly.
	 */	
	@Override
	public boolean validate() {
		boolean valid = super.validate();
		return valid && StringUtils.isNotEmpty(this.classPath) && StringUtils.isNotEmpty(this.className);
	} // end : validate method 	

	// GETTERS AND SETTERS ________________________________________________________________________
	// ********************************************************************************************	
	
	/** @return The current associated ID of the application. */
	public String getAgent() {
		return agent;
	}

	/** @param agent The java application agent to set. */
	public void setAgent(String agent) {
		this.agent = agent;
	}
	
	/** @return The java class name of the application. */
	public String getClassName() {
		return className;
	}

	/** @param className The java class name to set. */
	public void setClassName(String className) {
		this.className = className;
	}

	/** @return The current java application classpath. */
	public String getClassPath() {
		return classPath;
	}

	/** @param className The classpath for the java application to set. */
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	/** @return The contained arguments of the java application. */
	public String[] getArguments() {
		return arguments;
	}

	/** @param arguments Input arguments for the java application to set. */
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}

}