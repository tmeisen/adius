package org.adiusframework.web.applicationlauncher.client;

import org.adiusframework.web.applicationlauncher.client.component.InstanceTimer;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;

/**
 * In this tab a table is rendered to show all the created and
 * running instances handled by the server, each five seconds
 * the data is updated due the use of GWT timer.
 * 
 * @author Nelson Fernandez
 */
public class WALInstancesTab extends DockLayoutPanel {

	/** Timer for activate the request to get the number of instances. */
	Timer instanceTimer;
	
	/** Graphic area where the number of instances is showed. */
	FlexTable instancesTable;

	/**
	 * Default Constructor to do the proper initialization of components.
	 */	
	public WALInstancesTab() {
		super(Unit.EM);
		
		this.instancesTable = new FlexTable();
		
		addStyleName("wal-application-tab");
		initComponents();
		retrieveData();
	} // end : constructor

	
	// PROTECTED METHODS __________________________________________________________________________
	// ********************************************************************************************

	/**
	 * Method to initialize and organize the UI components showed in the tab.
	 */
	protected void initComponents() {		
		add(this.instancesTable); // More UI elements could be added ++		
	} // end : initComponents method 
	
	
	// PUBLIC METHODS _____________________________________________________________________________
	// ********************************************************************************************
	
	/** 
	 * It will activate the timer to retrieve the information about
	 * the number of instances each five seconds. 
	 */
	public void retrieveData() {		
		instanceTimer = new InstanceTimer(instancesTable);
		instanceTimer.scheduleRepeating(5000/*mls*/);	
	} // end : retrieveData method 
	
}