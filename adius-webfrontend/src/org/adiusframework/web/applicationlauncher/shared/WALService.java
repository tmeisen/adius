package org.adiusframework.web.applicationlauncher.shared;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 * 
 * @author Tobias Meisen
 */
@RemoteServiceRelativePath("rpc")
public interface WALService extends RemoteService {
	
	/**
	 * Behavior defined to be executed when a new instance in WAL is created.
	 * 
	 * @param appId
	 * 		  Application ID that was recently created, to know which counter increase.
	 */	
	public abstract void addCreatedInstance(String appId);

	/**
	 * Behavior defined after a instance is created, it will be executed.
	 * 
	 * @param appId
	 * 		  Application ID of the created instances that will be executed.
	 * @return The unique universal id indicating that the instance was executed.
	 */		
	public abstract String executeAppInstance(String appId);
	
	/**
	 * Necessary behavior useful when the timer will get all the number of
	 * currently created and running instances from the server.
	 * 
	 * @return Map with the total number of instances associated with each defined application.
	 */		
	public abstract Map<String, int[]> getInstancesNumber();
	
	/**
	 * Implementation to be executed when a created instance in WAL is removed from 
	 * the UI.
	 * 
	 * @param appId
	 * 		  Application ID that was created, to know which counter decrease.
	 */		
	public abstract void reduceCreatedInstance(String appId);
	
	/**
	 * Implementation defined to get all the applications' definitions contained 
	 * in the server.
	 * 
	 * @return All the application definitions, or an empty collection if nothing was set.
	 */		
	public abstract Collection<ApplicationDefinition> getAppDefs();
	
	/**
	 * Behavior executed after an application instance is executed, this method 
	 * will be useful to get all the data read from the stream, and then show 
	 * it into the UI. 
	 * 
	 * @param uuid
	 * 		  Unique identifier of an application from which the data will be read.
	 * @param channel
	 * 		  Channel related to the specified stream that contains the required data.
	 * @return If it was not found the required data, an empty list will be 
	 * 		   returned, otherwise, the next piece of data obtained from the output.	
	 */		
	public abstract List<String> readLogData(String uuid, int channel);

	/**
	 * Behavior triggered when an application instance is executed, this method 
	 * will be useful to get all the data read from the stream, and then show it 
	 * into the UI. 
	 * 
	 * @param uuid
	 * 		  Unique identifier of an application that will be shutdown.
	 * @param appId
	 * 		  Information container that will help in the shutdown process.
	 * @return False if the application finish before it was expected, or, there is no data associated to the instance.
	 */		
	public abstract boolean shutdownAppInstance(String uuid, String appId);
	
}