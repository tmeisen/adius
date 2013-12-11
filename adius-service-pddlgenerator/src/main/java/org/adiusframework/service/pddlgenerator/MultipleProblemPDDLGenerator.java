package org.adiusframework.service.pddlgenerator;

import java.util.List;
import java.util.Vector;

import org.adiusframework.ontology.exception.OWLEntityNotFoundException;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;
import org.adiusframework.resource.state.State;
import org.adiusframework.resource.state.StateCollection;

/**
 * The PDDL generator.
 * 
 * @author Erdal Baran
 * @author Alexander Tenbrock
 * @author Tobias Meisen
 */
public class MultipleProblemPDDLGenerator implements PDDLGenerator {

	private FrameworkOntologyManager contentManager;

	private PDDLDomainBuilder domainBuilder;

	private PDDLProblemBuilder problemBuilder;

	/**
	 * Constructor
	 * 
	 * @param contentManager
	 *            the content manager to use
	 */
	public MultipleProblemPDDLGenerator(FrameworkOntologyManager contentManager) {

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

	public PDDLProblemBuilder getProblemBuilder() {
		return problemBuilder;
	}

	public void setProblemBuilder(PDDLProblemBuilder problemBuilder) {
		this.problemBuilder = problemBuilder;
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

	@Override
	public List<String> getPDDLProblems(String problemPrefix, State axiomState, State currentState, State targetState)
			throws OWLEntityNotFoundException, UnsupportedExpressionException {
		List<String> problems = new Vector<String>();
		if (StateCollection.class.isInstance(currentState)) {
			int count = 0;
			for (State actualState : StateCollection.class.cast(currentState).getStates()) {
				count++;
				String problem = getProblemBuilder().buildPDDL(problemPrefix + "_" + count, axiomState, actualState,
						targetState);
				problems.add(problem);
			}
		} else
			problems.add(getProblemBuilder().buildPDDL(problemPrefix, axiomState, currentState, targetState));
		return problems;
	}

}
