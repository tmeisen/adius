package org.adiusframework.processmanager;

import org.adiusframework.util.IsConfigured;

/**
 * A ServiceProcessDefinitionFinder is used to store a mapping between a
 * process-type as a String and the ServiceProcessDefinition.
 */
public interface ServiceProcessDefinitionFinder extends IsConfigured {

	/**
	 * Tries to find a ServiceProcessDefinition by the given process-type.
	 * 
	 * @param type
	 *            A String representing the given process-type.
	 * @param domain
	 *            the domain of the process that have to be searched, can be set
	 *            to <code>null</code>
	 * @return The ServiceProcessDefiniton if it is found, null otherwise.
	 */
	public ServiceProcessDefinition find(String type, String domain);

}
