package org.adiusframework.web.applicationlauncher.client;

import java.util.HashMap;
import java.util.Map;

import org.adiusframework.web.applicationlauncher.client.event.InstanceStoppedEvent;
import org.adiusframework.web.applicationlauncher.client.event.InstanceStoppedEventHandler;
import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.StackLayoutPanel;

/**
 * Tab that contains the loaded information for executing instances of an
 * application handled by WAL.
 * 
 * @author Tobias Meisen
 */
public class WALApplicationTab extends DockLayoutPanel {

	/**
	 * It refers to the number of instances created until now, this number
	 * always grows.
	 */
	private int instUiCounter;

	/**
	 * The maximum number of instances that could be created for this
	 * application.
	 */
	private int maxNumInstances;

	/** It stores the number of current active instances of an application. */
	private Integer numInstCounter;

	/** Button useful for the creation of a new application's instance. */
	private Button btnInstance;

	/** Text show if the maximum number of instances is reached. */
	Label lblMaxInst;

	/** UI Component for locate all the application instances. */
	StackLayoutPanel slpAppInstances;

	/**
	 * Contains all the information needed for the execution of an application
	 * in this tab.
	 */
	private final ApplicationDefinition appDef;

	/**
	 * Structure to store all the instances of the associated application
	 * definition.
	 */
	private Map<Integer, WALAppInstanceTab> tabMap;

	/** CSS style for the instance's tab panel. */
	private static final String CSS_STYLE_NAME = "wal-application-instance-tab-panel";
	/** CSS style for the button that creates new instances. */
	private static final String CSS_BUTTON_STYLE_NAME = "wal-application-new-instance-button";
	/** CSS style for the instance's header. */
	private static final String CSS_INSTANCE_HEADER_STYLE_NAME = "wal-application-instance-header";

	/**
	 * Constructor to store the application definition and do the proper
	 * initialization of components.
	 * 
	 * @param appDef
	 *            Application definition that will be handled for this tab.
	 */
	public WALApplicationTab(ApplicationDefinition appDef) {
		super(Unit.EM);

		this.appDef = appDef;
		this.instUiCounter = 0;
		this.setNumInstCounter(0);
		this.setMaxNumInstances(appDef.getInstanceMax());
		this.tabMap = new HashMap<Integer, WALAppInstanceTab>();

		addStyleName("wal-application-tab");
		initComponents();
	} // end : constructor

	// PROTECTED METHODS
	// __________________________________________________________________________
	// ********************************************************************************************

	/**
	 * @return the current number of instances
	 */
	protected Integer getNumInstCounter() {
		return numInstCounter;
	}

	/**
	 * @param numInstCounter
	 *            the value that has to be set as the number of existing
	 *            instances
	 */
	protected void setNumInstCounter(Integer numInstCounter) {
		this.numInstCounter = numInstCounter;
	}

	/**
	 * @return the maximum number of possible instances
	 */
	protected int getMaxNumInstances() {
		return maxNumInstances;
	}

	/**
	 * @param maxNumInstances
	 *            the maximum number of instances to set
	 */
	protected void setMaxNumInstances(int maxNumInstances) {
		this.maxNumInstances = maxNumInstances;
	}

	/**
	 * Method to initialize and organize the UI components showed in the tab.
	 */
	protected void initComponents() {

		// Add the creation button
		btnInstance = new Button("Create New Instance (" + this.appDef.getInstanceMax() + " Max)");

		lblMaxInst = new Label("No more instances, maximum number reached.");
		lblMaxInst.setVisible(false);

		btnInstance.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				createNewInstance();
			}
		});

		HorizontalPanel buttonPanel = new HorizontalPanel();

		buttonPanel.add(btnInstance);
		buttonPanel.add(lblMaxInst);
		buttonPanel.addStyleName(CSS_BUTTON_STYLE_NAME);

		addNorth(buttonPanel, 3.3);

		// Add the instance area
		slpAppInstances = new StackLayoutPanel(Unit.EM) {

			@Override
			public void animate(int duration) {
				super.animate(duration, new AnimationCallback() {

					private double previousDouble = -1.0;

					@Override
					public void onLayout(Layer layer, double progress) {
						if ((int) (previousDouble * 100) / 10 != (int) (progress * 100) / 10) {
							previousDouble = progress;
							redraw();
						}
					}

					@Override
					public void onAnimationComplete() {
						previousDouble = -1.0;
						redraw();
					}
				});
			}

			/**
			 * Redraws the UI component that stores all the application
			 * instances.
			 */
			protected void redraw() {
				int index = slpAppInstances.getVisibleIndex();

				if (index > -1) {
					WALAppInstanceTab appInstanceTab = (WALAppInstanceTab) slpAppInstances.getWidget(index);
					appInstanceTab.redraw();
				}
			}

		};

		slpAppInstances.addStyleName(CSS_STYLE_NAME);
		slpAppInstances.setVisible(false);

		add(slpAppInstances);
	} // end : initComponents method

	/**
	 * It starts the creation of a new instance with the information contain in
	 * the application definition, and the it will stored in the instances' map.
	 */
	protected void createNewInstance() {

		final Label instLbl; // Instance header label
		final HorizontalPanel instanceHeader = new HorizontalPanel(); // Container
																		// for
																		// the
																		// label
																		// and
																		// the
																		// remove
																		// instance
																		// button

		// If the maximun number of instaces is exceed, it won't create nothing
		if (getNumInstCounter() + 1 > getMaxNumInstances()) {
			lblMaxInst.setVisible(true);
			return;
		}

		// Increases the client counters
		setNumInstCounter(getNumInstCounter() + 1);
		instUiCounter++;

		// Create new tab and register it in the map
		final WALAppInstanceTab appInstanceTab = new WALAppInstanceTab(instUiCounter, appDef);

		// -> Triggered event when an instance is going to be removed
		appInstanceTab.addInstanceStoppedEventHandler(new InstanceStoppedEventHandler() {

			@Override
			public void onInstanceStopped(InstanceStoppedEvent event) {

				final ApplicationDefinition appDef = event.getAppDef();

				// Verify that the event concerns this tab manager
				if (!getAppDef().equals(appDef)) {
					return;
				}

				setNumInstCounter(getNumInstCounter() - 1); // Decrease the
															// number of current
															// instances

				// Removes the maximum instance label if one instance is deleted
				if (getNumInstCounter() == (getMaxNumInstances() - 1)) {
					lblMaxInst.setVisible(false);
				}

				removeTab(event.getInstanceNumber());
			}
		});

		// -> Event for removing a created instance from the application
		appInstanceTab.btnRemoveInst.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Button currentInstBtn = (Button) event.getSource(); // Current
																	// pressed
																	// button
				Integer instanceIndex = (Integer) currentInstBtn.getLayoutData();

				removeCreatedInstance(instanceIndex);
			}
		});
		appInstanceTab.btnRemoveInst.setLayoutData(instUiCounter); // !!Used to
																	// store the
																	// instance
																	// number(index)!!

		// Adding the UI header components of the instance
		instLbl = new Label(appDef.getDisplayName() + " (Instance #" + instUiCounter + ")");
		instanceHeader.addStyleName(CSS_INSTANCE_HEADER_STYLE_NAME);
		instanceHeader.add(instLbl);
		instanceHeader.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		instanceHeader.add(appInstanceTab.btnRemoveInst);

		// Adding all together, instance header consist of a label, and the
		// remove instance button.
		tabMap.put(instUiCounter, appInstanceTab); // <------ New tab stored
		slpAppInstances.add(appInstanceTab, instanceHeader, 2.0); // Was 2.5

		// Notify to the server that one created instance was added.
		WALRpcServiceManager.get().addCreatedInstance(getAppDef().getId(), new AsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				// TODO: add created instance should return a result
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO: handle failures of a call (e.g. show the error in a
				// label)
			} // +1 CI
		}); // end : addCreatedInstance

		// Now add the tab to the panel and set it to visible
		if (!slpAppInstances.isVisible()) {
			slpAppInstances.setVisible(true);
		}
	} // end : createNewInstance method

	/**
	 * Method that return the application definition contained in the tab.
	 * 
	 * @return The current application definition associated to the application
	 *         tab.
	 */
	protected ApplicationDefinition getAppDef() {
		return appDef;
	} // end : getAppDef

	// PUBLIC METHODS
	// _____________________________________________________________________________
	// ********************************************************************************************

	/**
	 * Removes an application's instance from the tab map.
	 * 
	 * @param instanceNumber
	 *            Instance's number for search in the map.
	 */
	public void removeTab(final int instanceNumber) {
		// Get the tab, remove it and update the map
		final WALAppInstanceTab appInstanceTab = tabMap.get(instanceNumber);

		if (slpAppInstances.remove(appInstanceTab)) {
			tabMap.remove(instanceNumber);

			// Notify to the server that one created instance was removed.

			WALRpcServiceManager.get().reduceCreatedInstance(getAppDef().getId(), new AsyncCallback<Void>() {

				@Override
				public void onSuccess(Void result) {
					// TODO: reduce created instance should return a result
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO: handle failures of a call (e.g. show the error in a
					// label)
				} // -1 CI
			}); // end : reduceCreatedInstance

			if (slpAppInstances.getWidgetCount() == 0) {
				slpAppInstances.setVisible(false);
			}
		}
	} // end : removeTab method

	/**
	 * Removes a stack from the current application tab and clear the selected
	 * application instance.
	 * 
	 * @param instanceNumber
	 *            Instance's number for search in the map.
	 */
	public void removeCreatedInstance(final int instanceNumber) {
		// If the maximum number of instances was reached, for instance, this
		// label won't be visible.
		if (lblMaxInst.isVisible()) {
			lblMaxInst.setVisible(false);
		}

		removeTab(instanceNumber); // Remove the UI instance element
		setNumInstCounter(getNumInstCounter() - 1); // Decreases the client
													// counter
	} // end : removeCreatedInstance method

}