package org.adiusframework.service.pddlgenerator;

import java.util.List;
import java.util.Vector;

import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.ontology.frameworkontology.model.Constant;
import org.adiusframework.resource.state.AssertionObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Domain {

	private static final Logger LOGGER = LoggerFactory.getLogger(Domain.class);

	protected static final String DEFAULT_DOMAIN = "adius_service_composition_domain";

	private FrameworkOntologyManager contentManager;

	private String name;

	private List<AssertionObject> constants;

	public Domain(FrameworkOntologyManager contentManager) {
		this(DEFAULT_DOMAIN, contentManager);
	}

	public Domain(String name, FrameworkOntologyManager contentManager) {
		setContentManager(contentManager);
		setName(name);
		setConstants(null);
	}

	public FrameworkOntologyManager getContentManager() {
		return contentManager;
	}

	protected void setContentManager(FrameworkOntologyManager contentManager) {
		this.contentManager = contentManager;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public List<AssertionObject> getConstants() {
		return constants;
	}

	protected void setConstants(List<AssertionObject> constants) {
		this.constants = constants;
	}

	public void initDomain() {

		// reset constants
		setConstants(new Vector<AssertionObject>());

		// load constants from ontology
		try {
			for (Constant constant : getContentManager().getConstants()) {
				getConstants().add(
						new AssertionObject(constant.getRepresentativeClassName(), constant.getRepresentativeName(),
								true));
			}
		} catch (OWLInvalidContentException e) {
			LOGGER.error("Unexpected content exception in information modell " + e.getMessage());
			setConstants(new Vector<AssertionObject>());
		}
	}

}
