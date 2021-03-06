package javaff.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javaff.data.strips.Not;
import javaff.data.strips.NullFact;
import javaff.data.strips.OperatorName;
import javaff.data.strips.STRIPSInstantAction;
import javaff.planning.State;

/**
 * A stub action which requires nothing and achieves nothing.
 * 
 * @author pattison
 * 
 */
public class NullInstantAction extends STRIPSInstantAction {
	private final HashSet emptySet = new HashSet();

	public NullInstantAction() {
		super.name = new OperatorName("NullAction");
		super.params = new ArrayList();
		super.setEffect(NullFact.getInstance());
		super.setCondition(NullFact.getInstance());
		super.cost = new BigDecimal(0);
	}

	@Override
	public void apply(State s) {
		// do nothing
	}

	@Override
	public Set<Fact> getAddPropositions() {
		return emptySet;
	}

	@Override
	public Set getComparators() {
		return emptySet;
	}

	@Override
	public Set<Fact> getPreconditions() {
		return emptySet;
	}

	@Override
	public Set<Not> getDeletePropositions() {
		return emptySet;
	}

	@Override
	public Set getOperators() {
		return emptySet;
	}

	/**
	 * Always returns true because nothing is changed when this action is
	 * applied.
	 */
	@Override
	public boolean isApplicable(State s) {
		return true;
	}

	@Override
	public void staticify(Map values) {

	}
}