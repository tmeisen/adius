/************************************************************************
 * Strathclyde Planning Group,
 * Department of Computer and Information Sciences,
 * University of Strathclyde, Glasgow, UK
 * http://planning.cis.strath.ac.uk/
 * 
 * Copyright 2007, Keith Halsey
 * Copyright 2008, Andrew Coles and Amanda Smith
 *
 * (Questions/bug reports now to be sent to Andrew Coles)
 *
 * This file is part of JavaFF.
 * 
 * JavaFF is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 * 
 * JavaFF is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JavaFF.  If not, see <http://www.gnu.org/licenses/>.
 * 
 ************************************************************************/

package javaff.data.strips;

import javaff.data.Fact;
import javaff.data.PDDLPrinter;
import javaff.data.GroundFact;
import javaff.data.UngroundFact;

import javaff.planning.State;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class TrueCondition implements GroundFact, UngroundFact {
	protected static final HashSet EmptySet = new HashSet();
	private static TrueCondition t;

	private TrueCondition() {
	}

	@Override
	public Set<Fact> getFacts() {
		Set<Fact> s = new HashSet<Fact>(1);
		s.add(t);
		return s;
	}

	@Override
	public Object clone() {
		return t;
	}

	@Override
	public GroundFact staticify(Map fValues) {
		return this;
	}

	public static TrueCondition getInstance() {
		if (t == null)
			t = new TrueCondition();
		return t;
	}

	@Override
	public UngroundFact minus(UngroundFact effect) {
		return this;
	}

	@Override
	public boolean isTrue(State s) {
		return true;
	}

	@Override
	public boolean isStatic() {
		return true;
	}

	@Override
	public Set getStaticPredicates() {
		return TrueCondition.EmptySet;
	}

	// public Set getConditionalPropositions()
	// {
	// return TrueCondition.EmptySet;
	// }

	@Override
	public Set getComparators() {
		return TrueCondition.EmptySet;
	}

	@Override
	public GroundFact ground(Map varMap) {
		return this;
	}

	@Override
	public String toString() {
		return "()";
	}

	@Override
	public String toStringTyped() {
		return toString();
	}

	@Override
	public void PDDLPrint(java.io.PrintStream p, int indent) {
		PDDLPrinter.printToString(this, p, false, false, indent);
	}

	@Override
	public void apply(State s) {
	}

	@Override
	public void applyAdds(State s) {
	}

	@Override
	public void applyDels(State s) {
	}

	// @Override
	// public Set<SingleLiteral> getAddPropositions()
	// {
	// return null;
	// }
	//
	// @Override
	// public Set<SingleLiteral> getDeletePropositions()
	// {
	// return null;
	// }

	@Override
	public Set getOperators() {
		return null;
	}

	// @Override
	// public GroundFact staticifyEffect(Map fValues)
	// {
	// return null;
	// }

	@Override
	public boolean effects(PredicateSymbol ps) {
		return false;
	}

	@Override
	public UngroundFact effectsAdd(UngroundFact cond) {
		return null;
	}

}
