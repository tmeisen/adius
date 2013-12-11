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
import javaff.data.GroundFact;
import javaff.data.Literal;
import javaff.data.UngroundFact;

import javaff.data.CompoundLiteral;
import javaff.data.PDDLPrinter;
import javaff.planning.State;

import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Iterator;
import java.io.PrintStream;

public class And implements CompoundLiteral, GroundFact, UngroundFact {
	public Set<Fact> literals = new HashSet<Fact>(); // set of Literals

	public And() {
		super();
		this.literals = new HashSet<Fact>();
	}

	public And(Collection<Fact> props) {
		this();
		for (Fact f : props)
			this.add(f);
	}

	public And(Fact f) {
		this();
		this.add(f);
	}

	@Override
	public Object clone() {
		And and = new And();
		and.literals = new HashSet<Fact>(this.literals);
		return and;
	}

	public void add(Fact o) {
		if (o instanceof And) {
			for (Fact f : ((And) o).literals) {
				this.add(f);
			}
		} else {
			literals.add(o);
		}
	}

	public void addAll(Collection<Fact> c) {
		for (Fact l : c)
			this.add(l);
	}

	@Override
	public boolean isStatic() {
		for (Fact c : this.literals) {
			if (!c.isStatic())
				return false;
		}
		return true;
	}

	@Override
	public GroundFact staticify(Map fValues) {
		Set newlit = new HashSet(literals.size());
		for (Fact c : this.literals) {
			if ((c instanceof TrueCondition))
				newlit.add(((TrueCondition) c).staticify(fValues));
			else if (!(c instanceof NullFact) && c instanceof GroundFact)
				newlit.add(((GroundFact) c).staticify(fValues));
		}
		literals = newlit;
		if (literals.isEmpty())
			return TrueCondition.getInstance();
		else
			return this;
	}

	//
	// public GroundFact staticifyEffect(Map fValues)
	// {
	// Set newlit = new HashSet(literals.size());
	// Iterator it = literals.iterator();
	// while (it.hasNext())
	// {
	// GroundFact e = (GroundFact) it.next();
	// }
	// literals = newlit;
	// if (literals.isEmpty())
	// return NullEffect.getInstance();
	// else
	// return this;
	// }

	@Override
	public boolean isTrue(State s) {
		Iterator cit = literals.iterator();
		while (cit.hasNext()) {
			GroundFact c = (GroundFact) cit.next();
			if (!c.isTrue(s))
				return false;
		}
		return true;
	}

	@Override
	public void apply(State s) {
		applyDels(s);
		applyAdds(s);
	}

	@Override
	public void applyAdds(State s) {
		Iterator eit = literals.iterator();
		while (eit.hasNext()) {
			GroundFact e = (GroundFact) eit.next();
			e.applyAdds(s);
		}
	}

	@Override
	public void applyDels(State s) {
		Iterator eit = literals.iterator();
		while (eit.hasNext()) {
			GroundFact e = (GroundFact) eit.next();
			e.applyDels(s);
		}
	}

	@Override
	public Set getOperators() {
		Set rSet = new HashSet();
		Iterator eit = literals.iterator();
		while (eit.hasNext()) {
			GroundFact e = (GroundFact) eit.next();
			rSet.addAll(e.getOperators());
		}
		return rSet;
	}

	@Override
	public Set getComparators() {
		Set rSet = new HashSet();
		Iterator eit = literals.iterator();
		while (eit.hasNext()) {
			GroundFact e = (GroundFact) eit.next();
			rSet.addAll(e.getComparators());
		}
		return rSet;
	}

	@Override
	public Set getStaticPredicates() {
		Set rSet = new HashSet();
		Iterator it = literals.iterator();
		while (it.hasNext()) {
			UngroundFact c = (UngroundFact) it.next();
			rSet.addAll(c.getStaticPredicates());
		}
		return rSet;
	}

	@Override
	public boolean effects(PredicateSymbol ps) {
		boolean rEff = false;
		Iterator lit = literals.iterator();
		while (lit.hasNext() && !(rEff)) {
			UngroundFact ue = (UngroundFact) lit.next();
			rEff = ue.effects(ps);
		}
		return rEff;
	}

	@Override
	public UngroundFact minus(UngroundFact effect) {
		And a = new And();
		Iterator lit = literals.iterator();
		while (lit.hasNext()) {
			UngroundFact p = (UngroundFact) lit.next();
			a.add(p.minus(effect));
		}
		return a;
	}

	@Override
	public UngroundFact effectsAdd(UngroundFact cond) {
		Iterator lit = literals.iterator();
		UngroundFact c = null;
		while (lit.hasNext()) {
			UngroundFact p = (UngroundFact) lit.next();
			UngroundFact d = p.effectsAdd(cond);
			if (!d.equals(cond))
				c = d;
		}
		if (c == null)
			return cond;
		else
			return c;
	}

	@Override
	public GroundFact ground(Map<Variable, PDDLObject> varMap) {
		And a = new And();
		Iterator lit = literals.iterator();
		while (lit.hasNext()) {
			UngroundFact p = (UngroundFact) lit.next();
			GroundFact g = p.ground(varMap);

			if (g instanceof And) {
				for (Fact f : g.getFacts()) {
					a.add(f);
				}
			} else
				a.add(g);
		}
		return a;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof And) {
			And a = (And) obj;
			return (literals.equals(a.literals));
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return literals.hashCode();
	}

	@Override
	public void PDDLPrint(PrintStream p, int indent) {
		PDDLPrinter.printToString(literals, "and", p, false, true, indent);
	}

	@Override
	public String toString() {
		String str = "(and";
		Iterator it = literals.iterator();
		while (it.hasNext()) {
			Object next = it.next();
			if (next instanceof TrueCondition || next instanceof NullFact)
				continue;

			str += " (" + next + ") ";
		}
		str += ")";
		return str;
	}

	@Override
	public String toStringTyped() {
		String str = "(and";
		Iterator it = literals.iterator();
		while (it.hasNext()) {
			Object next = it.next();
			if (next instanceof Not) {
				Not l = (Not) next;
				str += " (" + l.toStringTyped() + ")";
			} else if (next instanceof TrueCondition || next instanceof NullFact) {
			} else {
				Literal l = (Literal) next;
				str += " (" + l.toStringTyped() + ")";
			}
		}
		str += ")";
		return str;

	}

	@Override
	public Set<Fact> getFacts() {
		return literals;
	}
}
