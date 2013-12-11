package org.adiusframework.web.applicationlauncher.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton class that will count for each application defined the
 * total number of created and running instances in the server.
 * 
 * @author Nelson Fernandez
 */
public final class InstanceSupervisor {

	/** Unique instance for the this class. */
	private static InstanceSupervisor instance;
	
	/** 
	 * Container for the counters of each application related with the total number of instances;
	 * in the values, the array position number zero will be for the number of created instances, 
	 * and, the array position number one will be for the number of running instances.
	 */
	private static Map<String,int[]> instTotalNumber = new HashMap<String,int[]>();
	
	/**
	 * Default constructor, private for be a singleton.	
	 */	
	private InstanceSupervisor() {} // end : constructor
		
	/** 
	 * It will create counters(arrays with two positions) for the applications to store the
	 * total number of created and running instances.
	 * 
	 * @param appDefs 
	 * 		  Names for the application definitions which a counter of instances will be required.	
	 * @throws IllegalArgumentException 
	 * 		   When the required array of application names is null or empty, or, when there are two similar names.
	 */	
	protected void initialize(String[] appDefs) { /*public - in shared*/	
		
		ArrayList<String> applicationDefinitions = new ArrayList<String>(); // Copy for safe iteration
		
		/* Validation for the name of the application definition. */
		if ( (appDefs == null) || (appDefs.length == 0) ) {
			throw new IllegalArgumentException("Not possible to initialize correctly the instance supervisor.");
		}		
		
		/* Validation to avoid duplicate application definition names */
		for (String appName : appDefs) {	
			applicationDefinitions.add(appName);				
		}		
		for (String text : applicationDefinitions) { // Check if a similar name is contained twice in the array	
			
			int duplicateCounter = 0;
			
			med: for (int idx = 0; idx < appDefs.length; idx++) {				
				if (text.trim().equalsIgnoreCase(appDefs[idx].trim())) {
					
					if (duplicateCounter == 1) {
						throw new IllegalArgumentException("Duplicate application definition name: " + text + " = " + appDefs[idx]);
					}
					
					duplicateCounter++;
					continue med;
				} // end : if					
			} // end : med	
		}	
	
		// -> Adding the counters for each application		
		for (String application : appDefs) {			
			instTotalNumber.put(application, new int[2]); // [0]: # Created instances ; [1]: # Running instances 	
		}		
	} // end : initialize method 
	
	
	// PUBLIC METHODS _____________________________________________________________________________
	// ********************************************************************************************

	/**
	 * It will return an object for this class, after this invocation initialize method must be called.	
	 */
	public static synchronized InstanceSupervisor getInstance() {
		if (instance == null) {
			instance = new InstanceSupervisor();
		}			
		return instance;
	} // end : getInstance method 	
	
	/** 
	 * Increments by one the number of total created or running instances.
	 * 
	 * @param appDef 
	 * 		  Name for the application definition which the number of instances will increase.
	 * @param creatOrRunn 
	 * 		  If it is true, it will increase the number of created instances, otherwise,
	 *        it will increase the number of running instances.
	 * @throws IllegalArgumentException 
	 * 		   When the required application name is null or empty.
	 */
	public synchronized void incrInstances(String appDef, boolean creatOrRunn) { // Change this boolean if more types are defined
		
		// Validation for the name of the application definition.
		if ( (appDef == null) || (appDef.isEmpty()) ) {
			throw new IllegalArgumentException("Null or empty application definition name.");
		}
		
		int[] totalNumInstances = instTotalNumber.get(appDef); // Get the counter for this application	
		
		if (creatOrRunn) { // Add one created instance
			totalNumInstances[0]++;
		}
		else { // Add one running instance
			totalNumInstances[1]++;
		}		
	} // end : incrInstances method 
	
	/** 
	 * Decrements by one the number of total created or running instances.
	 * 
	 * @param appDef 
	 * 		  Name for the application definition which the number of instances will decrease.
	 * @param creatOrRunn 
	 * 		  If it is true, it will decrease the number of created instances, otherwise,
	 *        it will decrease the number of running instances.
	 * @throws IllegalArgumentException 
	 * 		   When the required application name is null or empty.
	 */
	public synchronized void decrInstances(String appDef, boolean creatOrRunn) {
		
		// Validation for the name of the application definition.
		if ( (appDef == null) || (appDef.isEmpty()) ) {
			throw new IllegalArgumentException("Null or empty application definition name.");
		}
		
		int[] totalNumInstances = instTotalNumber.get(appDef); // Get the counter for this application	
				
		if (creatOrRunn) { // Remove one created instance
			if (totalNumInstances[0] - 1 < 0) {
				return; // Not possible to have negative numbers.
			}
			totalNumInstances[0]--;
		}
		else { // Remove one running instance
			if (totalNumInstances[1] - 1 < 0) {
				return; // Not possible to have negative numbers.
			}			
			totalNumInstances[1]--;
		}		
	} // end : decrInstances method 
	
	/** 
	 * Gets the total number of instances of an application defined in the server.
	 * 
	 * @return A map with the defined applications, and, an array with two positions, 
	 * 		   the first one for the number of created instances, and the second one 
	 * 		   for the running instances.	
	 */
	Map<String, int[]> getTotalInstances() {		
		if ( (instTotalNumber != null) && (!instTotalNumber.isEmpty()) ) {
			return instTotalNumber; 
		}
		else {
			return new HashMap<String,int[]>();
		}	
	} // end : getTotalInstances method 	

}