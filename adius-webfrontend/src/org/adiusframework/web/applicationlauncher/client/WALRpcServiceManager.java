package org.adiusframework.web.applicationlauncher.client;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;
import org.adiusframework.web.applicationlauncher.shared.WALService;
import org.adiusframework.web.applicationlauncher.shared.WALServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Class designed to organize and handle the methods invoked from the
 * WALService class.
 * 
 * @author Tobias Meisen
 */
public class WALRpcServiceManager {

	/** It stores the asynchronous object reference to the WALService class. */
	private WALServiceAsync service = GWT.create(WALService.class);

	/** Class variable of an instance of this class. */
	private static WALRpcServiceManager instance;
	
	
	// PUBLIC METHODS _____________________________________________________________________________
	// ********************************************************************************************

	/**
	 * Get a reference from the instance of this class.
	 * 
	 * @return The currently stored instance of this class.
	 */
	public static WALRpcServiceManager get() {
		if (instance == null) {
			instance = new WALRpcServiceManager();
		}
		return instance;
	} // end : get method 

	/** Invocation of the method getAppDefs from the WALService. */
	public void getAppDefs(AsyncCallback<Collection<ApplicationDefinition>> callback) {
		service.getAppDefs(callback);
	} // end : getAppDefs method 

	/** Invocation of the method executeAppInstance from the WALService. */
	public void executeAppInstance(String appId, AsyncCallback<String> callback) {
		service.executeAppInstance(appId, callback);
	} // end : executeAppInstance method 
		
	/** Invocation of the method getInstancesNumber from the WALService. */
	public void getInstancesNumber(AsyncCallback<Map<String, int[]>> callback) {
		service.getInstancesNumber(callback);
	} // end : getInstancesNumber method	
	
	/** Invocation of the method addCreatedInstance from the WALService. */ 
	public void addCreatedInstance(String appId, AsyncCallback<Void> callback) {
		service.addCreatedInstance(appId, callback);
	} // end : addCreatedInstance method	
	
	/** Invocation of the method reduceCreatedInstance from the WALService. */ 
	public void reduceCreatedInstance(String appId, AsyncCallback<Void> callback) {
		service.reduceCreatedInstance(appId, callback);
	} // end : reduceCreatedInstance method	
	
	/** Invocation of the method shutdownAppInstance from the WALService. */
	public void shutdownAppInstance(String uuid, String appId, AsyncCallback<Boolean> callback) {
		service.shutdownAppInstance(uuid, appId, callback);
	} // end : shutdownAppInstance method 

	/** Invocation of the method readLogData from the WALService. */
	public void readLogData(String uuid, int channel, AsyncCallback<List<String>> callback) {
		service.readLogData(uuid, channel, callback);
	} // end : readLogData method 

}