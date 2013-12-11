package javaff.data;

import java.util.Set;

/**
 * This is a PDDL fact, which can be a conjunction, single literal, OR, Imply,
 * function etc.
 * 
 * @author David Pattison
 * 
 */
public interface Fact extends PDDLPrintable, Cloneable {
	public boolean isStatic(); // returns whether this condition is static

	public Object clone();

	/**
	 * Returns all literals held in this fact. It is up to the implementation to
	 * decide quite what this means...
	 * 
	 * @return
	 */
	public Set<? extends Fact> getFacts();

}
