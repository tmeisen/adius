package org.adiusframework.service.planner;

import javaff.data.Plan;

import org.adiusframework.resource.serviceplan.ServiceProcessPlan;

public interface ServiceProcessParser {

	public ServiceProcessPlan parse(Plan plan, ServiceProcessPlan serviceProcess) throws PlanParseException;

}
