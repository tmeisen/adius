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

import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class TimeStampedPlan implements Plan {
	public SortedSet<TimeStampedAction> actions = new TreeSet<TimeStampedAction>();

	public void addAction(Action a, BigDecimal t) {
		addAction(a, t, null);
	}

	public void addAction(Action a, BigDecimal t, BigDecimal d) {
		actions.add(new TimeStampedAction(a, t, d));
	}

	@Override
	public void print(PrintStream p) {
		Iterator<TimeStampedAction> ait = actions.iterator();
		while (ait.hasNext()) {
			TimeStampedAction a = ait.next();
			p.println(a);
		}
	}

	@Override
	public void print(PrintWriter p) {
		Iterator<TimeStampedAction> ait = actions.iterator();
		while (ait.hasNext()) {
			TimeStampedAction a = ait.next();
			p.println(a);
		}
	}

	@Override
	public List<Action> getActions() {
		ArrayList<Action> s = new ArrayList<Action>();
		Iterator<TimeStampedAction> ait = actions.iterator();
		while (ait.hasNext()) {
			TimeStampedAction a = ait.next();
			s.add(a.action);
		}
		return s;
	}
}
