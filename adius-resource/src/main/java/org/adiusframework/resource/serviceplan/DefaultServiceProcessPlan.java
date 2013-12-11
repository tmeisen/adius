package org.adiusframework.resource.serviceplan;

import java.util.List;
import java.util.Vector;

public class DefaultServiceProcessPlan implements ServiceProcessPlan {
	private static final long serialVersionUID = 3535855726897986918L;

	private List<ServiceTemplate> templates;

	public DefaultServiceProcessPlan() {
		templates = new Vector<ServiceTemplate>();
	}

	public DefaultServiceProcessPlan(ServiceProcessPlan baseProcessPlan) {
		this();
		if (baseProcessPlan != null) {
			System.out.println(baseProcessPlan);
			for (int i = 0; i < baseProcessPlan.getLength(); i++) {
				addServiceTemplate(baseProcessPlan.getServiceTemplate(i));
			}
		}
	}

	@Override
	public int getLength() {
		return templates.size();
	}

	@Override
	public ServiceTemplate getServiceTemplate(int index) {
		if (index < 0 || index >= templates.size())
			return null;
		return templates.get(index);
	}

	public void addServiceTemplate(ServiceTemplate template) {
		templates.add(template);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < getLength(); i++) {
			builder.append(i).append(":").append(getServiceTemplate(i).toString());
			if (i < getLength() - 1)
				builder.append("\n");
		}
		return builder.toString();
	}

}
