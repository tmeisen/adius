package org.adiusframework.web.applicationlauncher.client;

import org.adiusframework.web.applicationlauncher.client.component.LogTimer;
import org.adiusframework.web.applicationlauncher.client.event.InstanceStoppedEvent;
import org.adiusframework.web.applicationlauncher.client.event.InstanceStoppedEventHandler;
import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Container for the UI that handles the operations over an instance created
 * with the application definition; with the help of a timer it will execute
 * repeatedly for reading log data produced by the application executed.
 * 
 * @author Tobias Meisen
 */
public class WALAppInstanceTab extends DockLayoutPanel {
	
	/** Id related with the application's instance. */
	private int identifier;
	
	/** Universally unique identifier associated with the application's instance. */
	private String uuid;
	
	/** Timer for activate the request for getting more log data. */
	Timer logTimer;
	
	/** UI component for initialize the application's instance. */
	Button btnStart;
	
	/** UI component for finish the application's instance execution. */
	Button btnStop;
	
	/** UI component to remove a created instance, it is attached in the instance header. */
	Button btnRemoveInst;

	/** Graphic area where log data is showed. */
	LogTextArea txtLogging;
	
	/** Data of the application definition. */
	private ApplicationDefinition appDef;

	/** CSS style for the instance tab name. */
	private static final String CSS_STYLE_NAME = "wal-application-instance-tab";
	/** CSS style for the instance's button. */
	private static final String CSS_BUTTON_STYLE_NAME = "wal-application-instance-button";
	/** CSS style for the logs in the text area. */
	private static final String CSS_LOG_STYLE_NAME = "wal-application-instance-log-output";	
	/** CSS style for the instance's label. */
	private static final String CSS_LABEL_STYLE_NAME = "wal-application-instance-label-output";	
	/** CSS style for the button bar. */
	private static final String CSS_BUTTON_BAR_STYLE_NAME = "wal-application-instance-button-bar";	
	/** CSS style for the remove created instance's button. */
	private static final String CSS_REMOVE_BUTTON_STYLE_NAME = "wal-application-remove-instance-button";

	
	/**
	 * Constructor that will initialize the components for starting and stopping an application's instance.
	 * 
	 * @param identifier Id associated with the application definition.  
	 * @param appDef Application definition from which instances will be created. 
	 */
	public WALAppInstanceTab(int identifier, ApplicationDefinition appDef) {
		super(Unit.EM);
	
		this.appDef = appDef;
		this.identifier = identifier;
		
		addStyleName(CSS_STYLE_NAME);		
		initComponents();

		// React on resizes of the window
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				redraw();
			}
		});
	} // end : constructor
	
	
	// PROTECTED METHODS __________________________________________________________________________
	// ********************************************************************************************

	/**
	 * Method to initialize and organize the UI components showed in the tab.
	 */	
	protected void initComponents() {
		Label lblOutput;
		FlowPanel panel;
		
		// Creates the north label
		lblOutput = new Label("Output");
		lblOutput.addStyleName(CSS_LABEL_STYLE_NAME);
		addNorth(lblOutput, 2.0); // N - DLPanel 

		// Add buttons align horizontal
		panel = new FlowPanel();
		panel.addStyleName(CSS_BUTTON_BAR_STYLE_NAME);

		// Creates START button and define behavior
		this.btnStart = new Button("Start", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				startInstance();
			}
		});
		this.btnStart.addStyleName(CSS_BUTTON_STYLE_NAME);
		panel.add(this.btnStart);
		this.btnStart.setEnabled(true);

		// Creates STOP button and define behavior
		this.btnStop = new Button("Stop", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				stopInstance();
			}
		});
		this.btnStop.addStyleName(CSS_BUTTON_STYLE_NAME);
		panel.add(this.btnStop);
		this.btnStop.setEnabled(false);
		
		// Creates REMOVE INSTANCE button
		this.btnRemoveInst = new Button("X");
		this.btnRemoveInst.addStyleName(CSS_REMOVE_BUTTON_STYLE_NAME);
		
		addSouth(panel, 4.0); // S - DLPanel 

		// Creates centered area for logging	
		this.txtLogging = new LogTextArea();  
		this.txtLogging.addStyleName(CSS_LOG_STYLE_NAME);
		this.txtLogging.setReadOnly(true);
		
		add(this.txtLogging); // Add - DLPanel		
		redraw();
	} // end : initComponents method 

	/**
	 * Calculates the new height of the logging.
	 */
	protected void redraw() {
		int height = txtLogging.getElement().getParentElement().getOffsetHeight() - 20;
		txtLogging.setHeight((height < 0 ? 0 : height) + "px");
	} // end : redraw method

	/**
	 * It starts the execution of a new instance with the information contain in the 
	 * application definition, it handles also the UI interaction.
	 */
	protected void startInstance() {

		// Check if an instance is currently running
		if (isStarted()) {
			return;
		}
		
		txtLogging.addLog("Start initiated...");
		
		btnStart.setEnabled(false);
		btnStop.setEnabled(false);
		btnRemoveInst.setEnabled(false);

		// Calling the service for execute the application's instance
		WALRpcServiceManager.get().executeAppInstance(this.appDef.getId(), new AsyncCallback<String>() {

			@Override
			public void onSuccess(String uuid) {				
				txtLogging.addLog("Start of application successful, identifiable using " + uuid);
				setUuid(uuid);
			
				// Starting the log forwarding timer
				logTimer = new LogTimer(getUuid(), txtLogging);
				logTimer.scheduleRepeating(500/*mls*/); 
				
				btnStop.setEnabled(true);
				btnRemoveInst.setEnabled(false);
			}

			@Override
			public void onFailure(Throwable caught) {							
				txtLogging.addLog("Start of application failed: " + caught.getMessage());
				btnStop.setEnabled(false);
				btnRemoveInst.setEnabled(true);
			}
		}); // end : executeAppInstance
		
	} // end : startInstance method

	/**
	 * It finishes the execution of the current instance firing the 
	 * instance stopped event.
	 */	
	protected void stopInstance() {

		// Checks if an instance is currently running
		if (isStarted()) {
			
			txtLogging.addLog("Stop initiated...");
			
			// Calling the service to shutdown the application instance
			WALRpcServiceManager.get().shutdownAppInstance(this.uuid, this.appDef.getId(), new AsyncCallback<Boolean>() {
			
				@Override
				public void onSuccess(Boolean result) {
					txtLogging.addLog("Shutdown of completed with result code: " + result);
					setUuid(null);
					
					logTimer.cancel();
					logTimer = null;
					
					fireEvent(new InstanceStoppedEvent(getIdentifier(), getAppDef()));
				}
				
				@Override
				public void onFailure(Throwable caught) {
					txtLogging.addLog("Shutdown of application failed: " + caught.getMessage());
					btnStop.setEnabled(true);
				}				
			}); // end : shutdownAppInstance
		}
					
		btnStop.setEnabled(false);
	} // end : stopInstance method
	
	
	// PUBLIC METHODS _____________________________________________________________________________
	// ********************************************************************************************
	
	/**
	 * Adds the proper handler with the event type for register the event's handler.
	 * 
	 * @param handler 
	 *        Event handler related when an instance stops
	 * @return Use to remove the handler
	 */	
	public HandlerRegistration addInstanceStoppedEventHandler(InstanceStoppedEventHandler handler) {
		return addHandler(handler, InstanceStoppedEvent.TYPE);
	} // end : addInstanceStoppedEventHandler method
	
	/**
	 * To know if the instance has been started.
	 * 
	 * @return True, if the instance was already started, false otherwise
	 */		
	public boolean isStarted() {
		return this.uuid != null;
	} // end : isStarted method	

	// GETTERS AND SETTERS ________________________________________________________________________
	// ********************************************************************************************
	
	/** @return The current identifier of the application instance. */
	public int getIdentifier() {
		return identifier;
	}
	
	/** @return The unique identifier of the application's instance. */
	public String getUuid() {
		return uuid;
	}

	/** @param uuid Universally unique identifier of the application's instance to set. */
	protected void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/** @return The application definition associated to this class. */
	public ApplicationDefinition getAppDef() {
		return appDef;
	}
	
}