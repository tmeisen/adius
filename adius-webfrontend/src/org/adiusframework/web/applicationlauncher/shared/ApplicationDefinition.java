package org.adiusframework.web.applicationlauncher.shared;

import java.io.Serializable;

/**
 * Class that handle all the properties for defining an application to 
 * be executed and stopped by the Web Application Launcher.
 * 
 * @author Tobias Meisen
 */
public class ApplicationDefinition implements Serializable {
	
	/** Identification name for this application. */
	private String id;

	/** Title related to the name to be displayed in the UI. */
	private String displayName;

	/** Property for the executable file of the application. */
	private String execFile;

	/** Path that contains the executable file of the application. */
	private String execFolder;

	/** It contains the shutdown parameters for the application. */
	private String shutdown;

	/** Maximum number of instances possible for this application. */
	private int instanceMax;
	
	/** UID used for serialization. */
	private static final long serialVersionUID = 5730218401568569176L;		

	
	// PUBLIC METHODS _____________________________________________________________________________
	// ********************************************************************************************
	
	/** 
	 * The String representation of an object of this class will be its display name. 
	 * 
	 * @return The display name showed in the graphical user interface.
	 */
	@Override
	public String toString() {
		return displayName;
	} // end : toString method 

	/** 
	 * Hashcode for an object of this class. 
	 * 
	 * @return The hash number that represent an instance of this class.
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	} // end : hashCode method 

	/** 
	 * It will check if two objects of this class are equal. 
	 * 
	 * @param obj 
	 * 		  Object wanted to be compared with the current instance
	 * @return True, if they are equal according to this implementation, false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}			
		if (obj == null) {
			return false;
		}			
		if (getClass() != obj.getClass()) {
			return false;
		}
			
		ApplicationDefinition other = (ApplicationDefinition) obj;		
		if (this.id == null) {
			if (other.id != null) return false;				
		} else {
			if (!this.id.equals(other.id)) return false;					
		}
		       
		return true;
	} // end : equals method 	
	
	/** 
	 * Validates if all the important attributes for defining an application are assigned. 
	 * 
	 * @return False if one validation is not successful, true, if everything is set correctly.
	 */
	public boolean validate() {
		return StringUtils.isNotEmpty(this.id)       && (this.instanceMax > 0)
			&& StringUtils.isNotEmpty(this.execFile) && StringUtils.isNotEmpty(this.execFolder)
			&& StringUtils.isNotEmpty(this.shutdown) && StringUtils.isNotEmpty(this.displayName);  
	} // end : validate method 	
	
	
	// GETTERS AND SETTERS ________________________________________________________________________
	// ********************************************************************************************

	/** @return The current associated ID of the application. */
	public String getId() {
		return id;
	}

	/** @param id The application's ID to set. */
	public void setId(String id) {
		this.id = id;
	}

	/** @return The display name of the application. */
	public String getDisplayName() {
		return displayName;
	}

	/** @param displayName The application display name to set. */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/** @return The execution file of the application. */
	public String getExecFile() {
		return execFile;
	}

	/** @param execFile Execution file name to set. */
	public void setExecFile(String execFile) {
		this.execFile = execFile;
	}

	/** @return The path of the execution files of the application. */
	public String getExecFolder() {
		return execFolder;
	}

	/** @param execFolder Execution files' path to set. */
	public void setExecFolder(String execFolder) {
		this.execFolder = execFolder;
	}

	/** @return The maximum number of instances possible for the application. */
	public int getInstanceMax() {
		return instanceMax;
	}

	/** @param instanceMax Maximum number possible of instances to set. */
	public void setInstanceMax(int instanceMax) {
		if(instanceMax < 0) {
			System.out.println("The maximum number of instances can not be negative.");
			return;
		}
		this.instanceMax = instanceMax;
	}

	/** @return The shutdown parameters for the application. */
	public String getShutdown() {
		return shutdown;
	}

	/** @param shutdown Shutdown parameters to set. */
	public void setShutdown(String shutdown) {
		this.shutdown = shutdown;
	}

}