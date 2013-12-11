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

package javaff.planning;

import javaff.data.Fact;
import javaff.data.GroundFact;
import javaff.data.GroundProblem;
import javaff.data.TotalOrderPlan;
import javaff.data.strips.Not;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RelaxedPlanningGraph extends PlanningGraph {
	private static final Logger LOGGER = LoggerFactory.getLogger(RelaxedPlanningGraph.class);
	private List<PGAction> notPrecondtionAction;

	public RelaxedPlanningGraph(GroundProblem gp) {
		// super(gp);
		notPrecondtionAction = new ArrayList<PGAction>();
		// setProposition(gp.groundedPropositions);
		setActionMap(gp.actions);
		this.setLinks();
		createNoOps();
		setGoal(gp.goal);
	}

	/**
	 * Construct an RPG with a goal which overrides that contained in the first
	 * parameter's GroundProblem.goal field.
	 * 
	 * @param gp
	 * @param goal
	 */
	public RelaxedPlanningGraph(GroundProblem gp, GroundFact goal) {
		// super(gp);
		// setProposition(gp.groundedPropositions);
		setActionMap(gp.actions);
		this.setLinks();
		createNoOps();

		this.setGoal(goal);
	}

	/*
	 * private void setProposition(Set<Proposition> propositions){
	 * for(Proposition p : propositions){ for(Fact f : p.getFacts()){
	 * this.getPGFact(f); } } }
	 */

	@Override
	public List extractPlan() {
		return this.searchRelaxedPlan(this.goal, super.num_layers);
	}

	@Override
	protected void setLinks() {
		long startMem = Runtime.getRuntime().freeMemory();
		int i = 0;
		for (Object a : this.actions) {
			PGAction pga = (PGAction) a;
			i++;
			Set<Fact> pcs = pga.action.getPreconditions();
			for (Fact p : pcs) {

				PGFact pgp = this.getPGFact(p);
				if (pgp.fact instanceof Not && !notPrecondtionAction.contains(pga)) {
					// LOGGER.info("setLinks : pga precondition NOT " + pga +
					// " pcs: " + pcs);
					notPrecondtionAction.add(pga);
				} else {
					// LOGGER.info("setLinks : pga precondition " + pga +
					// " pcs: " + pcs);
				}
				pga.conditions.add(pgp);
				pgp.enables.add(pga);
			}

			Set<Fact> adds = pga.action.getAddPropositions();
			for (Fact p : adds) {
				PGFact pgp = this.getPGFact(p);
				// LOGGER.info("setLinks add: pga " + pga + " pgp: " + pgp);
				pga.achieves.add(pgp);
				pgp.achievedBy.add(pga);
			}

			Set<Not> dels = pga.action.getDeletePropositions();
			for (Fact p : dels) {
				PGFact pgp = this.getPGFact(p);
				// LOGGER.info("setLinks del: pga " + pga + " pgp: " + pgp);
				pga.deletes.add(pgp);
				pgp.deletedBy.add(pga);
			}
		}
		LOGGER.info("Number precons: " + propositions.size());
		// constiderNotPreconditions();
		LOGGER.info("Actions " + i++ + "Linking done!! Used Mem: "
				+ (int) (-Runtime.getRuntime().freeMemory() + startMem));

	}

	private void constiderNotPreconditions(PGFact f) {

		for (PGAction action : notPrecondtionAction) {
			Set<Fact> pcs = action.action.getPreconditions();
			boolean doesntAllowPrecondition = false;
			for (Fact p : pcs) {
				if (p instanceof Not && f.fact.equals((new Not(p)).literal)) {
					doesntAllowPrecondition = true;
					break;
				}
			}
			if (!doesntAllowPrecondition && !f.enables.contains(action)) {
				f.enables.add(action);
				getPGFact(f.fact);
				// LOGGER.info(" propositions: " + f + " action: " + action);
			}
		}
	}

	@Override
	protected ArrayList<PGAction> createFactLayer(List<PGFact> pFacts, int pLayer) {
		memorised.add(new HashSet());
		ArrayList<PGAction> scheduledActs = new ArrayList<PGAction>();
		HashSet<MutexPair> newMutexes = new HashSet<MutexPair>();
		// LOGGER.info("createFactLayer: pFacts.size " + pFacts.size());
		for (PGFact f : pFacts) {
			// LOGGER.info("createFactLayer: f.layer " + f.layer);
			// if (f.layer < 0)
			if (f.layer < 0) {
				f.layer = pLayer;
				/*
				 * LOGGER.info("PlanningGraph->createFactLayer: f.enables " +
				 * f.enables.size());
				 */
				// LOGGER.info("PlanningGraph->createFactLayer: fact : " + f);
				constiderNotPreconditions(f);
				out: for (PGAction a : f.enables) {
					// LOGGER.info("PlanningGraph->createFactLayer: action : " +
					// a);
					// need to check for NOTs now that ADL is supported
					for (PGFact apc : a.conditions) {
						if (apc.fact instanceof Not) {
							if (pFacts.contains(((Not) apc.fact).literal) == true)
								continue out;
						}

					}

					scheduledActs.add(a);
				}
				// scheduledActs.addAll(f.enables);
				level_off = false;

				// calculate mutexes
				if (pLayer != 0) {
					Iterator pit = propositions.iterator();
					while (pit.hasNext()) {
						PGFact p = (PGFact) pit.next();
						if (p.layer >= 0 && this.checkPropMutex(f, p, pLayer)) {
							this.makeMutex(f, p, pLayer, newMutexes);
						}
					}
				}

			}
		}

		// check old mutexes
		Iterator pmit = propMutexes.iterator();
		while (pmit.hasNext()) {
			MutexPair m = (MutexPair) pmit.next();
			if (checkPropMutex(m, pLayer)) {
				this.makeMutex(m.node1, m.node2, pLayer, newMutexes);
			} else {
				level_off = false;
			}
		}

		// add new mutexes to old mutexes and remove those which have
		// disappeared
		propMutexes = newMutexes;

		return scheduledActs;
	}

	@Override
	protected ArrayList<PGFact> createActionLayer(List<PGAction> pActions, int pLayer) {
		level_off = true;
		HashSet<PGAction> actionSet = this.getAvailableActions(pActions, pLayer);
		// LOGGER.info("PlanningGraph:createActionLayer: actionSet size = " +
		// actionSet.size());
		actionSet.addAll(readyActions);
		readyActions = new HashSet<PGAction>();
		HashSet<PGAction> filteredSet = this.filterSet(actionSet, pLayer);
		// LOGGER.info("PlanningGraph:createActionLayer: filteredSet size = " +
		// filteredSet.size());
		ArrayList<PGFact> scheduledFacts = this.calculateActionMutexesAndProps(filteredSet, pLayer);
		return scheduledFacts;
	}

	@Override
	public TotalOrderPlan getPlan(State s) {
		setInitial(s);
		resetAll(s);
		// AND oldGoal = new AND(this.goal);
		setGoal(s.goal);

		// set up the intital set of facts
		List<PGFact> scheduledFacts = new ArrayList<PGFact>(initial);
		List<PGAction> scheduledActs = null;

		scheduledActs = createFactLayer(scheduledFacts, 0);
		List plan = null;

		//
		HashSet<Fact> realInitial = new HashSet<Fact>();
		for (PGFact i : this.initial) {
			constiderNotPreconditions(i);
			realInitial.add(i.fact);
		}

		this.factLayers.add(realInitial); // add current layer
		// this.pgFactLayers.add(scheduledFacts); //add current layer

		// create the graph==========================================
		while (true) {
			scheduledFacts = createActionLayer(scheduledActs, num_layers);
			++num_layers;
			scheduledActs = createFactLayer(scheduledFacts, num_layers);

			if (scheduledFacts != null) {
				HashSet factList = new HashSet();
				// plan = extractPlan();
				for (Object pgp : scheduledFacts)
					factList.add(((PGFact) pgp).fact);

				factList.addAll(this.factLayers.get(num_layers - 1));

				this.factLayers.add(factList); // add current layer

			}

			if (goalMet() && !goalMutex()) {
				plan = extractPlan();
			}
			if (plan != null)
				break;
			if (!level_off)
				numeric_level_off = 0;
			if (level_off || numeric_level_off >= NUMERIC_LIMIT) {
				// printGraph();
				break;
			}
		}

		TotalOrderPlan p = null;
		if (plan != null) {
			p = new TotalOrderPlan();
			Iterator pit = plan.iterator();
			while (pit.hasNext()) {
				PGAction a = (PGAction) pit.next();
				if (!(a instanceof PGNoOp))
					p.addAction(a.action);
			}
			// p.print(javaff.JavaFF.infoOutput);
			return p;
		}
		// this.setGoal(oldGoal);

		return p;

	}

	private void constiderNotPreconditions() {
		LOGGER.info("PRINT propositions");
		for (Object o : propositions) {
			PGFact f = (PGFact) o;
			for (PGAction action : notPrecondtionAction) {
				Set<Fact> pcs = action.action.getPreconditions();
				boolean doesntAllowPrecondition = false;
				for (Fact p : pcs) {
					if (p instanceof Not && f.equals(new Not(p))) {
						doesntAllowPrecondition = true;
						break;
					}
				}
				if (!doesntAllowPrecondition) {
					f.enables.add(action);
					// LOGGER.info(" propositions: " + f + " action: " +
					// action);
				}
			}

		}
	}

	public List searchRelaxedPlan(Set<PGFact> goalSet, int l) {
		if (l == 0)
			return new ArrayList();
		Set chosenActions = new HashSet();
		// loop through actions to achieve the goal set
		for (PGFact g : goalSet) {
			PGAction a = null;
			for (PGAction na : g.achievedBy) {
				if (na.layer < l && na.layer >= 0) {
					if (na instanceof PGNoOp) {
						a = na;
						break; // always choose NO-Ops if they exist
					} else if (chosenActions.contains(na)) {
						a = na;
						break;
					} else {
						if (a == null)
							a = na;
						else if (a.difficulty > na.difficulty) // this is the
																// "min" in
																// h_add
							a = na;
					}
				}
			}

			if (a != null) {
				chosenActions.add(a);
			}
		}

		Set newGoalSet = new HashSet();
		// loop through chosen actions adding in propositions and comparators
		Iterator cait = chosenActions.iterator();
		while (cait.hasNext()) {
			PGAction ca = (PGAction) cait.next();
			newGoalSet.addAll(ca.conditions);
		}

		List rplan = this.searchRelaxedPlan(newGoalSet, l - 1);
		rplan.addAll(chosenActions);
		return rplan;
	}

	@Override
	public boolean checkPropMutex(MutexPair m, int l) {
		return false;
	}

	@Override
	public boolean checkPropMutex(PGFact p1, PGFact p2, int l) {
		return false;
	}

	@Override
	public boolean checkActionMutex(MutexPair m, int l) {
		return false;
	}

	@Override
	public boolean checkActionMutex(PGAction a1, PGAction a2, int l) {
		return false;
	}

	@Override
	protected boolean noMutexes(Set s, int l) {
		return true;
	}

	@Override
	protected boolean noMutexesTest(Node n, Set s, int l) // Tests to see if
															// there is a mutex
															// between n and all
															// nodes in s
	{
		return true;
	}

	@Override
	public void setGoal(Fact g) {
		super.setGoal(g);
	}
	//
	// /**
	// * Creates the RPG, returns null.
	// */
	// @Override
	// public TotalOrderPlan getPlan(State s)
	// {
	// setInitial(s);
	// resetAll(s);
	//
	// // set up the intital set of facts
	// Set scheduledFacts = new HashSet(initial);
	// List scheduledActs = null;
	//
	// scheduledActs = createFactLayer(scheduledFacts, 0);
	// List plan = null;
	//
	// this.factLayers.add(new ArrayList(initial)); //add current layer
	//
	// // create the graph==========================================
	// while (true)
	// {
	// scheduledFacts = createActionLayer(scheduledActs, num_layers);
	// ++num_layers;
	// scheduledActs = createFactLayer(scheduledFacts, num_layers);
	//
	// if (scheduledFacts != null)
	// {
	// List factList = new ArrayList();
	// //plan = extractPlan();
	// for (Object pgp : scheduledFacts)
	// factList.add(((PGProposition)pgp).proposition);
	//
	// this.factLayers.add(factList); //add current layer
	//
	// }
	//
	//
	// if (scheduledActs.size() == 0 && scheduledFacts.size() == 0)
	// plan = extractPlan();
	//
	// if (plan != null)
	// break;
	// if (!level_off)
	// numeric_level_off = 0;
	// if (level_off || numeric_level_off >= NUMERIC_LIMIT)
	// {
	// // printGraph();
	// break;
	// }
	// }
	//
	// if (plan != null)
	// {
	// Iterator pit = plan.iterator();
	// TotalOrderPlan p = new TotalOrderPlan();
	// while (pit.hasNext())
	// {
	// PGAction a = (PGAction) pit.next();
	// if (!(a instanceof PGNoOp))
	// p.addAction(a.action);
	// }
	// // p.print(javaff.JavaFF.infoOutput);
	// return p;
	// } else
	// return null;
	//
	// }

}