package org.adiusframework.web.applicationlauncher.client.component;

import java.util.List;

import org.adiusframework.web.applicationlauncher.client.LogTextArea;
import org.adiusframework.web.applicationlauncher.client.WALRpcServiceManager;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Timer that will request for more log data entries to the server,
 * to render it in the user interface.
 * 
 * @author Tobias Meisen
 */
public class LogTimer extends Timer {

	/** Universally unique identifier associated with the application's instance. */
	private String uuid;
	
	/** UI to show all the log data. */
	LogTextArea logTextArea;

	/**
	 * Constructor of the log text area with definable capacity.
	 * 
	 * @param uuid 
	 * 		  Unique identifier of the application's instance.
	 * @param logTextArea
	 *        Graphic component to show the output data. 
	 */	
	public LogTimer(String uuid, LogTextArea logTextArea) {
		this.uuid = uuid;
		this.logTextArea = logTextArea;		
	} // end : constructor
		
	/**
	 * Main method to execute the service for receive log data from the application.
	 */	
	@Override
	public void run() {		
		
		WALRpcServiceManager.get().readLogData(this.uuid, 0, new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {				
				for (String entry : result) {
					logTextArea.addLog(entry);
				}
			}
		
			@Override
			public void onFailure(Throwable caught) {
				logTextArea.addLog("Exception: " + caught);
			}
		}); // end : readLogData 
		
	} // end : run method 
	
}