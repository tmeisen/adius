package org.adiusframework.web.applicationlauncher.server;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.adiusframework.web.applicationlauncher.shared.ApplicationDefinition;
import org.adiusframework.web.applicationlauncher.shared.WALService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side invocation of the implementations of the RPC services.
 * 
 * @author Tobias Meisen
 */
@SuppressWarnings("serial")
public class WALServiceImpl extends RemoteServiceServlet implements WALService {

	/** Invocation of the method getAppDefs from the WAL Server Manager. */
	@Override
	public Collection<ApplicationDefinition> getAppDefs() {
		return WALServerManager.get().getAppDefData();
	} // end : getAppDefs method

	/** Invocation of the method executeAppInstance from the WAL Server Manager. */
	@Override
	public String executeAppInstance(String appName) {
		return WALServerManager.get().executeAppInstance(appName);
	} // end : executeAppInstance method

	/** Invocation of the method getInstancesNumber from the WAL Server Manager. */
	@Override
	public Map<String, int[]> getInstancesNumber() {
		return WALServerManager.get().getInstancesNumber();
	}

	/** Invocation of the method addCreatedInstance from the WAL Server Manager. */
	@Override
	public void addCreatedInstance(String appName) {
		WALServerManager.get().addCreatedInstance(appName);
	} // end : addCreatedInstance method

	/**
	 * Invocation of the method reduceCreatedInstance from the WAL Server
	 * Manager.
	 */
	@Override
	public void reduceCreatedInstance(String appName) {
		WALServerManager.get().reduceCreatedInstance(appName);
	} // end : reduceCreatedInstance method

	/**
	 * Invocation of the method shutdownAppInstance from the WAL Server Manager.
	 */
	@Override
	public boolean shutdownAppInstance(String uuid, String appId) {
		return WALServerManager.get().shutdownAppInstance(uuid, appId);
	} // end : shutdownAppInstance method

	/** Invocation of the method readAppOutput from the WAL Server Manager. */
	@Override
	public List<String> readLogData(String uuid, int channel) {
		return WALServerManager.get().readAppOutput(uuid, channel);
	} // end : readLogData method

}