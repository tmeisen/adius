package org.adiusframework.resource.pddl;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class PDDLDescription implements Serializable {
	private static final long serialVersionUID = 7872236245992005780L;

	private String domain;

	private List<String> problems;

	public PDDLDescription() {
		problems = new Vector<String>();
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void addProblems(List<String> problems) {
		for (String problem : problems)
			addProblem(problem);
	}

	public void addProblem(String problem) {
		problems.add(problem);
	}

	public List<String> getProblems() {
		return problems;
	}

	public int getNumberOfProblems() {
		return problems.size();
	}

}
