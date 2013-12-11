package org.adiusframework.service.planner;

import javaff.data.Action;
import javaff.data.Parameter;
import javaff.data.Plan;

import org.adiusframework.ontology.exception.OWLEntityNotFoundException;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.ontology.frameworkontology.model.ParameterAssertion;
import org.adiusframework.ontology.frameworkontology.model.Transformation;
import org.adiusframework.resource.serviceplan.DefaultServiceProcessPlan;
import org.adiusframework.resource.serviceplan.DefaultServiceTemplate;
import org.adiusframework.resource.serviceplan.ServiceProcessPlan;
import org.adiusframework.service.xml.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class FFPlannerProcessParser implements ServiceProcessParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(FFPlannerProcessParser.class);

	private String ontology;

	public String getOntology() {
		return ontology;
	}

	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	protected FrameworkOntologyManager loadOntologyManager() throws PlanParseException {
		FrameworkOntologyManager contentManager = null;
		try {
			contentManager = new FrameworkOntologyManager(getOntology());
		} catch (OWLEntityNotFoundException e) {
			throw new PlanParseException("Ontology entity not found, required for parsing: " + e.getMessage());
		} catch (OWLOntologyCreationException e) {
			throw new PlanParseException("Ontology creation failed: " + e.getMessage());
		}
		LOGGER.debug("FrameworkOntologyManager created");
		return contentManager;
	}

	@Override
	public ServiceProcessPlan parse(Plan plan, ServiceProcessPlan baseProcessPlan) throws PlanParseException {

		// first we create the content manager to access the ontology
		FrameworkOntologyManager contentManager = loadOntologyManager();

		// now we can parse the plan information
		DefaultServiceProcessPlan processPlan = new DefaultServiceProcessPlan(baseProcessPlan);
		try {
			LOGGER.debug("Translating plan result into service process");
			for (Action a : plan.getActions()) {

				// first lets find the transformation information model in the
				// ontology
				String transformationName = a.name.toString();
				Transformation transformation = null;
				for (Transformation candidate : contentManager.getTransformations()) {
					LOGGER.debug("Checking candidate " + candidate.getRepresentativeName());
					if (candidate.getRepresentativeName().equalsIgnoreCase(transformationName)) {
						if (transformation != null)
							throw new PlanParseException("Multiple matching candidates for transformation "
									+ transformationName);
						transformation = candidate;
					}
				}
				if (transformation == null)
					throw new PlanParseException("No matching candidate for transformation " + transformationName
							+ " found");

				// now we can build the template
				DefaultServiceTemplate template = new DefaultServiceTemplate(Category.TRANSFORMATION,
						transformation.getRepresentativeName());

				// at least we have to map the parameter values
				for (Parameter p : a.params) {

					// the parameter has to be translated into the ontology
					for (ParameterAssertion assertion : transformation.getParameterAssertions()) {
						if (assertion.getIndividual().getTypeName().equalsIgnoreCase(p.getType().toString())) {

							// getting the value of the parameter (syntax
							// name_value)
							String value = p.getName().substring(p.getName().lastIndexOf("_") + 1);
							template.addProperty(assertion.getPropertyName(), value);
						}
					}
				}
				processPlan.addServiceTemplate(template);
			}
		} catch (OWLInvalidContentException e) {
			throw new PlanParseException(e.getMessage());
		}
		return processPlan;
	}
}
