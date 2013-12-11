package org.adiusframework.web.applicationlauncher.client.component;

import java.util.Map;

import org.adiusframework.web.applicationlauncher.client.WALRpcServiceManager;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

/**
 * In a determined time frame this timer will request the information 
 * of the total number of created and running instances on the server. 
 * 
 * @author Nelson Fernandez
 */
public class InstanceTimer extends Timer {
		
	/** Graphic area where the number of instances is showed. */
	FlexTable instancesTable;

	/**
	 * Constructor for the first row of the instances table.
	 * 
	 * @param instancesTable
	 *        Graphic component to show the instances numbers data. 
	 * @throws NullPointerException
	 * 		   When the argument contains null
	 */	
	public InstanceTimer(FlexTable instancesTable) {	
		if (instancesTable != null) {
			this.instancesTable = instancesTable;
			
			// Adding the table titles by default
			this.instancesTable.setWidget(0, 0, new Label("Applications"));
			this.instancesTable.setWidget(0, 1, new Label("# Created Instances"));
			this.instancesTable.setWidget(0, 2, new Label("# Running Instances"));
		} else {
			throw new NullPointerException("The flextable to store the number of instances is null");
		}
	} // end : constructor
	
	/**
	 * Main method to get the total number of instances handled by the WAL.
	 */	
	@Override
	public void run() {		
		
		WALRpcServiceManager.get().getInstancesNumber(new AsyncCallback<Map<String, int[]>>() {
			
			@Override
			public void onSuccess(Map<String, int[]> result) {	
				
				// Counter for the flex table iterator, it is 1, 
				// because the 0 was already used in the constructor.
				int tableIndex = 1; 
			  
				// Filling the table with the application names and number of instances
			    for (Map.Entry<String, int[]> instTableEntry : result.entrySet()) {
			    	
			        String appName 	 = instTableEntry.getKey();
			        int[] numOfInsts = instTableEntry.getValue();
			     			        
			        instancesTable.setWidget(tableIndex, 0, new Label(appName));
			        instancesTable.setWidget(tableIndex, 1, new Label(Integer.toString(numOfInsts[0])));
			        instancesTable.setWidget(tableIndex, 2, new Label(Integer.toString(numOfInsts[1])));
			        
					tableIndex++;
			    }	
			} 
		
			@Override
			public void onFailure(Throwable caught) {
				//instancesTable.setVisible(false); // ?				
				caught.printStackTrace(); // Show error details
			}
			
		}); // end : getInstancesNumber
		
	} // end : run method 	
	
}