package org.adiusframework.web.applicationlauncher.client;

import java.util.Collection;

import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point of the Web Application Launcher, it will get all the information
 * of the applications available, and it will load the UI components.
 * 
 * @author Tobias Meisen
 */
public class WALEntryPoint implements EntryPoint {

	/** Prefix for CSS styles of the application. */
	public static final String CSS_APP_PREFIX = "wal-";
	/** CSS style for the root panel of WAL.*/
	protected static final String CSS_ROOT_PANEL = CSS_APP_PREFIX + "root-panel";
	/** CSS style for the tab panel of WAL.*/
	protected static final String CSS_TAB_PANEL = CSS_APP_PREFIX + "tab-panel";

	/**
	 * Entry point method for loading the applications definitions and show 
	 * information about it to a new client.
	 */	
	@Override
	public void onModuleLoad() {

		// Create the panels (adding a root panel to adjust inner containers)
		RootPanel 		rootPanel 	= RootPanel.get(null);		
		final FlowPanel outerPanel 	= new FlowPanel();
		final HTML 		infoLabel 	= new HTML("Loading...");
		
		outerPanel.addStyleName(CSS_ROOT_PANEL);
		outerPanel.add(infoLabel);

		// Get data to initialize the inner panel, it was previously loaded by the server's listener
		WALRpcServiceManager.get().getAppDefs(new AsyncCallback<Collection<ApplicationDefinition>>() {

			@Override
			public void onSuccess(Collection<ApplicationDefinition> result) {
				
				// The inner application panel
				if (result.size() > 0) {
					WALApplicationTabPanel applicationPanel = new WALApplicationTabPanel(result);
					applicationPanel.addStyleName(CSS_TAB_PANEL);
					
					outerPanel.remove(infoLabel);					
					outerPanel.add(applicationPanel);
				} else {
					infoLabel.setHTML("No application configured...");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				infoLabel.setHTML(caught.getMessage());
			}
		}); // end : getAppDefs
		
		rootPanel.add(outerPanel, 0, 0); // All the UI assembled together
		
	} // end : onModuleLoad method 
	
}