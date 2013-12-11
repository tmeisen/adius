package javaff.data.adl;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javaff.data.CompoundLiteral;
import javaff.data.Fact;
import javaff.data.GroundFact;

import javaff.data.Literal;
import javaff.data.PDDLPrinter;
import javaff.data.UngroundFact;

import javaff.data.strips.Not;
import javaff.data.strips.NullFact;
import javaff.data.strips.PDDLObject;
import javaff.data.strips.PredicateSymbol;
import javaff.data.strips.TrueCondition;
import javaff.data.strips.Variable;
import javaff.planning.State;

public class Or implements CompoundLiteral, GroundFact, UngroundFact {
	public Set<Fact> literals; // set of Literals

	public Or() {
		this.literals = new HashSet<Fact>();
	}

	public Or(Collection<Fact> props) {
		this();
		this.literals = new HashSet<Fact>(props);
	}

	@Override
	public Object clone() {
		Or or = new Or();
		or.literals = new HashSet<Fact>(this.literals);
		return or;
	}

	public void add(Object o) {
		literals.add((Fact) o);
	}

	public void addAll(Collection<Fact> c) {
		for (Object l : c)
			this.add(l);
	}

	@Override
	public boolean isStatic() {
		Iterator<Fact> it = literals.iterator();
		while (it.hasNext()) {
			Fact c = it.next();
			if (!c.isStatic())
				return false;
		}
		return true;
	}

	@Override
	public GroundFact staticify(Map fValues) {
		Set<Fact> newlit = new HashSet<Fact>(literals.size());
		Iterator<Fact> it = literals.iterator();
		while (it.hasNext()) {
			GroundFact c = (GroundFact) it.next();
			if (!(c instanceof TrueCondition))
				newlit.add(c.staticify(fValues));
		}
		literals = newlit;
		if (literals.isEmpty())
			return TrueCondition.getInstance();
		else
			return this;
	}

	public GroundFact staticifyEffect(Map fValues) {
		Set<Fact> newlit = new HashSet<Fact>(literals.size());
		Iterator<Fact> it = literals.iterator();
		while (it.hasNext()) {
			GroundFact e = (GroundFact) it.next();
			if (!(e instanceof NullFact))
				newlit.add(e.staticify(fValues));
		}
		literals = newlit;
		if (literals.isEmpty())
			return NullFact.getInstance();
		else
			return this;
	}

	@Override
	public Set getStaticPredicates() {
		Set rSet = new HashSet();
		Iterator<Fact> it = literals.iterator();
		while (it.hasNext()) {
			UngroundFact c = (UngroundFact) it.next();
			rSet.addAll(c.getStaticPredicates());
		}
		return rSet;
	}

	@Override
	public boolean effects(PredicateSymbol ps) {
		boolean rEff = false;
		Iterator<Fact> lit = literals.iterator();
		while (lit.hasNext() && !(rEff)) {
			UngroundFact ue = (UngroundFact) lit.next();
			rEff = ue.effects(ps);
		}
		return rEff;
	}

	@Override
	public UngroundFact minus(UngroundFact effect) {
		Or a = new Or();
		Iterator<Fact> lit = literals.iterator();
		while (lit.hasNext()) {
			UngroundFact p = (UngroundFact) lit.next();
			a.add(p.minus(effect));
		}
		return a;
	}

	@Override
	public UngroundFact effectsAdd(UngroundFact cond) {
		Iterator<Fact> lit = literals.iterator();
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
		Or o = new Or();
		Iterator<Fact> lit = literals.iterator();
		while (lit.hasNext()) {
			UngroundFact p = (UngroundFact) lit.next();
			GroundFact g = p.ground(varMap);

			if (g instanceof Or) {
				for (Fact f : g.getFacts()) {
					o.add(f);
				}
			} else
				o.add(g);
		}
		return o;
	}

	@Override
	public boolean isTrue(State s) {
		for (Fact l : this.literals) {
			GroundFact c = (GroundFact) l;
			if (c.isTrue(s))
				return true;
		}
		return false;
	}

	@Override
	public void apply(State s) {
		applyDels(s);
		applyAdds(s);
	}

	@Override
	public void applyAdds(State s) {
		Iterator<Fact> eit = literals.iterator();
		while (eit.hasNext()) {
			GroundFact e = (GroundFact) eit.next();
			e.applyAdds(s);
		}
	}

	@Override
	public void applyDels(State s) {
		Iterator<Fact> eit = literals.iterator();
		while (eit.hasNext()) {
			GroundFact e = (GroundFact) eit.next();
			e.applyDels(s);
		}
	}

	// public Set getConditionalPropositions()
	// {
	// Set rSet = new HashSet();
	// Iterator eit = literals.iterator();
	// while (eit.hasNext())
	// {
	// GroundFact e = (GroundFact) eit.next();
	// rSet.addAll(e.getConditionalPropositions());
	// }
	// return rSet;
	// }
	//
	// public Set getAddPropositions()
	// {
	// Set rSet = new HashSet();
	// Iterator eit = literals.iterator();
	// while (eit.hasNext())
	// {
	// GroundFact e = (GroundFact) eit.next();
	// rSet.addAll(e.getAddPropositions());
	// }
	// return rSet;
	// }
	//
	// public Set getDeletePropositions()
	// {
	// Set rSet = new HashSet();
	// Iterator eit = literals.iterator();
	// while (eit.hasNext())
	// {
	// GroundFact e = (GroundFact) eit.next();
	// rSet.addAll(e.getDeletePropositions());
	// }
	// return rSet;
	// }

	@Override
	public Set getOperators() {
		Set rSet = new HashSet();
		Iterator<Fact> eit = literals.iterator();
		while (eit.hasNext()) {
			GroundFact e = (GroundFact) eit.next();
			rSet.addAll(e.getOperators());
		}
		return rSet;
	}

	@Override
	public Set getComparators() {
		Set rSet = new HashSet();
		Iterator<Fact> eit = literals.iterator();
		while (eit.hasNext()) {
			GroundFact e = (GroundFact) eit.next();
			rSet.addAll(e.getComparators());
		}
		return rSet;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Or) {
			Or a = (Or) obj;
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
		PDDLPrinter.printToString(literals, "or", p, false, true, indent);
	}

	@Override
	public String toString() {
		String str = "(or";
		Iterator<Fact> it = literals.iterator();
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
		String str = "(or";
		Iterator<Fact> it = literals.iterator();
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
