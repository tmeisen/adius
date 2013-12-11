package org.adiusframework.processmanager;

/**
 * The processing of a query has different states that have to be executed
 * before the query processing is finally finished. This ENUM refers the
 * different status that reference a state. Thereby a query can only reach the
 * state failed or finished, when it has either reached the status queued or
 * executed before.
 * 
 * @author tm807416
 * 
 */
public enum QueryStatus {
	QUEUED, QUEUETIMEDOUT, QUEUEFAILED, EXECUTION, FINISHED;
}
