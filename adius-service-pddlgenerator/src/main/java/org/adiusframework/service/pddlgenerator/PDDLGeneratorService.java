package org.adiusframework.service.pddlgenerator;

import java.util.List;

import org.adiusframework.ontology.exception.OWLEntityNotFoundException;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.resource.ObjectResource;
import org.adiusframework.resource.StringResourceCapability;
import org.adiusframework.resource.pddl.PDDLDescription;
import org.adiusframework.resource.state.State;
import org.adiusframework.service.ServiceException;
import org.adiusframework.service.ServiceInstance;
import org.adiusframework.service.ServiceResult;
import org.adiusframework.service.StandardServiceResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class PDDLGeneratorService implements ServiceInstance {

	private static final Logger LOGGER = LoggerFactory.getLogger(PDDLGeneratorService.class);

	protected static final String DEFAULT_PROBLEM_NAME = "default_problem";

	private String ontology;

	private State axiomState;

	private State actualState;

	private State targetState;

	private PDDLGeneratorFactory factory;

	public PDDLGeneratorFactory getFactory() {
		return factory;
	}

	public void setFactory(PDDLGeneratorFactory factory) {
		this.factory = factory;
	}

	public State getTargetState() {
		return targetState;
	}

	public void setTargetState(State targetState) {
		this.targetState = targetState;
	}

	public State getAxiomState() {
		return axiomState;
	}

	public void setAxiomState(State axiomState) {
		this.axiomState = axiomState;
	}

	public State getActualState() {
		return actualState;
	}

	public void setActualState(State actualState) {
		this.actualState = actualState;
	}

	/**
	 * Sets ontology, containing the path to the information model.
	 * 
	 * @param ontology
	 *            Path to the ontology containing the information model
	 */
	public void setOntology(String ontology) {
		this.ontology = ontology;
	}

	public String getOntology() {
		return this.ontology;
	}

	@Override
	public ServiceResult execute() throws ServiceException {
		try {
			StandardServiceResult result = new StandardServiceResult();

			// setup contentManager and PDDLGenerator
			FrameworkOntologyManager contentManager = new FrameworkOntologyManager(getOntology());
			PDDLGenerator generator = getFactory().create(contentManager);

			// create domain and problem file
			String domain = generator.getPDDLDomain();
			List<String> problems = generator.getPDDLProblems(DEFAULT_PROBLEM_NAME, getAxiomState(), getActualState(),
					getTargetState());
			LOGGER.debug("PDDL domain:");
			LOGGER.debug(domain);
			LOGGER.debug("PDDL problems:");
			LOGGER.debug(problems.toString());
			PDDLDescription pddlDescription = new PDDLDescription();
			pddlDescription.setDomain(domain);
			pddlDescription.addProblems(problems);

			// adding the data to the result of the service
			ObjectResource<PDDLDescription> pddlResource = new ObjectResource<PDDLDescription>();
			pddlResource.setCapability(new StringResourceCapability("pddl_description"));
			pddlResource.setObject(pddlDescription);
			result.addResource(pddlResource);
			return result;

		} catch (OWLEntityNotFoundException | OWLOntologyCreationException | OWLInvalidContentException
				| UnsupportedExpressionException e) {
			LOGGER.error(e.getClass() + " while generating OutputDataset");
			LOGGER.error(e.toString());
			throw new ServiceException(e);
		}

	}

}
