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

package javaff.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javaff.data.metric.NamedFunction;
import javaff.data.strips.Not;
import javaff.data.strips.OperatorName;
import javaff.planning.State;

public abstract class Action {
	public OperatorName name;
	public List<Parameter> params = new ArrayList<Parameter>(); // List of
																// PDDLObjects

	public BigDecimal cost = new BigDecimal(0);

	@Override
	public String toString() {
		String stringrep = name.toString();
		Iterator<Parameter> i = params.iterator();
		while (i.hasNext()) {
			stringrep = stringrep + " " + i.next();
		}
		return stringrep;
	}

	public abstract boolean isApplicable(State s);

	public abstract void apply(State s);

	public abstract Set<Fact> getPreconditions();

	public abstract Set<Fact> getAddPropositions();

	public abstract Set<Not> getDeletePropositions();

	public abstract Set<NamedFunction> getComparators();

	public abstract Set getOperators();

	public abstract void staticify(Map fValues);

	public boolean deletes(Fact f) {
		for (Not n : this.getDeletePropositions()) {
			if (n.literal.equals(f)) {
				return true;
			}
		}

		return false;
	}

	public boolean adds(Fact f) {
		return this.getAddPropositions().contains(f);
	}

	public boolean requires(Fact f) {
		return this.getPreconditions().contains(f);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Action) {
			Action a = (Action) obj;
			return (name.equals(a.name) && params.equals(a.params));
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode() ^ params.hashCode();
	}

	@Override
	public abstract Object clone();

}
