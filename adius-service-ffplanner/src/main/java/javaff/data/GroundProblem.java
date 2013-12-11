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

import javaff.planning.STRIPSState;
import javaff.planning.MetricState;
import javaff.planning.TemporalMetricState;
import javaff.planning.RelaxedPlanningGraph;
import javaff.planning.RelaxedMetricPlanningGraph;
import javaff.planning.RelaxedTemporalMetricPlanningGraph;
import javaff.data.adl.Imply;
import javaff.data.metric.BinaryComparator;
import javaff.data.metric.Function;
import javaff.data.metric.NamedFunction;
import javaff.data.metric.ResourceOperator;
import javaff.data.strips.And;
import javaff.data.strips.Equals;
import javaff.data.strips.InstantAction;
import javaff.data.strips.Not;
import javaff.data.strips.NullFact;
import javaff.data.strips.Proposition;
import javaff.data.strips.SingleLiteral;
import javaff.data.strips.TrueCondition;
import javaff.data.temporal.DurativeAction;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroundProblem implements Cloneable {
	private static final Logger LOGGER = LoggerFactory.getLogger(GroundProblem.class);
	public String name;
	public Set<Action> actions = new HashSet<Action>();
	public Map<NamedFunction, BigDecimal> functionValues = new Hashtable<NamedFunction, BigDecimal>();
	public Metric metric;

	public GroundFact goal;
	public Set<Proposition> initial;

	/**
	 * A set of all grounded propositions which can be in the domain. This will
	 * include all propositions which could exist, but do not appear in the init
	 * or goal conditions, and are not preconditions or effects of any actions.
	 */
	public Set<Proposition> groundedPropositions;
	public Set<Proposition> reachableFacts;

	public TemporalMetricState tmstate = null;
	public MetricState mstate = null;
	public STRIPSState state = null;

	public HashSet<Parameter> objects;

	@Override
	public Object clone() {
		GroundProblem clone = new GroundProblem(new HashSet<Action>(this.actions), new HashSet<Proposition>(
				this.initial), (GroundFact) this.goal.clone(), new Hashtable<NamedFunction, BigDecimal>(
				this.functionValues), this.metric);
		if (this.state != null)
			clone.state = (STRIPSState) this.state.clone();
		if (this.mstate != null)
			clone.mstate = (MetricState) this.mstate.clone();
		if (this.tmstate != null)
			clone.tmstate = (TemporalMetricState) this.tmstate.clone();

		clone.groundedPropositions = new HashSet<Proposition>(this.groundedPropositions);
		clone.reachableFacts = new HashSet<Proposition>(this.reachableFacts);
		clone.name = this.name;
		clone.objects = new HashSet<Parameter>(this.objects);
		return clone;

	}

	public GroundProblem(Set<Action> a, Set<Proposition> i, GroundFact g, Map<NamedFunction, BigDecimal> f, Metric m) {
		actions = a;
		initial = i;
		goal = g;
		functionValues = f;
		metric = m;
		name = "unknown";
		this.reachableFacts = new HashSet<Proposition>();
		// this.state = this.getTemporalMetricInitialState();
		this.objects = new HashSet<Parameter>();

		extractPddlObjects();
		computeGroundedProps();
		makeAllLowerCase();

		this.reachableFacts = new HashSet<Proposition>(this.groundedPropositions);
	}

	private void extractPddlObjects() {
		this.objects.clear();
		for (Action a : this.actions) {
			this.objects.addAll(a.params);
		}
	}

	private void makeAllLowerCase() {
		// action, goal, initial, groundedprops
		Hashtable<String, Fact> lookup = new Hashtable<String, Fact>();
		for (Fact p : this.groundedPropositions) {
			lookup.put(p.toString().toLowerCase(), p);
		}
	}

	private void computeGroundedProps() {
		this.groundedPropositions = new HashSet<Proposition>();
		this.groundedPropositions.addAll(initial); // add statics?
		for (Action a : this.actions) {
			for (Fact pc : a.getPreconditions()) {
				Collection<Fact> c = this.decompileFact(pc);
				for (Fact f : c) {
					this.groundedPropositions.add((Proposition) f);
				}
			}

			for (Fact add : a.getAddPropositions()) {
				Collection<Fact> c = this.decompileFact(add);
				for (Fact f : c) {
					this.groundedPropositions.add((Proposition) f);
				}
			}

			for (Not del : a.getDeletePropositions()) {
				Collection<Fact> c = this.decompileFact(del);
				for (Fact f : c) {
					this.groundedPropositions.add((Proposition) f);
				}
			}
		}
	}

	/**
	 * This helper method deconstructs any Fact into individual literals for use
	 * in the planning graph. If a new type is introduced into the heirarchy,
	 * this will probably need modified.
	 * 
	 * @param f
	 * @return
	 */
	protected Collection<Fact> decompileFact(Fact f) {
		HashSet<Fact> lits = new HashSet<Fact>();

		if (f instanceof Function || f instanceof ResourceOperator) {
			lits.add(f);
		} else {
			this.decompileFact(f, lits);
		}
		return lits;
	}

	/**
	 * This helper method deconstructs any Fact into individual literals for use
	 * in the planning graph. If a new type is introduced into the heirarchy,
	 * this will probably need modified.
	 * 
	 * @param f
	 * @return
	 */
	protected void decompileFact(Fact f, Collection<Fact> existing) {
		if (f instanceof Imply) {
			Set<And> ands = ((Imply) f).getSTRIPSConjunctions();

			for (And a : ands)
				this.decompileFact(a, existing);
		} else if (f instanceof CompoundLiteral) {
			for (Fact cf : f.getFacts()) {
				this.decompileFact(cf, existing);
			}
		} else if (f instanceof SingleLiteral) {
			existing.add(f);
		} else if (f instanceof Not) {
			// existing.add(((Not)f).literal);
			this.decompileFact(((Not) f).literal, existing);
		} else if (f instanceof Function || f instanceof BinaryComparator) {
			existing.add(f);
		} else if (f instanceof NullFact) {

		} else if (f instanceof Equals) {

		} else if (f instanceof Function) {
			existing.add(f);
		} else if (f instanceof TrueCondition) {

		} else
			throw new IllegalArgumentException("Cannot decompile fact " + f + " - unknown type: " + f.getClass());
	}

	public STRIPSState getSTRIPSInitialState() {
		if (this.state == null) {
			STRIPSState s = new STRIPSState(actions, initial, goal);
			// s.setRPG(new RelaxedPlanningGraph(this)); change!!!
			if (this.mstate != null) {
				s.setRPG(mstate.getRPG());
			} else {
				s.setRPG(new RelaxedPlanningGraph(this));
			}
			this.state = s;
		}
		return state;
	}

	public MetricState getMetricInitialState() {
		if (this.mstate == null) {
			MetricState ms = new MetricState(actions, initial, goal, functionValues, metric);
			ms.setRPG(new RelaxedMetricPlanningGraph(this));
			this.mstate = ms;
			LOGGER.info("msstate is null " + (mstate == null));

		}
		return mstate;
	}

	public STRIPSState recomputeSTRIPSInitialState() {
		STRIPSState s = new STRIPSState(actions, initial, goal);
		s.setRPG(new RelaxedPlanningGraph(this));
		this.state = s;

		return this.state;
	}

	public MetricState recomputeMetricInitialState() {
		MetricState ms = new MetricState(actions, initial, goal, functionValues, metric);
		ms.setRPG(new RelaxedMetricPlanningGraph(this));
		this.mstate = ms;

		return mstate;
	}

	public TemporalMetricState getTemporalMetricInitialState() {
		if (tmstate == null) {
			Set na = new HashSet();
			Set ni = new HashSet();
			Iterator ait = actions.iterator();
			while (ait.hasNext()) {
				Action act = (Action) ait.next();
				if (act instanceof InstantAction) {
					na.add(act);
					ni.add(act);
				} else if (act instanceof DurativeAction) {
					DurativeAction dact = (DurativeAction) act;
					na.add(dact.startAction);
					na.add(dact.endAction);
					ni.add(dact.startAction);
				}
			}
			TemporalMetricState ts = new TemporalMetricState(ni, initial, goal, functionValues, metric);
			GroundProblem gp = new GroundProblem(na, initial, goal, functionValues, metric);
			gp.name = this.name;
			gp.reachableFacts = new HashSet<Proposition>(this.reachableFacts);
			ts.setRPG(new RelaxedTemporalMetricPlanningGraph(gp));
			tmstate = ts;
			LOGGER.info("msstate is null " + (tmstate == null));
		}
		return tmstate;
	}

	public TemporalMetricState recomputeTemporalMetricInitialState() {
		Set na = new HashSet();
		Set ni = new HashSet();
		Iterator ait = actions.iterator();
		while (ait.hasNext()) {
			Action act = (Action) ait.next();
			if (act instanceof InstantAction) {
				na.add(act);
				ni.add(act);
			} else if (act instanceof DurativeAction) {
				DurativeAction dact = (DurativeAction) act;
				na.add(dact.startAction);
				na.add(dact.endAction);
				ni.add(dact.startAction);
			}
		}
		TemporalMetricState ts = new TemporalMetricState(ni, initial, goal, functionValues, metric);
		GroundProblem gp = new GroundProblem(na, initial, goal, functionValues, metric);
		ts.setRPG(new RelaxedTemporalMetricPlanningGraph(gp));
		tmstate = ts;

		return tmstate;
	}

	@Override
	public String toString() {
		return "GroundProblem: " + this.name;
	}
}
