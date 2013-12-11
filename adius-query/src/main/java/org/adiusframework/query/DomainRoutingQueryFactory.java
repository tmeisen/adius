package org.adiusframework.query;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainRoutingQueryFactory implements QueryFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(DomainRoutingQueryFactory.class);

	private ConcurrentMap<String, QueryFactory> mapping;

	public ConcurrentMap<String, QueryFactory> getMapping() {
		return mapping;
	}

	public void setMapping(ConcurrentMap<String, QueryFactory> mapping) {
		this.mapping = mapping;
	}

	@Override
	public Query create(String type, String domain, Map<String, String> resources, Map<String, String> properties) {
		LOGGER.debug("Checking for domain " + domain);
		if (mapping.containsKey(domain)) {
			return mapping.get(domain).create(type, domain, resources, properties);
		}
		LOGGER.debug("Query factory of " + domain + " not found");
		return null;
	}

}
