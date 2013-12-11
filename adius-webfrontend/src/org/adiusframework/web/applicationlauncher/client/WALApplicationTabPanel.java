package org.adiusframework.web.applicationlauncher.client;

import java.util.Collection;

import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.TabLayoutPanel;

/**
 * Tab layout panel that contains all the application tabs for WAL.
 * 
 * @author Tobias Meisen
 */
public class WALApplicationTabPanel extends TabLayoutPanel {

	/**
	 * Constructor to initialize the tabs associated with each application definition.
	 * 
	 * @param appDefs
	 *  	  Total set of applications' definitions that will be handled for WAL.
	 */	
	public WALApplicationTabPanel(Collection<ApplicationDefinition> appDefs) {
		super(2.0, Unit.EM);

		addStyleName("wal-application-tab-panel");
		
		// Iteration to get the applications to create the tabs with the names containing it.
		for (ApplicationDefinition appDef : appDefs) {
			WALApplicationTab tab = new WALApplicationTab(appDef);
			add(tab, appDef.getDisplayName());
		}
		
		add(new WALInstancesTab(), "Application Instances"); // It shows the current handled instances in the server
		
	} // end : constructor

}