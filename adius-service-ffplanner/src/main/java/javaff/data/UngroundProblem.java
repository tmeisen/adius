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
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javaff.data.adl.Imply;
import javaff.data.adl.Quantifier;
import javaff.data.metric.BinaryComparator;
import javaff.data.metric.Function;
import javaff.data.metric.FunctionSymbol;
import javaff.data.metric.NamedFunction;
import javaff.data.strips.And;
import javaff.data.strips.Constant;
import javaff.data.strips.Not;
import javaff.data.strips.Operator;
import javaff.data.strips.PDDLObject;
import javaff.data.strips.Predicate;
import javaff.data.strips.PredicateSymbol;
import javaff.data.strips.Proposition;
import javaff.data.strips.SimpleType;
import javaff.data.strips.SingleLiteral;
import javaff.data.strips.UngroundInstantAction;

public class UngroundProblem {
	/**
	 * If set to True, grounding will also ground out all static facts which
	 * would otherwise only slow down search. Defaults to false. Must be set
	 * prior to calling ground().
	 */
	public static boolean GroundStatics = false;

	public String DomainName = ""; // Name of Domain
	public String ProblemName = ""; // Name of Problem
	public String ProblemDomainName = ""; // Name of Domain as specified by the
											// Problem

	public Set<String> requirements = new HashSet<String>(); // Requirements of
																// the domain
	// (String)

	public Set<Type> types = new HashSet<Type>(); // For simple object types in
													// this
	// domain (SimpleTypes)
	public Map<String, Type> typeMap = new Hashtable<String, Type>(); // Set for
																		// mapping
																		// String
																		// ->
																		// types
	// (String => Type)
	public Map<Type, Set<PDDLObject>> typeSets = new Hashtable<Type, Set<PDDLObject>>(); // Maps
																							// a
																							// type
																							// on
																							// to
																							// a
																							// set
																							// of
	// PDDLObjects (Type => Set
	// (PDDLObjects))

	public Set<PredicateSymbol> predSymbols = new HashSet<PredicateSymbol>(); // Set
																				// of
																				// all
																				// (ungrounded)
																				// predicate
	// (PredicateSymbol)
	public Map<String, PredicateSymbol> predSymbolMap = new Hashtable<String, PredicateSymbol>(); // Maps
																									// Strings
																									// of
																									// the
																									// symbol
																									// to
	// the Symbols (String =>
	// PredicateSymbol)

	public Set<Constant> constants = new HashSet<Constant>(); // Set of all
																// constant
																// (PDDLObjects)
	public Map<String, Constant> constantMap = new Hashtable<String, Constant>(); // Maps
																					// Strings
																					// of
																					// the
																					// constant
	// to the PDDLObject

	public Set<FunctionSymbol> funcSymbols = new HashSet<FunctionSymbol>(); // Set
																			// of
																			// all
																			// function
																			// symbols
	// (FunctionSymbol)
	public Map<String, FunctionSymbol> funcSymbolMap = new Hashtable<String, FunctionSymbol>(); // Maps
																								// Strings
																								// onto
																								// the
																								// Symbols
	// (String => FunctionSymbol)

	public Set<Operator> actions = new HashSet<Operator>(); // List of all
															// (ungrounded)
															// actions
	// (Operators)

	public Set<PDDLObject> objects = new HashSet<PDDLObject>(); // Objects in
																// the problem
																// (PDDLObject)
	public Map<String, PDDLObject> objectMap = new Hashtable<String, PDDLObject>(); // Maps
																					// Strings
																					// onto
																					// PDDLObjects
	// (String => PDDLObject)

	public Set<Proposition> initial = new HashSet<Proposition>(); // Set of
																	// initial
																	// facts
																	// (Proposition)
	public Map<NamedFunction, BigDecimal> funcValues = new Hashtable<NamedFunction, BigDecimal>(); // Maps
																									// functions
																									// onto
																									// numbers
	// (NamedFunction => BigDecimal)
	public GroundFact goal = new And();

	public Metric metric;

	public Map<PredicateSymbol, Set<Proposition>> staticPropositionMap = new Hashtable<PredicateSymbol, Set<Proposition>>(); // (PredicateName
																																// =>
																																// Set
	// (Proposition))

	public TypeGraph TypeGraph = new TypeGraph();

	public UngroundProblem() {
		typeMap.put(Type.rootType.toString(), Type.rootType);
	}

	public void setupQuantifiers() {
		for (Operator o : this.actions) {
			if (o instanceof UngroundInstantAction) {
				UngroundInstantAction a = (UngroundInstantAction) o;
				for (Fact pc : a.condition.getFacts()) {
					if (pc instanceof Quantifier) {
						((Quantifier) pc).setQuantifiedObjects(this.typeSets.get(((Quantifier) pc).getVariable()
								.getType()));

					}
				}
				for (Fact pc : a.effect.getFacts()) {
					if (pc instanceof Quantifier) {
						((Quantifier) pc).setQuantifiedObjects(this.typeSets.get(((Quantifier) pc).getVariable()
								.getType()));

					}
				}
			}

		}
	}

	public GroundProblem groundEverything() {
		// long startTime = System.nanoTime();
		calculateStatics();
		makeStaticPropositionMap();
		// long afterStatics = System.nanoTime();
		buildTypeSets();
		// long afterTypeSets = System.nanoTime();
		Set<Action> groundActions = new HashSet<Action>();
		Iterator<Operator> ait = actions.iterator();
		while (ait.hasNext()) {
			Operator o = ait.next();
			Set<Action> s = o.ground(this);
			groundActions.addAll(s);
		}
		// long afterActions = System.nanoTime();

		for (Object ao : groundActions) {
			Action a = (Action) ao;
			for (Object co : a.getComparators()) {
				// System.out.println("found comparator "+co+" in "+a);
				BinaryComparator bc = (BinaryComparator) co;
				NamedFunction namef = (NamedFunction) bc.first;

				if (this.funcValues.containsKey(namef) == false)
					this.funcValues.put(namef, new BigDecimal(0));
			}
		}
		// long afterFunc = System.nanoTime();

		// static-ify the functions
		// Iterator<Action> gait = groundActions.iterator();
		// while (gait.hasNext())
		// {
		// Action a = (Action) gait.next();
		// a.staticify(funcValues);
		// // System.out.println("Statified "+a);
		// }
		// long afterStaticify = System.nanoTime();

		// remove static functions from the intial tmstate
		// removeStaticsFromInitialState();

		// -could put in code here to
		// a) get rid of static functions in initial tmstate - DONE
		// b) get rid of static predicates in initial tmstate - DONE
		// c) get rid of static propositions in the actions (this may have
		// already been done)
		// d) get rid of no use actions (i.e. whose preconditions can't be
		// achieved) - DONE - David Pattison, 23/5/2011

		GroundProblem rGP = new GroundProblem(groundActions, initial, goal, funcValues, metric);
		// long afterCreate = System.nanoTime();
		rGP.name = this.DomainName;// +"_-_"+this.ProblemName;

		// System.out.println("Statics: "+(afterStatics -
		// startTime)/1000000000f);
		// System.out.println("Type sets: "+(afterTypeSets -
		// afterStatics)/1000000000f);
		// System.out.println("Actions: "+(afterActions -
		// afterTypeSets)/1000000000f);
		// System.out.println("Functions: "+(afterFunc -
		// afterActions)/1000000000f);
		// System.out.println("Staticify: "+(afterStaticify -
		// afterFunc)/1000000000f);
		// System.out.println("Creation: "+(afterCreate -
		// afterStaticify)/1000000000f);

		return rGP;
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
		this.decompileFact(f, lits);
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

			for (And dec : ands) {
				this.decompileFact(dec, existing);
			}
			// existing.add(f);

			// PGFact apgp = this.getProposition(((Imply)f.fact).getA());
			// PGFact bpgp = this.getProposition(((Imply)f.fact).getB());
			//
			// this.decompileFact(apgp, existing);
			// this.decompileFact(bpgp, existing);
		} else if (f instanceof CompoundLiteral) {
			for (Fact cf : f.getFacts()) {
				this.decompileFact(cf, existing);
			}
		} else if (f instanceof SingleLiteral) {
			existing.add(f);
		} else if (f instanceof Not) {
			existing.add(f);
		} else if (f instanceof Function) {
			existing.add(f);
		} else
			throw new IllegalArgumentException("Cannot decompile fact " + f + " - unknown type");
	}

	/**
	 * To be called in the event that ground() will not be called at some point.
	 * Detects static facts and constructs type sets. If not explicitly called
	 * here, this will never happen.
	 * 
	 * Calling this on a Temporal problem will cause a crash.
	 */
	public void postProcess() {
		calculateStatics();
		makeStaticPropositionMap();
		buildTypeSets();

		// now run through all actions and modify whether their PCs are static-
		// because
		// they have a different object reference
		for (Operator ao : this.actions) {
			UngroundInstantAction a = (UngroundInstantAction) ao;
			if (a.condition instanceof And) {
				And and = (And) a.condition;
				for (Object lo : and.literals) {
					Literal l = (Literal) lo;
					l.getPredicateSymbol().setStatic(this.predSymbolMap.get(l.name.toString()).isStatic());
				}
			} else if (a.condition instanceof Predicate) {
				((Predicate) a.condition).getPredicateSymbol().setStatic(
						this.predSymbolMap.get(((Predicate) a.condition).name.toString()).isStatic());
			}
		}

	}

	public GroundProblem ground() {
		if (UngroundProblem.GroundStatics) {
			return this.groundEverything();
		}

		calculateStatics();
		makeStaticPropositionMap();
		buildTypeSets();
		Set<Action> groundActions = new HashSet<Action>();
		Iterator<Operator> ait = actions.iterator();
		while (ait.hasNext()) {
			Operator o = ait.next();
			Set<Action> s = o.ground(this);
			groundActions.addAll(s);
		}

		// static-ify the functions
		Iterator<Action> gait = groundActions.iterator();
		while (gait.hasNext()) {
			Action a = gait.next();
			a.staticify(funcValues);
		}

		// remove static functions from the intial tmstate
		removeStaticsFromInitialState();

		// -could put in code here to
		// a) get rid of static functions in initial tmstate - DONE
		// b) get rid of static predicates in initial tmstate - DONE
		// c) get rid of static propositions in the actions (this may have
		// already been done)
		// d) get rid of no use actions (i.e. whose preconditions can't be
		// achieved)

		GroundProblem rGP = new GroundProblem(groundActions, initial, goal, funcValues, metric);
		rGP.name = this.DomainName;// +"_-_"+this.ProblemName;
		return rGP;
	}

	public void buildTypeSets() // builds typeSets for easy access of all
								// the objects of a particular type
	{
		Iterator<Type> tit = types.iterator();
		while (tit.hasNext()) {
			SimpleType st = (SimpleType) tit.next();
			Set<PDDLObject> s = new HashSet<PDDLObject>();
			typeSets.put(st, s);

			Iterator<PDDLObject> oit = objects.iterator();
			while (oit.hasNext()) {
				PDDLObject o = oit.next();
				if (o.isOfType(st))
					s.add(o);
			}

			Iterator<Constant> cit = constants.iterator();
			while (cit.hasNext()) {
				Object next = cit.next();
				Constant c = (Constant) next;
				// PDDLObject c = new PDDLObject(constant.getName(),
				// constant.getType());// (PDDLObject) next;
				if (c.isOfType(st))
					s.add(c);
			}
		}

		Set<PDDLObject> s = new HashSet<PDDLObject>(objects);
		s.addAll(constants);
		typeSets.put(Type.rootType, s);
	}

	private void calculateStatics() // Determines whether the predicateSymbols
									// and funcSymbols are static or not
	{
		Iterator<PredicateSymbol> pit = predSymbols.iterator();
		while (pit.hasNext()) {
			boolean isStatic = true;
			PredicateSymbol ps = pit.next();
			Iterator<Operator> oit = actions.iterator();
			while (oit.hasNext() && isStatic) {
				Operator o = oit.next();
				isStatic = !o.effects(ps);
			}
			ps.setStatic(isStatic);
		}

		Iterator<FunctionSymbol> fit = funcSymbols.iterator();
		while (fit.hasNext()) {
			boolean isStatic = true;
			FunctionSymbol fs = fit.next();
			Iterator<Operator> oit = actions.iterator();
			while (oit.hasNext() && isStatic) {
				Operator o = oit.next();
				isStatic = !o.effects(fs);
			}
			fs.setStatic(isStatic);
		}
	}

	private void makeStaticPropositionMap() {
		Iterator<PredicateSymbol> pit = predSymbols.iterator();
		while (pit.hasNext()) {
			PredicateSymbol ps = pit.next();
			if (ps.isStatic()) {
				staticPropositionMap.put(ps, new HashSet<Proposition>());
			}
		}

		Iterator<Proposition> iit = initial.iterator();
		while (iit.hasNext()) {
			Proposition p = iit.next();
			if (p.name.isStatic() && this.initial.contains(p)) // second
																// condition
																// eliminate any
																// illegal
																// static facts,
																// ie
																// unachievable
			{
				Set<Proposition> pset = staticPropositionMap.get(p.name);
				pset.add(p);
			}
		}
	}

	private void removeStaticsFromInitialState() {
		// remove static functions
		/*
		 * Iterator fit = funcValues.keySet().iterator(); Set staticFuncs = new
		 * HashSet(); while (fit.hasNext()) { NamedFunction nf = (NamedFunction)
		 * fit.next(); if (nf.isStatic()) staticFuncs.add(nf); } fit =
		 * staticFuncs.iterator(); while (fit.hasNext()) { Object o =
		 * fit.next(); funcValues.remove(o); }
		 */

		// remove static Propositions
		Iterator<Proposition> init = initial.iterator();
		Set<Proposition> staticProps = new HashSet<Proposition>();
		while (init.hasNext()) {
			Proposition p = init.next();
			if (p.isStatic())
				staticProps.add(p);
		}
		initial.removeAll(staticProps);
	}

	@Override
	public Object clone() {
		UngroundProblem clone = new UngroundProblem();
		clone.actions = new HashSet<Operator>(this.actions);
		clone.constantMap = new Hashtable<String, Constant>(this.constantMap);
		clone.constants = new HashSet<Constant>(this.constants);
		clone.DomainName = this.DomainName;
		clone.funcSymbolMap = new Hashtable<String, FunctionSymbol>(this.funcSymbolMap);
		clone.funcSymbols = new HashSet<FunctionSymbol>(this.funcSymbols);
		clone.funcValues = new Hashtable<NamedFunction, BigDecimal>(this.funcValues);
		clone.goal = (GroundFact) this.goal.clone();
		clone.initial = new HashSet<Proposition>(this.initial);
		clone.metric = this.metric; // FIXME shallow clone
		clone.objectMap = new Hashtable<String, PDDLObject>(this.objectMap);
		clone.objects = new HashSet<PDDLObject>(this.objects);
		clone.predSymbolMap = new Hashtable<String, PredicateSymbol>(this.predSymbolMap);
		clone.predSymbols = new HashSet<PredicateSymbol>(this.predSymbols);
		clone.ProblemDomainName = this.ProblemDomainName;
		clone.ProblemName = this.ProblemName;
		clone.requirements = new HashSet<String>(this.requirements);
		clone.staticPropositionMap = new Hashtable<PredicateSymbol, Set<Proposition>>(this.staticPropositionMap);
		clone.typeMap = new Hashtable<String, Type>(this.typeMap);
		clone.types = new HashSet<Type>(this.types);
		clone.typeSets = new Hashtable<Type, Set<PDDLObject>>(this.typeSets);
		clone.TypeGraph = (TypeGraph) TypeGraph.clone();
		return clone;
	}

	@Override
	public String toString() {
		return "UngroundProblem: " + this.DomainName + "_-_" + this.ProblemName;
	}
}