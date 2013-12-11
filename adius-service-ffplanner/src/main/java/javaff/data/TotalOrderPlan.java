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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class TotalOrderPlan implements Plan, Cloneable {
	protected ArrayList<Action> plan = new ArrayList<Action>();

	@Override
	@SuppressWarnings("unchecked")
	public Object clone() {
		TotalOrderPlan rTOP = new TotalOrderPlan();
		rTOP.plan = (ArrayList<Action>) this.plan.clone();
		return rTOP;
	}

	public void addAction(Action a) {
		plan.add(a);
	}

	public int getPlanLength() {
		return plan.size();
	}

	public Iterator<Action> iterator() {
		return plan.iterator();
	}

	public ListIterator<Action> listIteratorEnd() {
		return plan.listIterator(plan.size());
	}

	public ListIterator<Action> listIterator(Action a) {
		return plan.listIterator(plan.indexOf(a));
	}

	@Override
	public List<Action> getActions() {
		return plan;
	}

	public List<Action> getLinearActions() {
		return plan;
	}

	public void clear() {
		this.plan.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TotalOrderPlan) {
			TotalOrderPlan p = (TotalOrderPlan) obj;
			return (plan.equals(p.plan));
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return plan.hashCode();
	}

	@Override
	public void print(PrintStream ps) {
		Iterator<Action> pit = plan.iterator();
		while (pit.hasNext()) {
			ps.println("(" + pit.next() + ")");
		}
	}

	@Override
	public void print(PrintWriter pw) {
		Iterator<Action> pit = plan.iterator();
		while (pit.hasNext()) {
			pw.println("(" + pit.next() + ")");
		}
	}

	@Override
	public String toString() {
		return plan.toString();
	}
}
