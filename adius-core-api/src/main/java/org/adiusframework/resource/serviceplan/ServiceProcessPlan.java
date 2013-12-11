package org.adiusframework.resource.serviceplan;

import java.io.Serializable;

public interface ServiceProcessPlan extends Serializable {

	public int getLength();

	public ServiceTemplate getServiceTemplate(int index);

}
