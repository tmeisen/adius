package org.adiusframework.util;

/**
 * The TimerUtil class provides several methods to wait for an exact number of
 * seconds or milliseconds.
 */
public class TimerUtil {
	/**
	 * The factor to calculate the number of milliseconds out of a number of
	 * seconds.
	 */
	private static final int S_TO_MS = 1000;

	/**
	 * The default constructor is the only constructor and private, because this
	 * class should not be instantiated. It only contains static methods.
	 */
	private TimerUtil() {
	}

	/**
	 * Waits for a specific number of milliseconds.
	 * 
	 * @param n
	 *            The number of milliseconds.
	 */
	public static void waitMilliseconds(int n) {
		long t0, t1;
		t0 = System.currentTimeMillis();
		do {
			t1 = System.currentTimeMillis();
		} while (t1 - t0 < n);
	}

	/**
	 * Waits for a specific number of seconds.
	 * 
	 * @param n
	 *            The number of seconds.
	 */
	public static void waitSeconds(int n) {
		waitMilliseconds(S_TO_MS * n);
	}
}
