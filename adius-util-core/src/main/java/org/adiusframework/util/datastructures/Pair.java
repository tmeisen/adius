package org.adiusframework.util.datastructures;

/**
 * Defines a Pair of two elements.
 * 
 * @param <T1>
 *            The class of the first element.
 * @param <T2>
 *            The class of the second element.
 */
public class Pair<T1, T2> {
	/**
	 * Stores the first element.
	 */
	T1 value1;

	/**
	 * Stores the second element.
	 */
	T2 value2;

	/**
	 * Creates a new Pair with with the given two elements.
	 * 
	 * @param value1
	 *            The first element.
	 * @param value2
	 *            The second element.
	 */
	public Pair(T1 value1, T2 value2) {
		this.value1 = value1;
		this.value2 = value2;
	}

	/**
	 * Return the first element.
	 * 
	 * @return The first element.
	 */
	public T1 getValue1() {
		return this.value1;
	}

	/**
	 * Returns the second element.
	 * 
	 * @return The second element.
	 */
	public T2 getValue2() {
		return this.value2;
	}

	@Override
	public boolean equals(Object object) {

		// check type of object
		if (!(object instanceof Pair<?, ?>))
			return false;

		// cast object
		Pair<?, ?> pair = (Pair<?, ?>) object;
		if (pair.getValue1().equals(this.value1) && pair.getValue2().equals(this.value2))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "(" + this.getValue1().toString() + "," + this.getValue2().toString() + ")";
	}

	@Override
	public int hashCode() {
		return this.getValue1().hashCode() ^ this.getValue2().hashCode();
	}
}
