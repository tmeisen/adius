package org.adiusframework.service.pddlgenerator;

import java.util.List;
import java.util.Vector;

import org.adiusframework.ontology.exception.OWLEntityNotFoundException;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.resource.state.State;

/**
 * The PDDL generator.
 * 
 * @author Erdal Baran
 * @author Alexander Tenbrock
 * @author Tobias Meisen
 */
public class DefaultPDDLGenerator implements PDDLGenerator {

	private FrameworkOntologyManager contentManager;

	private PDDLDomainBuilder domainBuilder;

	private PDDLProblemBuilder problemBuilder;

	/**
	 * Constructor
	 * 
	 * @param contentManager
	 *            the content manager to use
	 */
	public DefaultPDDLGenerator(FrameworkOntologyManager contentManager) {

		// initialize members
		setContentManager(contentManager);

		// create the domain description
		Domain domain = new Domain(contentManager);
		domain.initDomain();

		// create the builder
		setDomainBuilder(new PDDLDomainBuilder(domain));
		setProblemBuilder(new PDDLProblemBuilder(getDomainBuilder()));
	}

	public FrameworkOntologyManager getContentManager() {
		return contentManager;
	}

	public void setContentManager(FrameworkOntologyManager contentManager) {
		this.contentManager = contentManager;
	}

	public PDDLDomainBuilder getDomainBuilder() {
		return domainBuilder;
	}

	public void setDomainBuilder(PDDLDomainBuilder domainBuilder) {
		this.domainBuilder = domainBuilder;
	}

	/**
	 * This method returns the PDDL domain description.
	 * 
	 * @return domain description
	 * @throws UnsupportedExpressionException
	 * @throws OWLInvalidContentException
	 */
	@Override
	public String getPDDLDomain() throws OWLInvalidContentException, UnsupportedExpressionException {
		return getDomainBuilder().buildPDDL();
	}

	/**
	 * This method returns the PDDL problem description.
	 * 
	 * @return The problem description
	 * @throws OWLEntityNotFoundException
	 * @throws UnsupportedExpressionException
	 */
	@Override
	public List<String> getPDDLProblems(String problemName, State axiomState, State currentState, State targetState)
			throws OWLEntityNotFoundException, UnsupportedExpressionException {
		List<String> problems = new Vector<String>();
		problems.add(getProblemBuilder().buildPDDL(problemName, axiomState, currentState, targetState));
		return problems;
	}

	public PDDLProblemBuilder getProblemBuilder() {
		return problemBuilder;
	}

	public void setProblemBuilder(PDDLProblemBuilder problemBuilder) {
		this.problemBuilder = problemBuilder;
	}

}
