package org.adiusframework.processmanager.exception;

import org.adiusframework.processmanager.domain.ServiceProcess;

/**
 * The IsServiceProcessRelated interface is used to show that something is
 * related to a special ServiceProcess. Moreover it is possible to access this
 * special ServiceProcess.
 */
public interface IsServiceProcessRelated {

	/**
	 * Return the ServiceProcess that relates to this object.
	 * 
	 * @return The related ServiceProcess.
	 */
	public ServiceProcess getServiceProcess();

}
