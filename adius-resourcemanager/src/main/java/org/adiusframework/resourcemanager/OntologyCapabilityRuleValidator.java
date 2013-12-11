package org.adiusframework.resourcemanager;

import java.util.Properties;

import org.adiusframework.ontology.resourceontology.ResourceOntologyManager;
import org.adiusframework.resource.ResourceCapability;
import org.adiusframework.resource.ResourceCapabilityRule;

/**
 * TODO javadoc
 */
public class OntologyCapabilityRuleValidator implements CapabilityRuleValidator {

	private ResourceOntologyManager ontologyManager;

	public ResourceOntologyManager getOntologyManager() {
		return ontologyManager;
	}

	public void setOntologyManager(ResourceOntologyManager ontologyManager) {
		this.ontologyManager = ontologyManager;
	}

	@Override
	public boolean checkConfiguration() {
		// if(ontologyManager == null)
		// return false;

		// TODO add if implemented
		return true; // ontologyManager.checkConfiguration();
	}

	@Override
	public boolean isSatisfied(ResourceCapabilityRule rule, Properties execConditions, ResourceCapability capability) {
		// TODO: Currently this check only works for string based rules and
		// capabilities. This means that the exec conditions are not used. Also
		// the ontology manager is not used.
		return rule.satisfiedBy(capability);
	}

}
