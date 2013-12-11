package org.adiusframework.service.pddlgenerator;

import org.adiusframework.ontology.frameworkontology.FrameworkOntologyManager;

public class PDDLGeneratorFactory {

	private boolean problemDecomposition;

	public PDDLGenerator create(FrameworkOntologyManager contentManager) {
		if (isProblemDecomposition())
			return new MultipleProblemPDDLGenerator(contentManager);
		return new DefaultPDDLGenerator(contentManager);
	}

	public boolean isProblemDecomposition() {
		return problemDecomposition;
	}

	public void setProblemDecomposition(boolean problemDecomposition) {
		this.problemDecomposition = problemDecomposition;
	}

}
