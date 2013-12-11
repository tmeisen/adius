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

package javaff;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import javaff.data.Action;
import javaff.data.Fact;
import javaff.data.GroundProblem;
import javaff.data.Metric;
import javaff.data.Plan;
import javaff.data.TimeStampedPlan;
import javaff.data.TotalOrderPlan;
import javaff.data.UngroundProblem;
import javaff.data.adl.Exists;
import javaff.data.adl.ForAll;
import javaff.data.adl.Imply;
import javaff.data.adl.Or;
import javaff.data.metric.NumberFunction;
import javaff.data.strips.And;
import javaff.data.strips.InstantAction;
import javaff.data.strips.Not;
import javaff.data.temporal.DurativeAction;
import javaff.parser.PDDL21Parser;
import javaff.planning.HelpfulFilter;
import javaff.planning.NullFilter;
import javaff.planning.RelaxedPlanningGraph;
import javaff.planning.RelaxedTemporalMetricPlanningGraph;
import javaff.planning.State;
import javaff.planning.TemporalMetricState;
import javaff.search.BestFirstSearch;
import javaff.search.EnforcedHillClimbingSearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the FF planner in Java. The planner currently only
 * supports STRIPS style planning, but as it is a branch of the CRIKEY planner,
 * the components for both Temporal and Metric planning exist, but are unused.
 * 
 * @author Keith Halsey
 * @author Amanda Coles
 * @author Andrew Coles
 * @author David Pattison
 */
public class JavaFF {
	private static final Logger LOGGER = LoggerFactory.getLogger(JavaFF.class);

	public static BigDecimal EPSILON = new BigDecimal(0.01);
	public static BigDecimal MAX_DURATION = new BigDecimal("100000");
	public static Random generator = new Random();

	private String domain;

	private File domainFile;

	private File useOutputFile;

	public JavaFF(String domain) {
		this.domain = domain;
		this.domainFile = null;
		this.useOutputFile = null;
	}

	public JavaFF(File domain, File solutionFile) {
		this.domain = null;
		this.domainFile = domain;
		this.useOutputFile = solutionFile;
	}

	public static void main(String args[]) {
		EPSILON = EPSILON.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		MAX_DURATION = MAX_DURATION.setScale(2, BigDecimal.ROUND_HALF_EVEN);

		if (args.length < 2) {
			System.out.println("Parameters needed: domainFile.pddl problemFile.pddl [random seed] [outputfile.sol]");

		} else {
			File domainFile = new File(args[0]);
			File problemFile = new File(args[1]);
			File solutionFile = null;
			if (args.length > 2) {
				if (args[2].endsWith(".sol") || args[2].endsWith(".soln")) {
					solutionFile = new File(args[2]);
				} else {
					generator = new Random(Integer.parseInt(args[2]));
					if (args.length > 3) {
						solutionFile = new File(args[3]);
					}
				}
			}

			JavaFF planner = new JavaFF(domainFile, solutionFile);
			planner.plan(problemFile);
		}
	}

	/**
	 * Constructs plans over several problem files.
	 * 
	 * @param path
	 *            The path to the folder containing the problem files.
	 * @param filenamePrefix
	 *            The prefix of each problem file, usually "pfile".
	 * @param pfileStart
	 *            The start index which will be appended to the filenamePrefix.
	 * @param pfileEnd
	 *            The index of the last problem file.
	 * @param usePDDLpostfix
	 *            Whether to use ".pddl" at the end of the problem files.
	 *            Domains are assumed to already have this.
	 * @return A totally ordered plan.
	 */
	public List<Plan> plan(String path, String filenamePrefix, int pfileStart, int pfileEnd, boolean usePDDLpostfix) {
		List<Plan> plans = new ArrayList<Plan>(pfileEnd - pfileStart);
		for (int i = pfileStart; i < pfileEnd; i++) {
			String postfix = "" + i;
			if (i < 10)
				postfix = "0" + i;
			if (usePDDLpostfix)
				postfix = postfix + ".pddl";

			File pfile = new File(path + "/" + filenamePrefix + postfix);
			plans.add(this.plan(pfile));
		}

		return plans;
	}

	/**
	 * Construct a plan from the ground problem provided. This method foregoes
	 * any parsing required by plan(File), but due to this a path to the
	 * original problem file is required for translation into SAS+.
	 * 
	 * @param gproblem
	 *            A grounded problem.
	 * @param pfilePath
	 *            An absolute path to the problem file from which the
	 *            GroundProblem was created.
	 * @return A totally ordered plan.
	 */
	public Plan plan(GroundProblem gproblem)// , String pfilePath)
	{
		return doPlan(gproblem);// , pfilePath);
	}

	/**
	 * Construct a plan for the single problem file provided. Obviously this
	 * problem must be intended for the domain associated with this object. @see
	 * JavaFF.getDomainFile(). Note- This method should only be called if there
	 * exists no UngroundProblem or GroundProblem instance in the program.
	 * Otherwise, use plan(GroundProblem, String).
	 * 
	 * @param pFile
	 *            The file to parse.
	 * @return A totally ordered plan.
	 */
	public Plan plan(File pFile) {
		Plan plan = this.doFilePlan(pFile);

		if (plan != null) {
			if (useOutputFile != null) {
				writePlanToFile(plan, useOutputFile);
			}
		}
		return plan;
	}

	public Plan plan(String problem) {
		Plan plan = this.doStringPlan(problem);
		return plan;
	}

	protected Plan doPlan(GroundProblem ground) {
		long startTime = System.currentTimeMillis();
		long afterBFSPlanning = 0, afterEHCPlanning = 0;

		LOGGER.debug("Action set has " + ground.actions.size() + " in it");
		LOGGER.debug("Final Action set is " + ground.actions.size());

		// construct init
		Set<Action> na = new HashSet<Action>();
		Set<Action> ni = new HashSet<Action>();
		Iterator<Action> ait = ground.actions.iterator();
		while (ait.hasNext()) {
			Action act = ait.next();
			if (act instanceof InstantAction) {
				// LOGGER.debug("Action " + act);
				na.add(act);
				ni.add(act);
			} else if (act instanceof DurativeAction) {
				DurativeAction dact = (DurativeAction) act;
				na.add(dact.startAction);
				na.add(dact.endAction);
				ni.add(dact.startAction);
			}
		}

		Metric metric;
		if (ground.metric == null)
			metric = new Metric(Metric.MINIMIZE, new NumberFunction(Metric.MINIMIZE));
		else
			metric = ground.metric;

		LOGGER.debug("About to create init tmstate");
		TemporalMetricState ts = new TemporalMetricState(ni, ground.tmstate.facts, ground.goal, ground.functionValues,
				metric);
		LOGGER.debug("About to create gp");
		GroundProblem gp = new GroundProblem(na, ground.tmstate.facts, ground.goal, ground.functionValues, metric);
		LOGGER.debug("Creating RPG");
		// ts.setRPG(new RelaxedPlanningGraph(gp)); change!!!
		ts.setRPG(ground.getTemporalMetricInitialState().getRPG());
		LOGGER.debug("Creating ts.setRPG");
		TemporalMetricState currentInitState = ts;

		TemporalMetricState originalInitState = (TemporalMetricState) currentInitState.clone(); // get
																								// default
																								// init
																								// tmstate
																								// then
																								// modify
		LOGGER.debug("Creating clone");
		State goalState = null;
		TotalOrderPlan bestPlan = null;
		TotalOrderPlan ehcPlan = null;
		TotalOrderPlan bfsPlan = null;

		LOGGER.debug("Running FF with EHC...");
		goalState = performFFSearch(currentInitState, true, false);

		afterEHCPlanning = System.currentTimeMillis();
		if (goalState != null) {
			LOGGER.info("Found EHC plan: ");
			bestPlan = (TotalOrderPlan) goalState.getSolution();
			afterEHCPlanning = System.currentTimeMillis();
			double planningEHCTime = (afterEHCPlanning - startTime) / 1000.00;
			LOGGER.info("EHC Planning Time =\t" + planningEHCTime + "sec");
			LOGGER.info("Plan length is " + bestPlan.getLinearActions().size());
			ehcPlan = bestPlan;
		} else {
			currentInitState = (TemporalMetricState) originalInitState.clone();
			LOGGER.debug("Running FF with BFS...");
			goalState = performFFSearch(currentInitState, false, true);
			afterBFSPlanning = System.currentTimeMillis();
			if (goalState != null) {
				bfsPlan = (TotalOrderPlan) goalState.getSolution();
				if (ehcPlan == null || bfsPlan.getPlanLength() < ehcPlan.getPlanLength()) {
					bestPlan = bfsPlan;

					LOGGER.debug("Found BFS plan: ");

					double planningBFSTime = (afterBFSPlanning - afterEHCPlanning) / 1000.00;
					LOGGER.info("BFS Planning Time =\t" + planningBFSTime + "sec");
					LOGGER.info("Plan length is " + bestPlan.getLinearActions().size());
				}
			}
		}

		TimeStampedPlan tsp = null;
		if (bestPlan != null) {
			LOGGER.info("Final plan...");
			LOGGER.info(bestPlan.toString());

			// ***************0*****************
			// Schedule a plan
			// ********************************
			int counter = 0;
			tsp = new TimeStampedPlan();
			for (Object a : bestPlan.getLinearActions())
				tsp.addAction((Action) a, new BigDecimal(counter++));
			if (tsp != null) {
				LOGGER.debug(tsp.toString());
				LOGGER.debug("Final plan length is " + tsp.actions.size());
			}
		} else {
			LOGGER.error("No plan found");
		}

		long afterScheduling = System.currentTimeMillis();
		double planningEHCTime = (afterEHCPlanning - startTime) / 1000.00;
		double planningBFSTime = (afterBFSPlanning - afterEHCPlanning) / 1000.00;
		double schedulingTime = (afterScheduling - afterBFSPlanning) / 1000.00;

		LOGGER.debug("EHC Plan Time =\t" + planningEHCTime + "sec");
		LOGGER.debug("BFS Plan Time =\t" + planningBFSTime + "sec");
		LOGGER.debug("Scheduling Time =\t" + schedulingTime + "sec");

		return bestPlan;
	}

	protected Plan doStringPlan(String problem) {
		long startTime = System.currentTimeMillis();
		LOGGER.debug("Domain decription: " + this.domain);
		UngroundProblem unground = PDDL21Parser.parseStrings(this.domain, problem);
		if (unground == null) {
			LOGGER.error("Parsing error - see console for details");
			return null;
		}
		LOGGER.debug("Grounding...");
		GroundProblem ground = unground.ground();
		LOGGER.debug("Grounding complete");
		LOGGER.debug("Decompiling ADL...");
		int previousActionCount = ground.actions.size();
		this.decompileADL(ground);
		int adlActionCount = ground.actions.size();
		LOGGER.debug("Decompiling ADL complete");
		LOGGER.debug(previousActionCount + " actions before ADL, " + adlActionCount + " after");
		LOGGER.debug("Performing RPG eachability analysis...");
		LOGGER.debug("Reachability analysis complete");

		LOGGER.debug("Computing initial state and RPG...");
		ground.getTemporalMetricInitialState();
		LOGGER.debug("RPG complete");
		long afterGrounding = System.currentTimeMillis();
		double groundingTime = (afterGrounding - startTime) / 1000.00;
		LOGGER.debug("Instantiation Time =\t\t" + groundingTime + "sec");
		return doPlan(ground);
	}

	protected Plan doFilePlan(File pFile) {

		// ********************************
		// Parse and Ground the Problem
		// ********************************
		long startTime = System.currentTimeMillis();
		UngroundProblem unground = PDDL21Parser.parseFiles(domainFile, pFile);

		if (unground == null) {
			LOGGER.error("Parsing error - see console for details");
			return null;
		}
		LOGGER.debug("Grounding...");
		GroundProblem ground = unground.ground();
		LOGGER.debug("Grounding complete");
		LOGGER.debug("Decompiling ADL...");
		int previousActionCount = ground.actions.size();
		this.decompileADL(ground);
		int adlActionCount = ground.actions.size();
		LOGGER.debug("Decompiling ADL complete");
		LOGGER.debug(previousActionCount + " actions before ADL, " + adlActionCount + " after");
		LOGGER.debug("Performing RPG eachability analysis...");
		LOGGER.debug("Reachability analysis complete");

		LOGGER.debug("Computing initial state and RPG...");
		ground.getTemporalMetricInitialState();
		LOGGER.debug("RPG complete");
		long afterGrounding = System.currentTimeMillis();
		double groundingTime = (afterGrounding - startTime) / 1000.00;
		LOGGER.debug("Instantiation Time =\t\t" + groundingTime + "sec");
		return this.doPlan(ground);
	}

	private void decompileADL(GroundProblem ground) {
		Set<Action> refinedActions = new HashSet<Action>();

		// keep a queue of potentially-ADL actions. Add partially compiled out
		// actions to it. When
		// no ADL constructs exist in the PCs (actions unsupported for now), it
		// can be added to the set of
		// legal actions.
		Queue<Action> queue = new LinkedList<Action>(ground.actions);
		out: while (queue.isEmpty() == false) {
			Action a = queue.remove();

			for (Fact pc : a.getPreconditions()) {
				if (pc instanceof Imply) {
					Set<And> strips = ((Imply) pc).getSTRIPSConjunctions();

					for (And stripsPC : strips) {
						if (a instanceof InstantAction) {
							InstantAction actionClone = (InstantAction) a.clone();
							And modifiedPCs = null;
							if (actionClone.getCondition() instanceof And) {
								modifiedPCs = (And) actionClone.getCondition().clone();
							} else // not an AND, so make it one
							{
								modifiedPCs = new And((Fact) actionClone.getCondition().clone());
							}

							modifiedPCs.literals.remove(pc);

							modifiedPCs.literals.addAll(stripsPC.getFacts());
							// modifiedPCs.add(stripsPC);

							actionClone.setCondition(modifiedPCs);
							queue.add(actionClone);
						} else {
							DurativeAction stripsClone = (DurativeAction) a.clone();
							And modifiedPCs = new And((Fact) stripsClone.startCondition.clone());
							modifiedPCs.literals.remove(pc);

							modifiedPCs.add(stripsPC);

							stripsClone.startCondition = modifiedPCs;

							queue.add(stripsClone);
						}
					}
					continue out;
				} else if (pc instanceof ForAll) {
					// if it is grounded this should be a single And
					And compiledOut = (And) ((ForAll) pc).getFacts().iterator().next();

					if (a instanceof InstantAction) {
						InstantAction actionClone = (InstantAction) a.clone();

						Set<Fact> modifiedPCs = a.getPreconditions();
						modifiedPCs.remove(pc);

						modifiedPCs.add(compiledOut);

						actionClone.setCondition(new And(modifiedPCs));
						queue.add(actionClone);
					} else {
						// DurativeAction stripsClone = (DurativeAction)
						// a.clone();
						//
						// stripsClone.startCondition = and;
						// queue.add(stripsClone);
					}
					continue out;
				} else if (pc instanceof Exists) {
					throw new IllegalArgumentException("Decompiling Exists not yet supported");
				} else if (pc instanceof Or) {
					throw new IllegalArgumentException("Decompiling Or not yet supported");
				} else if (pc instanceof Not) {
					// ignore- Nots are legal
				}
			}
			refinedActions.add(a);

		}

		ground.actions = refinedActions;
	}

	protected State performFFSearch(TemporalMetricState initialState, boolean useEHC, boolean useBFS) {
		if (!useEHC && !useBFS)
			return null;

		State goalState = null;

		// LOGGER.info("INIT " + initialState.facts + "\nGOAL "
		// + initialState.goal);
		if (useEHC) {
			LOGGER.info("Performing search using EHC with standard helpful action filter");

			EnforcedHillClimbingSearch EHCS = new EnforcedHillClimbingSearch(initialState);

			// EHCS.setFilter(HelpfulFilter.getInstance()); // and use the
			// helpful
			EHCS.setFilter(HelpfulFilter.getInstance());
			// // actions neighbourhood

			// Try and find a plan using EHC
			goalState = EHCS.search();

			if (goalState != null)
				return goalState;
			else
				LOGGER.info("Failed to find solution using EHC");
		}
		if (useBFS) {
			LOGGER.info("Performing search using BFS");

			// create a Best-First Searcher
			BestFirstSearch BFS = new BestFirstSearch(initialState);
			BFS.setFilter(NullFilter.getInstance());
			goalState = BFS.search();

			if (goalState == null)
				LOGGER.info("Failed to find solution using BFS");
		}
		return goalState;
	}

	protected void writePlanToFile(Plan plan, File fileOut) {
		try {
			fileOut.delete();
			fileOut.createNewFile();
			FileOutputStream outputStream = new FileOutputStream(fileOut);
			PrintWriter printWriter = new PrintWriter(outputStream);
			plan.print(printWriter);
			printWriter.close();
		} catch (IOException e) {
			LOGGER.error(e.toString());
		}
	}

	public File getDomainFile() {
		return domainFile;
	}

	public void setDomainFile(File domainFile) {
		this.domainFile = domainFile;
	}

}
