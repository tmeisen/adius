package org.adiusframework.query;

import java.util.Map;

public interface QueryFactory {

	/**
	 * Creates a new query using the passed parameters.
	 * 
	 * @param type
	 *            the type of the service process that has to be executed
	 * @param domain
	 *            the domain of the service process
	 * @param resources
	 *            the attached resources
	 * @param properties
	 *            the user-defined properties
	 * @return the created query or null, if the creation has failed
	 */
	public Query create(String type, String domain, final Map<String, String> resources,
			final Map<String, String> properties);

}
