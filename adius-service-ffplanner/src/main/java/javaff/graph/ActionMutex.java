package javaff.graph;

import java.util.ArrayList;
import java.util.Collection;

import javaff.data.Action;

/**
 * Represents a mutex between a single action and 1-n others.
 * 
 * @author David Pattison
 * 
 */
public class ActionMutex implements Mutex<Action> {
	private Action owner;
	private Collection<Action> others;

	public ActionMutex(Action owner) {
		this.owner = owner;
		this.others = new ArrayList<Action>();
	}

	public ActionMutex(Action owner, Collection<Action> others) {
		this.owner = owner;
		this.others = others;
	}

	public ActionMutex(Action owner, Action other) {
		this.owner = owner;
		this.others = new ArrayList<Action>();
		this.others.add(other);
	}

	@Override
	public void addMutex(Action other) {
		if (this.others.contains(other))
			this.others.add(other);
	}

	@Override
	public boolean removeMutex(Action other) {
		return this.others.remove(other);
	}

	@Override
	public boolean hasMutexes() {
		return this.others.size() == 0;
	}

	@Override
	public boolean isMutexWith(Action a) {
		return this.others.contains(a);
	}

	@Override
	public Action getOwner() {
		return owner;
	}

	@Override
	public void setOwner(Action owner) {
		this.owner = owner;
	}

	@Override
	public Collection<Action> getOthers() {
		return others;
	}

	@Override
	public void setOthers(Collection<Action> others) {
		this.others = others;
	}

	@Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(this.owner + "\n");
		for (Action o : this.others)
			strBuf.append("\t" + o + "\n");

		return strBuf.toString();
	}
}
