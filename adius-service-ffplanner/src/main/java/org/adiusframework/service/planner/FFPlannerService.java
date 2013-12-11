package org.adiusframework.service.planner;

import javaff.JavaFF;
import javaff.data.Plan;

import org.adiusframework.resource.ObjectResource;
import org.adiusframework.resource.StringResourceCapability;
import org.adiusframework.resource.pddl.PDDLDescription;
import org.adiusframework.resource.serviceplan.ServiceProcessPlan;
import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInstance;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.service.StandardServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FFPlannerService implements ServiceInstance {

	private static final Logger LOGGER = LoggerFactory.getLogger(FFPlannerService.class);

	private PDDLDescription pddlDescription;

	private ServiceProcessParser processParser;

	public PDDLDescription getPddlDescription() {
		return pddlDescription;
	}

	public void setPddlDescription(PDDLDescription pddlDescription) {
		this.pddlDescription = pddlDescription;
	}

	public ServiceProcessParser getProcessParser() {
		return processParser;
	}

	public void setProcessParser(ServiceProcessParser processParser) {
		this.processParser = processParser;
	}

	@Override
	public ServiceResult execute() throws ServiceException {
		try {
			Long startTime = System.currentTimeMillis();

			LOGGER.debug("Starting planning for NewVersion " + getPddlDescription().getProblems().size() + " problems.");

			// for each plan within the description a solution has to be
			// determined
			JavaFF planner = new JavaFF(getPddlDescription().getDomain());
			ServiceProcessPlan serviceProcessPlan = null;
			for (String problem : getPddlDescription().getProblems()) {

				// lets initialize the planning domain and the current problem
				// (the initialization of the planner in each loop has to be
				// done, because otherwise JavaFF seems to merge the existing
				// and the new problem
				Plan plan = planner.plan(problem);
				if (plan == null)
					throw new ServiceException(
							"Determination of plan failed, due to unknown reason. Check logger output.");

				// now we have to extend the service process plan by translating
				// the plan structure of the planner into a service compatible
				// plan of the framework
				serviceProcessPlan = getProcessParser().parse(plan, serviceProcessPlan);
			}

			// now we are finished
			LOGGER.debug("Result of planning:\n" + serviceProcessPlan);
			StandardServiceResult result = new StandardServiceResult();
			ObjectResource<ServiceProcessPlan> processResource = new ObjectResource<ServiceProcessPlan>(
					serviceProcessPlan);
			processResource.setCapability(new StringResourceCapability("service_process"));
			result.addResource(processResource);
			LOGGER.debug("Duration Planning " + (System.currentTimeMillis() - startTime));
			return result;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

}
