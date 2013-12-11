package org.adiusframework.service.pddlgenerator;

import java.util.List;

import org.adiusframework.ontology.exception.OWLEntityNotFoundException;
import org.adiusframework.ontology.exception.OWLInvalidContentException;
import org.adiusframework.ontology.exception.UnsupportedExpressionException;
import org.adiusframework.resource.state.State;

public interface PDDLGenerator {

	public String getPDDLDomain() throws OWLInvalidContentException, UnsupportedExpressionException;

	public List<String> getPDDLProblems(String problemPrefix, State axiomState, State currentState, State targetState)
			throws OWLEntityNotFoundException, UnsupportedExpressionException;

}
