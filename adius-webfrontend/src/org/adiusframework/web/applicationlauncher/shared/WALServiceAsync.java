package org.adiusframework.web.applicationlauncher.shared;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The asynchronous interface is always used for the client
 * when a required service in WAL must be called. 
 *
 * @author Tobias Meisen
 */
public interface WALServiceAsync {
	
	/**
	 * Always it will be executed when a new instance in WAL is created, implement
	 * complementary behavior for the proper counter increasing on the server.
	 * 
	 * @param appId
	 * 		  Application ID that was recently created, to know which counter increase.
	 * @param callback
	 *        Object to the service proxy class, its generic type is Void like the server method return type.
	 */	
	void addCreatedInstance(String appId, AsyncCallback<Void> callback);
	
	/**
	 * Necessary when using the instances timer to get all the number of
	 * currently created and running instances from the server.
	 * 
	 * @param callback
	 *        Object to the service proxy class, its generic type is a Map like the server method return type.
	 */		
	void getInstancesNumber(AsyncCallback< Map<String, int[]>> callback);
	
	/**
	 * After a instance is created, it is ready to be executed, in the server
	 * the number of running instances will be increased by one.
	 * 
	 * @param appId
	 * 		  Application ID of the created instances that will be executed.
	 * @param callback
	 *        Object to the service proxy class, its generic type is String like the server method return type.
	 */		
	void executeAppInstance(String appId, AsyncCallback<String> callback);
	
	/**
	 * Always it will be executed when a created instance in WAL is removed from 
	 * the UI, implement complementary behavior for the proper counter decreasing 
	 * on the server.
	 * 
	 * @param appId
	 * 		  Application ID that was created, to know which counter decrease.
	 * @param callback
	 *        Object to the service proxy class, its generic type is Void like the server method return type.
	 */		
	void reduceCreatedInstance(String appId, AsyncCallback<Void> callback);	
	
	/**
	 * Necessary to get all the applications' definitions contained in the
	 * server.
	 * 
	 * @param callback
	 *        Object to the service proxy class, its generic type is a Collection like the server method return type.
	 */		
	void getAppDefs(AsyncCallback<Collection<ApplicationDefinition>> callback);
	
	/**
	 * After an application instance is executed, this method will be useful
	 * to get all the data read from the stream, and then show it into the UI. 
	 * 
	 * @param uuid
	 * 		  Unique identifier of an application from which the data will be read.
	 * @param channel
	 * 		  Channel related to the specified stream that contains the required data.
	 * @param callback
	 *        Object to the service proxy class, its generic type is List<String> like the server method return type.
	 */		
	void readLogData(String uuid, int channel, AsyncCallback<List<String>> callback);

	/**
	 * After an application instance is executed, this method will be useful
	 * to get all the data read from the stream, and then show it into the UI. 
	 * 
	 * @param uuid
	 * 		  Unique identifier of an application that will be shutdown.
	 * @param appId
	 * 		  Information container that will help in the shutdown process.
	 * @param callback
	 *        Object to the service proxy class, its generic type is Boolean like the server method return type.
	 */		
	void shutdownAppInstance(String uuid, String appId, AsyncCallback<Boolean> callback);
	
}