package org.adiusframework.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AbstractStoppableThread extends the basic Thread class with the
 * functionality of stopping the thread correct. It runs {@link #runInLoop()} in
 * an endless cycle until {@link #stopThread()} was called.
 */
public abstract class AbstractStoppableThread extends Thread implements StoppableThread {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStoppableThread.class);

	/**
	 * Indicates if the thread should be stopped the next time it returned from
	 * the method {@link #runInLoop()}.
	 */
	private volatile boolean stopThread = false;

	@Override
	public final void run() {
		startUp();

		while (!stopThread) {
			runInLoop();
		}
		LOGGER.debug("Thread has been stopped successfully");
		cleanUp();
	}

	@Override
	public void stopThread() {
		LOGGER.debug("Stop signal has been sent to thread");
		stopThread = true;
	}

	/**
	 * Can be overwritten by sub-classes if they want to implements special
	 * behavior on startup.
	 */
	protected void startUp() {
		// nothing to do but can be overwritten by super classes
	}

	/**
	 * This method is called in a endless cycle until {@link #stopThread()} is
	 * called.
	 */
	protected abstract void runInLoop();

	/**
	 * Can be overwritten by sub-classes if they want to implements special
	 * behavior when the Thread ends.
	 */
	protected void cleanUp() {
		// nothing to do but can be overwritten by super classes
	}

}
