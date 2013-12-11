package org.adiusframework.web.applicationlauncher.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.adiusframework.web.applicationlauncher.server.application.ApplicationManager;
import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;

/**
 * In this class the implementations of the server methods provided by the
 * service are located.
 * 
 * @author Tobias Meisen
 */
public class WALServerManager {

	/** Class variable of an instance of this class. */
	private static WALServerManager instance;

	/** Map to store all the applications definitions with their IDs. */
	private Map<String, ApplicationDefinition> appDefMap;

	/** Map to store all the applications managers with their UUIDs. */
	private Map<String, ApplicationManager> appManagerMap;

	/** Number of waits till a timeout during shutdown is identified. */
	private static final int TIMEOUT_COUNTER = 10;/* mls */

	/**
	 * Constructor that will initialize the application manager map.
	 */
	public WALServerManager() {
		this.appManagerMap = new HashMap<String, ApplicationManager>();
	} // end : constructor

	/**
	 * Assigns all the applications definitions to the map attribute in this
	 * class.
	 * 
	 * @param data
	 *            Map with all the applications definitions with their IDs.
	 */
	protected void setAppDefData(Map<String, ApplicationDefinition> data) {
		this.appDefMap = data;
	} // end : setAppDefData method

	// PUBLIC METHODS
	// _____________________________________________________________________________
	// ********************************************************************************************

	/**
	 * Get a reference from the instance of this class.
	 * 
	 * @return The currently stored instance of this class.
	 */
	public static WALServerManager get() {
		if (instance == null) {
			instance = new WALServerManager();
		}
		return instance;
	} // end : get method

	/**
	 * Useful to get all the applications definitions from the map attribute in
	 * this class, or a collection without elements if nothing is already
	 * stored.
	 * 
	 * @return All the application definitions, or an empty collection if
	 *         nothing was set.
	 */
	public Collection<ApplicationDefinition> getAppDefData() {
		if (this.appDefMap == null) {
			return new ArrayList<ApplicationDefinition>();
		}
		return new ArrayList<ApplicationDefinition>(this.appDefMap.values());
	} // end : getAppDefData method

	/**
	 * Search for the server-side definition of the provided id to execute a
	 * manager.
	 * 
	 * @param appId
	 *            Application id wanted to be executed.
	 * @return The unique universal id indicating that the instance was executed
	 * @throws NullPointerException
	 *             if the provided appId is <code>null</code>
	 */
	public String executeAppInstance(String appId) {
		if (appId != null) {
			final ApplicationDefinition appDef = appDefMap.get(appId);
			final ApplicationManager manager = ApplicationManagerFactory.createManager(appDef);
			final String uuid = UUID.randomUUID().toString();

			appManagerMap.put(uuid, manager);
			manager.execute(true);

			// +1 running instance
			InstanceSupervisor.getInstance().incrInstances(appId, false);

			return uuid;
		} else {
			throw new NullPointerException("AppId cannot be set to null");
		}
	} // end : executeAppInstance method

	/**
	 * Searches for the server-side instances counter of the provided
	 * application id to be returned to the client.
	 * 
	 * @param appId
	 *            Application id from which the number of instances want to
	 *            known.
	 * @return Map with the total number of instances associated with each
	 *         defined application.
	 */
	public Map<String, int[]> getInstancesNumber() {
		return InstanceSupervisor.getInstance().getTotalInstances();
	} // end : getInstancesNumber method

	/**
	 * Increments by one the number of created instances for an application.
	 * 
	 * @param appId
	 *            Application id from which the number of created instances want
	 *            to be increased.
	 */
	public void addCreatedInstance(String appId) {
		InstanceSupervisor.getInstance().incrInstances(appId, true);
	} // end : addCreatedInstance method

	/**
	 * Reduces by one the number of created instances for an application.
	 * 
	 * @param appId
	 *            Application id from which the number of created instances want
	 *            to be decreased.
	 */
	public void reduceCreatedInstance(String appId) {
		InstanceSupervisor.getInstance().decrInstances(appId, true);
	} // end : reduceCreatedInstance method

	/**
	 * It will finish the instance of an application applying the shutdown
	 * method, and waiting until a period of time for a successful termination.
	 * 
	 * @param uuid
	 *            Unique identifier of the application.
	 * @param appId
	 *            ID used to support the operations with the UUID.
	 * @return False if the application finish after the timeout counter, or,
	 *         there is no manager associated to the UUID.
	 */
	public boolean shutdownAppInstance(String uuid, String appId) {

		int count = 0; // Counter to compare with the timeout
		final ApplicationManager manager = appManagerMap.get(uuid); // Get the
																	// launcher

		// Elemental validation for the correct method execution
		if (manager == null || appId == null || (appId.isEmpty())) {
			return false;
		}

		manager.shutdown(); // !!Shutdown!!

		// Waits till the shutdown is completed or timed out
		while (!manager.isFinished() && (count < TIMEOUT_COUNTER)) {
			try {
				Thread.sleep(500/* mls */); // Because, we do not want to spam,
											// we wait
				count++; // some time till the next ask.
			} catch (InterruptedException ie) {
				// nothing to do 
			}
		}

		// check whether the manager has finished the execution (everythings fine) or not
		if (!manager.isFinished()) {
			
			// lets do it the hard way
			manager.destroy();
		}
		InstanceSupervisor.getInstance().decrInstances(appId, false); // -1 running instance
		return count < TIMEOUT_COUNTER;
	} // end : shutdownAppInstance method

	/**
	 * According to the channel, it will reads the next input from the
	 * application manager referenced by the UUID.
	 * 
	 * @param uuid
	 *            Unique identifier referencing an application manager.
	 * @param channel
	 *            Depending if it is input/error stream channel.
	 * @return If it was not found manager for the UUID, an empty list will be
	 *         returned, otherwise, the next line(s) returned from the output.
	 */
	public List<String> readAppOutput(String uuid, int channel) {

		final ApplicationManager manager = appManagerMap.get(uuid); // Get the
																	// launcher

		if (manager == null) {
			return new ArrayList<String>();
		}

		return manager.readOutput(channel);
	} // end : readAppOutput method

}