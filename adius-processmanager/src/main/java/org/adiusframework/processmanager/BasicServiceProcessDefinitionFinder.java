package org.adiusframework.processmanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The BasicServiceProcessDefinitionFinder implements a HashMap to map
 * process-types to its corresponding ServiceProcessDefinition objects.
 */
public class BasicServiceProcessDefinitionFinder implements ServiceProcessDefinitionFinder {

	private Map<ServiceProcessDefinitionKey, ServiceProcessDefinition> definitions;

	/**
	 * Creates a new BasicServiceProcessDefinitionFinder with no definitions.
	 */
	public BasicServiceProcessDefinitionFinder() {
		definitions = new HashMap<ServiceProcessDefinitionKey, ServiceProcessDefinition>();
	}

	@Override
	public boolean checkConfiguration() {
		return definitions != null;
	}

	public void add(ServiceProcessDefinition definition) {
		definitions.put(new ServiceProcessDefinitionKey(definition.getType(), definition.getDomain()), definition);
	}

	public void addAll(List<ServiceProcessDefinition> definitions) {
		for (ServiceProcessDefinition definition : definitions) {
			add(definition);
		}
	}

	@Override
	public ServiceProcessDefinition find(String type, String domain) {
		ServiceProcessDefinitionKey key = new ServiceProcessDefinitionKey(type, domain);
		if (definitions.containsKey(key)) {
			return definitions.get(key);
		}

		// if we reach this point a more common definition is searched
		key = new ServiceProcessDefinitionKey(type, null);
		if (definitions.containsKey(key)) {
			return definitions.get(key);
		} else {
			return null;
		}
	}

	private class ServiceProcessDefinitionKey {

		private String type;

		private String domain;

		public ServiceProcessDefinitionKey(String type, String domain) {
			this.type = type;
			this.domain = domain;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((domain == null) ? 0 : domain.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ServiceProcessDefinitionKey other = (ServiceProcessDefinitionKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (domain == null) {
				if (other.domain != null)
					return false;
			} else if (!domain.equals(other.domain))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}

		private BasicServiceProcessDefinitionFinder getOuterType() {
			return BasicServiceProcessDefinitionFinder.this;
		}
	}

}
