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

package javaff.search;

import javaff.planning.State;
import javaff.planning.Filter;

import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedList;
import java.util.Comparator;
import java.math.BigDecimal;

import java.util.Hashtable;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HillClimbingSearch extends Search {
	private static final Logger LOGGER = LoggerFactory.getLogger(HillClimbingSearch.class);

	protected Hashtable<Integer, State> closed;
	protected LinkedList<State> open;

	private int maxDepth;

	public HillClimbingSearch(State s, int maxDepth) {
		this(s, new HValueComparator(), maxDepth);
	}

	public HillClimbingSearch(State s, Comparator c, int maxDepth) {
		super(s);
		setComparator(c);

		closed = new Hashtable<Integer, State>();
		open = new LinkedList<State>();
		this.maxDepth = maxDepth;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int d) {
		this.maxDepth = d;
	}

	@Override
	public void setFilter(Filter f) {
		filter = f;
	}

	public State removeNext() {

		return (open).removeFirst();
	}

	public boolean needToVisit(State s) {
		Integer Shash = new Integer(s.hashCode()); // compute hash for tmstate
		State D = closed.get(Shash); // see if its on the closed list

		if (closed.containsKey(Shash) && D.equals(s))
			return false; // if it is return false

		closed.put(Shash, s); // otherwise put it on
		return true; // and return true
	}

	@Override
	public State search() {

		if (start.goalReached()) { // wishful thinking
			return start;
		}

		int depth = 0;
		needToVisit(start); // dummy call (adds start to the list of 'closed'
							// states so we don't visit it again

		open.add(start); // add it to the open list

		while (!open.isEmpty()) // whilst still states to consider
		{
			if (depth >= this.maxDepth)
				return null;

			BigDecimal bestHValue = new BigDecimal(Integer.MAX_VALUE); // and
																		// take
																		// its
																		// heuristic
																		// value
																		// as
																		// the
			// best so far

			// javaff.JavaFF.infoOutput.println(bestHValue);

			State s = removeNext(); // get the next one

			Set<State> successors = s.getNextStates(filter.getActions(s));
			ArrayList<State> bestSuccessors = new ArrayList<State>();

			Iterator<State> succItr = successors.iterator();

			while (succItr.hasNext()) {
				State succ = succItr.next(); // next successor

				if (needToVisit(succ)) {
					int res = succ.getHValue().compareTo(bestHValue);
					if (succ.goalReached()) { // if we've found a goal tmstate -
												// return it as the
												// solution
						return succ;
					} else if (res < 0) {
						// if we've found a tmstate with a better heuristic
						// value
						// than the best seen so far

						bestHValue = succ.getHValue(); // note the new best
														// avlue
						LOGGER.info(bestHValue.toString());
						bestSuccessors.clear(); // clear the open list
						bestSuccessors.add(succ); // put this on it
					} else if (res == 0) {
						bestSuccessors.add(succ); // otherwise, add to the open
													// list
					}
				}
			}
			if (bestSuccessors.isEmpty())
				return null;
			else {
				open.clear();
				open.add(bestSuccessors.get(javaff.JavaFF.generator.nextInt(bestSuccessors.size())));
			}

			depth++;
		}
		return null;
	}
}
