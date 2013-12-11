package org.adiusframework.util;

/**
 * The ArrayUtil class provides several static methods to deal with arrays.
 */
public class ArrayUtil {

	/**
	 * The default constructor is the only constructor and private, because this
	 * class should not be instantiated. It only contains static methods.
	 */
	private ArrayUtil() {
	}

	/**
	 * Swap two elements in an given array.
	 * 
	 * @param array
	 *            The given array.
	 * @param i1
	 *            The index of the first element to be swapped.
	 * @param i2
	 *            The index of the second element to be swapped.
	 * @return The arrays itself, now with swapped elements.
	 */
	public static <T> T[] swap(T[] array, int i1, int i2) {
		T tmp = array[i1];
		array[i1] = array[i2];
		array[i2] = tmp;
		return array;
	}

	/**
	 * Determines if a given array contains an element which is equal to a given
	 * parameter ({@link Object#equals(Object)}).
	 * 
	 * @param array
	 *            The given array.
	 * @param name
	 *            The parameter which should be searched.
	 * @return True if the parameter is in the arrays, false otherwise.
	 */
	public static <T> boolean contains(T[] array, T name) {
		for (T element : array) {
			if (element.equals(name))
				return true;
		}
		return false;
	}
}
