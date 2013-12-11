package org.adiusframework.processmanager;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.exception.InvalidDomainException;
import org.adiusframework.processmanager.exception.ProcessManagerException;
import org.adiusframework.processmanager.exception.ServiceProcessFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AbstractQueueQueryExecutionControl stores all SimulationProcesses in a
 * queue and re-executes them, if the previous has no result, until a maximum
 * execution-count is reached.
 */
public abstract class AbstractQueueQueryExecutionControl extends AbstractQueryExecutionControl {

	static final Logger LOGGER = LoggerFactory.getLogger(AbstractQueueQueryExecutionControl.class);

	private static final int DEFAULT_RETRY_COUNT = 5;

	private static final long DEFAULT_DELAY_PERIOD = 30;

	/**
	 * The queue where the ServiceProcesses are stored.
	 */
	private ServiceProcessQueue queue;

	private int retryCount;

	/**
	 * Create a new AbstractQueueQueryExecutionControl with default values.
	 */
	public AbstractQueueQueryExecutionControl() {
		setQueue(new ServiceProcessQueue(DEFAULT_DELAY_PERIOD));
		setRetryCount(DEFAULT_RETRY_COUNT);
	}

	/**
	 * Returns the delay-period which indicates how much time will be spend
	 * before the ServiceProcess is re-executed.
	 * 
	 * @return The delay-period.
	 */
	public long getDelayPeriod() {
		return getQueue().getDelayPeriod();
	}

	/**
	 * Sets a new delay-period.
	 * 
	 * @param delayPeriod
	 *            The new delay-period.
	 */
	public void setDelayPeriod(long delayPeriod) {
		getQueue().setDelayPeriod(delayPeriod);
	}

	/**
	 * Return the retry-count which indicates how often a ServiceProcess is
	 * re-executed at maximum.
	 * 
	 * @return The current retry-count.
	 */
	public int getRetryCount() {
		return retryCount;
	}

	/**
	 * Sets a new retry-count.
	 * 
	 * @param retryCount
	 *            The new retry-count.
	 */
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	/**
	 * Returns the ServiceProcessQueue which is currently used by the
	 * AbstractQueueQueryExecutionControl.
	 * 
	 * @return The current ServiceProcessQueue.
	 */
	protected ServiceProcessQueue getQueue() {
		return queue;
	}

	/**
	 * Sets a new ServiceProcessQueue.
	 * 
	 * @param queue
	 *            The new ServiceProcessQueue.
	 */
	protected void setQueue(ServiceProcessQueue queue) {
		this.queue = queue;
	}

	/**
	 * Before the AbstractQueryExecutionControl can execute the ServiceProcess
	 * it is checked if the execution of the ServiceProcess already failed.
	 */
	@Override
	protected ServiceProcess executeServiceProcess(ServiceProcess serviceProcess) throws ProcessManagerException {

		// instead of executing the process, we have to make sure that the
		// status requirements, in case of an extraction, are satisfied
		LOGGER.debug("Identifying type of service process...");
		ServiceProcessDefinition spd = getServiceProcessExecutionControl().findServiceDefinition(
				serviceProcess.getType(), serviceProcess.getDomain());
		LOGGER.debug("Type identified as: " + spd);
		if (spd.isDataAccessor()) {

			// hence the first step is the refresh of the service process object
			getRepository().refreshServiceProcess(serviceProcess);

			// now we can check the new status and proceed
			if (isUnprocessed(serviceProcess)) {
				queueServiceProcess(serviceProcess);
				return serviceProcess;
			} else if (isError(serviceProcess)) {

				// since the last check the simulation step has changed to error
				// state
				LOGGER.error("Execution of service process " + serviceProcess
						+ " stopped, because step is in error state.");
				throw new ServiceProcessFailedException("Simulation step is in error state", serviceProcess);
			}
		}

		// if we reach this point either an extraction query referencing a
		// entity that can be processed or another type of query is given. at
		// this point we can go on with the execution that means no more
		// queuing.
		LOGGER.debug("Executing service process...");
		getQueue().remove(serviceProcess);
		return super.executeServiceProcess(serviceProcess);
	}

	/**
	 * Method that have to check if the domain specific entity and the
	 * corresponding service process can be processed.
	 * 
	 * @param serviceProcess
	 *            service process containing all relevant information about the
	 *            entity
	 * @return true if the domain specific entity can be processed further.
	 * @throws InvalidDomainException
	 *             if domain cannot be identified
	 */
	protected abstract boolean isUnprocessed(ServiceProcess serviceProcess) throws InvalidDomainException;

	/**
	 * Method that have to check if the domain specific entity represents an
	 * error and a further processing is not possible.
	 * 
	 * @param serviceProcess
	 *            service process containing all relevant information about the
	 *            entity
	 * @return true if a further processing is not possible.
	 * @throws InvalidDomainException
	 *             if domain cannot be identified
	 */
	protected abstract boolean isError(ServiceProcess serviceProcess) throws InvalidDomainException;

	/**
	 * Redirects to
	 * {@link AbstractQueueQueryExecutionControl#executeServiceProcess(ServiceProcess)}
	 * and provides error-handling. If an internal exception occurs a
	 * QueryStatusEvent is created and published to all registered
	 * QueryStatusEventListeners.
	 * 
	 * @param serviceProcess
	 *            The ServiceProcess which should be executed.
	 */
	protected void executedQueuedServiceProcess(ServiceProcess serviceProcess) {
		try {
			executeServiceProcess(serviceProcess);
		} catch (ProcessManagerException e) {

			// the execution ended in an exception, hence a status event is
			// triggered and the queue is cleaned by the failed process
			getQueue().remove(serviceProcess);
			StandardQueryStatusEvent event = new StandardQueryStatusEvent(serviceProcess.getInternalId(),
					serviceProcess.getExternalId(), QueryStatus.QUEUEFAILED);
			event.setAttachment(e.getMessage());
			fireEvent(event);
		}
	}

	/**
	 * Adds a given ServiceProcess to the Queue if the retry-count isn't yet
	 * reached. Otherwise a representative QueryStatusEvent is published.
	 * 
	 * @param serviceProcess
	 *            The given ServiceProcess.
	 */
	protected void queueServiceProcess(ServiceProcess serviceProcess) {
		int count = getQueue().getCounter(serviceProcess);
		if (count <= getRetryCount()) {
			LOGGER.debug("Queuing service process " + serviceProcess.getInternalId() + " (" + count + "/"
					+ getRetryCount() + ")");
			getQueue().add(serviceProcess);
		} else {

			// a timeout occurred, hence the process is removed from the queue
			// and an event is triggered
			getQueue().remove(serviceProcess);
			fireEvent(new StandardQueryStatusEvent(serviceProcess.getInternalId(), serviceProcess.getExternalId(),
					QueryStatus.QUEUETIMEDOUT));
		}
	}

	/**
	 * The ServiceProcessQueue represents a delayed queue for ServiceProcesses.
	 */
	protected class ServiceProcessQueue implements Runnable {

		/**
		 * The DelayQueue object to store the ServiceProcessed.
		 */
		private DelayQueue<DelayedServiceProcess> queue;

		/**
		 * A ConcurrentMap to store the retry-count for every ServiceProcess
		 * based on it's internal identifier.
		 */
		private ConcurrentMap<String, Integer> counter;

		private long delayPeriod;

		/**
		 * Creates a new ServiceProcessQueue with the given parameter and starts
		 * a Thread witch processes the elements in the queue.
		 * 
		 * @param delayPeriod
		 *            The delay-period which should be used.
		 */
		public ServiceProcessQueue(long delayPeriod) {
			queue = new DelayQueue<DelayedServiceProcess>();
			setCounterMap(new ConcurrentHashMap<String, Integer>());
			setDelayPeriod(delayPeriod);

			// lets start the concurrent execution of the process input queue
			Thread thread = new Thread(this, "QueueProcessor");
			thread.setDaemon(true);
			thread.start();
		}

		/**
		 * Returns the delay-period which indicates how much time will be spend
		 * before the ServiceProcess is re-executed.
		 * 
		 * @return The delay-period.
		 */
		public long getDelayPeriod() {
			return delayPeriod;
		}

		/**
		 * Sets a new dely-period.
		 * 
		 * @param delayPeriod
		 *            The new dely-period.
		 */
		public void setDelayPeriod(long delayPeriod) {
			this.delayPeriod = delayPeriod;
		}

		/**
		 * Return the ConcurrentMap which is currently used by the
		 * ServiceProcessQueue.
		 * 
		 * @param counter
		 *            The current Map.
		 */
		public void setCounterMap(ConcurrentMap<String, Integer> counter) {
			this.counter = counter;
		}

		/**
		 * Sets a new ConcurrentMap.
		 * 
		 * @return The new ConcurrentMap.
		 */
		public ConcurrentMap<String, Integer> getCounterMap() {
			return this.counter;
		}

		/**
		 * Returns the number of execution for a given ServiceProcess.
		 * 
		 * @param serviceProcess
		 *            The given ServiceProcess.
		 * @return The number of execution, 0 if the ServiceProcess is
		 *         registered.
		 */
		public int getCounter(ServiceProcess serviceProcess) {
			if (getCounterMap().containsKey(serviceProcess.getInternalId()))
				return getCounterMap().get(serviceProcess.getInternalId());
			return 0;
		}

		/**
		 * Adds a ServiceProcess to the queue that it will be executed in
		 * future. And increases it's counter by one, respectively adds a new
		 * counter with the value one.
		 * 
		 * @param serviceProcess
		 *            The ServiceProcess which should be executed.
		 */
		public void add(ServiceProcess serviceProcess) {
			int count = 0;
			LOGGER.debug("Checking if " + serviceProcess.getInternalId() + " exists...");
			if (getCounterMap().containsKey(serviceProcess.getInternalId()))
				count = getCounterMap().get(serviceProcess.getInternalId()) + 1;
			else
				count = 1;
			getCounterMap().put(serviceProcess.getInternalId(), count);
			LOGGER.debug("Counter set to " + getCounterMap().get(serviceProcess.getInternalId()));
			queue.add(new DelayedServiceProcess(serviceProcess, getDelayPeriod()));
			LOGGER.debug(serviceProcess + " added with delay period " + getDelayPeriod());
		}

		/**
		 * Removes a ServiceProcess from the counter-map. This method should be
		 * called if the ServiceProcess is finished successfully or with an
		 * error.
		 * 
		 * @param serviceProcess
		 *            The ServiceProcess which should be removed.
		 */
		public void remove(ServiceProcess serviceProcess) {
			getCounterMap().remove(serviceProcess.getInternalId());
		}

		/**
		 * Takes, in an endless cycle, the first object from the queue and
		 * processes it.
		 */
		@Override
		public void run() {
			while (true) {
				try {
					LOGGER.debug("Waiting for next queue entry to be handled");
					DelayedServiceProcess delayedServiceProcess = queue.take();
					if (delayedServiceProcess != null)
						executedQueuedServiceProcess(delayedServiceProcess.getServiceProcess());
				} catch (InterruptedException e) {
					LOGGER.error("Interrupted during waiting for next input in queue: " + e.getMessage());
				}
			}
		}

		/**
		 * Class to wrap a ServiceProcess and add functionality required by the
		 * {@link Delayed} interface.
		 */
		protected class DelayedServiceProcess implements Delayed {

			private Date enqueueDate;

			private ServiceProcess serviceProcess;

			private long endOfDelay;

			/**
			 * Creates a new DelyedServiceProcess object with the given
			 * parameters.
			 * 
			 * @param serviceProcess
			 *            The ServiceProcess to wrap.
			 * @param delay_period
			 *            The relative delay-period for this ServiceProcess.
			 */
			public DelayedServiceProcess(ServiceProcess serviceProcess, long delay_period) {
				setServiceProcess(serviceProcess);
				setEnqueueDate(new Date());
				setEndOfDelay(System.currentTimeMillis() + delay_period * 1000L);
			}

			/**
			 * Returns the date when the ServiceProcess was enqueued.
			 * 
			 * @return The enqueue-date.
			 */
			public Date getEnqueueDate() {
				return enqueueDate;
			}

			/**
			 * Sets a new date when the ServiceProcess was enqueued.
			 * 
			 * @param enqueueDate
			 *            The new date.
			 */
			public void setEnqueueDate(Date enqueueDate) {
				this.enqueueDate = enqueueDate;
			}

			/**
			 * Return the absolute time which indicates when the wrapped
			 * ServiceProcess should be dequeued.
			 * 
			 * @return The time, according to {@link System#currentTimeMillis()}
			 *         .
			 */
			public long getEndOfDelay() {
				return endOfDelay;
			}

			/**
			 * Sets a new (absolute) time which indicates when the wrapped
			 * ServiceProcess should be dequeued.
			 * 
			 * @param endOfDelay
			 *            The new time.
			 */
			public void setEndOfDelay(long endOfDelay) {
				this.endOfDelay = endOfDelay;
			}

			/**
			 * Return the ServiceProcess which is wrapped by this
			 * DelyedServiceProcess object.
			 * 
			 * @return The wrapped ServiceProcess
			 */
			public ServiceProcess getServiceProcess() {
				return serviceProcess;
			}

			/**
			 * Sets a new ServiceProcess object which will be considered as the
			 * ServiceProcess witch is wrapped by this DelyedServiceProcess
			 * object.
			 * 
			 * @param serviceProcess
			 *            The new ServiceProcess.
			 */
			public void setServiceProcess(ServiceProcess serviceProcess) {
				this.serviceProcess = serviceProcess;
			}

			@Override
			public int compareTo(Delayed o) {

				// first lets validate the call
				if (!DelayedServiceProcess.class.isInstance(o))
					throw new UnsupportedOperationException(o.getClass() + " not supported");

				// now lets compare the end of delay for the different delayed
				// service processes
				DelayedServiceProcess request = DelayedServiceProcess.class.cast(o);
				if (getEndOfDelay() < request.getEndOfDelay())
					return -1;
				if (getEndOfDelay() > request.getEndOfDelay())
					return 1;
				return this.getEnqueueDate().compareTo(request.getEnqueueDate());
			}

			@Override
			public boolean equals(Object o) {
				if (!DelayedServiceProcess.class.isInstance(o))
					return false;
				DelayedServiceProcess request = DelayedServiceProcess.class.cast(o);
				if (getEndOfDelay() == request.getEndOfDelay() && getEnqueueDate().equals(request.getEnqueueDate())
						&& serviceProcess.equals(request.serviceProcess))
					return true;
				return false;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result + (int) (getEndOfDelay() ^ (getEndOfDelay() >>> 32));
				result = prime * result + ((getEnqueueDate() == null) ? 0 : getEnqueueDate().hashCode());
				result = prime * result + ((getServiceProcess() == null) ? 0 : getServiceProcess().hashCode());
				return result;
			}

			@Override
			public long getDelay(TimeUnit timeUnit) {
				return timeUnit.convert(getEndOfDelay() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
			}
		}

	}

}
