package org.adiusframework.serviceprovider;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.adiusframework.service.xml.ServiceDefinition;

public class SpringServiceProvider extends AbstractServiceProvider {

	private List<ServiceDefinition> definitions;

	public List<ServiceDefinition> getDefinitions() {
		return definitions;
	}

	public void setDefinitions(List<ServiceDefinition> definitions) {
		this.definitions = definitions;
	}

	@Override
	@PostConstruct
	public void init() {
		registerAll(getDefinitions());
	}

	@PreDestroy
	public void destroy() {
		unregisterAll();
	}

}
