package org.adiusframework.web.applicationlauncher.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * This interface should be implemented when is required to create a custom
 * event for stopping an application's instance.
 *
 * @author Tobias Meisen
 */
public interface InstanceStoppedEventHandler extends EventHandler {

	/**
	 * Useful when a class wants to implement behavior after a
	 * object's instance has stopped.
	 * 
	 * @param event 
	 * 		  Event related with the instance termination.
	 */	
	public abstract void onInstanceStopped(InstanceStoppedEvent event);

}