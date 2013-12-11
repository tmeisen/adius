package org.adiusframework.processmanager;

import org.adiusframework.processmanager.xml.TaskType;

/**
 * The ServiceProcessDefinition interface describes a Process as a list of
 * consecutive tasks.
 */
public interface ServiceProcessDefinition {

	/**
	 * Determines the type of the next task to a given type which is represented
	 * by a String.
	 * 
	 * @param type
	 *            The String representing the base type.
	 * @return The following TaskType.
	 */
	public TaskType getNextTask(String type);

	/**
	 * @param type
	 *            The type which should by examined.
	 * @return Returns true, if the given type is the last one in this Process.
	 */
	public boolean isFinalTask(String type);

	/**
	 * Checks if this process definition defines a process that only accesses
	 * data.
	 * 
	 * @return true, if only data is accessed by the defined process.
	 */
	public boolean isDataAccessor();

	/**
	 * @return the type of the service process
	 */
	public String getType();

	/**
	 * @return the domain of the service process
	 */
	public String getDomain();

}
