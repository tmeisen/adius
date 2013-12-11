package org.adiusframework.processmanager;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.query.Query;
import org.adiusframework.resource.Resource;
import org.adiusframework.service.StandardServiceResult;
import org.adiusframework.service.xml.ResourceRequirement;
import org.adiusframework.util.IsConfigured;
import org.adiusframework.util.datastructures.SystemData;

/**
 * A ResourceHandler enables the possibility to save and find information,
 * represented by Resource objects, which are related to a specific
 * ServiceProcess. Therefore it acts as a gateway to a ResourceManager object.
 */
public interface ResourceHandler extends IsConfigured {

	/**
	 * Creates any information which are related to the given ServiceProcess and
	 * stores them. They are associated with an uri, which is given by the
	 * ServiceProcess.
	 * 
	 * @param process
	 *            The given ServiceProcess
	 * @param query
	 *            The given Query
	 * @throws ProcessManagerException
	 *             if the registration of the resources failed.
	 */
	public abstract void registerResources(ServiceProcess process, Query query) throws ProcessManagerException;

	/**
	 * Registers any information which are attached to a StandardServiceResult
	 * in the context of the given ServiceProcess.
	 * 
	 * @param process
	 *            The given ServiceProcess.
	 * @param result
	 *            The StandardServiceResult which contains information.
	 * @throws ProcessManagerException
	 *             if the registration of the resources failed.
	 */
	public abstract void registerResources(ServiceProcess process, StandardServiceResult result)
			throws ProcessManagerException;

	/**
	 * Tries to find a registered Resource which is defined by a given
	 * RELExpression.
	 * 
	 * @param process
	 *            The ServiceProcess in which context the Resource is
	 *            registered.
	 * @param relExpr
	 *            The RELExpression which defines a Resource.
	 * @return The value of the Resource or null if the Resource couldn't been
	 *         found.
	 * @throws ProcessManagerException
	 *             if the replacement of the expression fails.
	 */
	public abstract String replaceRELExpression(ServiceProcess process, String relExpr) throws ProcessManagerException;

	/**
	 * Releases all resources which are related to the context of the given
	 * ServiceProcess.
	 * 
	 * @param process
	 *            The given ServiceProcess.
	 * @throws ProcessManagerException
	 *             if the release of the resources failed.
	 */
	public abstract void releaseResources(ServiceProcess process) throws ProcessManagerException;

	/**
	 * Tries to find a Resource, which is related to a given ServiceProcess and
	 * matches a given ResourceRequirement and the specified SystemData.
	 * 
	 * @param process
	 *            The ServiceProcess in which context the Resource is located.
	 * @param rr
	 *            The ResourceRequierement which should be fulfilled.
	 * @param systemData
	 *            The SystemData which are needed.
	 * @return The Resource if it is found.
	 * @throws ProcessManagerException
	 *             if the search of the resource has failed.
	 */
	public Resource findResource(ServiceProcess process, ResourceRequirement rr, SystemData systemData)
			throws ProcessManagerException;

	/**
	 * Updates all resources which are related to the context of the given
	 * ServiceProcess.
	 * 
	 * @param process
	 *            The given ServiceProcess.
	 * @throws ProcessManagerException
	 *             if the update of the resources fails.
	 */
	public abstract void updateResources(ServiceProcess process) throws ProcessManagerException;
}