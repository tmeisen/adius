package org.adiusframework.processmanager;

import java.util.concurrent.ConcurrentMap;

import org.adiusframework.processmanager.AbstractResourceHandler;
import org.adiusframework.processmanager.exception.InvalidDomainException;

public class DomainRoutingResourceHandler extends AbstractResourceHandler {

	private ConcurrentMap<String, DomainEntityUriGenerator> mapping;

	public ConcurrentMap<String, DomainEntityUriGenerator> getMapping() {
		return mapping;
	}

	public void setMapping(ConcurrentMap<String, DomainEntityUriGenerator> mapping) {
		this.mapping = mapping;
	}

	@Override
	protected String generateEntityResourceUri(String domain, Integer entityId) throws InvalidDomainException {
		if (!getMapping().containsKey(domain))
			throw new InvalidDomainException(domain);
		return getMapping().get(domain).generateUri(entityId);
	}

}
