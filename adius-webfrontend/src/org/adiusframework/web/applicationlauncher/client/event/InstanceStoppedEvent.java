package org.adiusframework.web.applicationlauncher.client.event;

import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Class to have a custom instance stopped event handler, associated with an
 * application definition.
 *
 * @author Tobias Meisen
 */
public class InstanceStoppedEvent extends GwtEvent<InstanceStoppedEventHandler> {
	
	/** Identification number of the instance associated with the application definition. */
	private final int instanceNumber;

	/** Application definition that it will stopped. */
	private final ApplicationDefinition appDef;
	
	/** Type related with the instance stopped event handler. */
	public static Type<InstanceStoppedEventHandler> TYPE = new Type<InstanceStoppedEventHandler>();

	/**
	 * Constructor that will initialize the components for starting and stopping an application's instance.
	 * 
	 * @param instanceNumber Id of the instance associated with the application definition.  
	 * @param appDef Data related with the running application. 
	 */
	public InstanceStoppedEvent(int instanceNumber, ApplicationDefinition appDef) {
		this.appDef = appDef;
		this.instanceNumber = instanceNumber;		
	} // end : constructor

	@Override
	protected void dispatch(InstanceStoppedEventHandler handler) {
		handler.onInstanceStopped(this);
	} // end : dispatch method
	
	@Override
	public GwtEvent.Type<InstanceStoppedEventHandler> getAssociatedType() {
		return TYPE;
	} // end : getAssociatedType method

	public int getInstanceNumber() {
		return instanceNumber;
	} // end : getInstanceNumber method
	
	public ApplicationDefinition getAppDef() {
		return appDef;
	} // end : getAppDef method

}