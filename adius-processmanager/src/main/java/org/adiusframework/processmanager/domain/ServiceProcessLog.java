package org.adiusframework.processmanager.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Entity;

/**
 * The ServiceProcessLog stores information to save a log-entry, which is
 * related to a ServiceProcess, in a database.
 */
@javax.persistence.Entity
@Entity
@Table(name = ServiceProcessLog.TABLE)
public class ServiceProcessLog implements Serializable {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 7521574398674690166L;

	/** The name of the table in which the log-entries are stored. */
	public static final String TABLE = "serviceprocesslog";

	/** The name of the column which stores an identifier for this log-entry. */
	public static final String LOG_ID = "Log_ID";

	/** The name of the column which stores a general message. */
	public static final String MESSAGE = "Message";

	/**
	 * The name of the column which stores the date, when the event happened, in
	 * which's context this entry was created.
	 */
	public static final String DATE = "Date";

	/** An identifier for this log-entry. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = LOG_ID)
	private Integer id;

	/** The ServiceProcess which is related to this log-entry. */
	@ManyToOne
	@JoinColumn(name = ServiceProcess.SERVICEPROCESS_ID)
	private ServiceProcess process;

	/** The ServiceTask which is related to this log-entry. */
	@ManyToOne(optional = true)
	@JoinColumn(name = ServiceTask.TASK_ID)
	private ServiceTask task;

	/**
	 * The date, when the event happened, in which's context this entry was
	 * created.
	 */
	@Column(name = DATE)
	private String date;

	/** A general message for this entry. */
	@Column(name = MESSAGE)
	private String message;

	/**
	 * Default constructor
	 */
	public ServiceProcessLog() {
	}

	/**
	 * Return the identifier for this log-entry.
	 * 
	 * @return The ID.
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets a new ServiceProcess that is related to this entry. Also deletes the
	 * link to a related ServiceTask.
	 * 
	 * @param process
	 *            The new related ServiceProcess.
	 */
	public void setProcess(ServiceProcess process) {
		this.process = process;
		this.task = null;
	}

	/**
	 * Returns the ServiceProcess which is related to this entry, if there is
	 * one.
	 * 
	 * @return The related ServiceProcess, or null if there is no relates
	 *         ServiceProcess.
	 */
	public ServiceProcess getProcess() {
		return this.process;
	}

	/**
	 * Sets a new ServiceTask that is related to this entry. Also deletes the
	 * link to a related ServiceProcess.
	 * 
	 * @param task
	 *            The new related ServiceProcess.
	 */
	public void setTask(ServiceTask task) {
		this.setProcess(task.getProcess());
		this.task = task;
	}

	/**
	 * Returns the ServiceTask which is related to this entry, if there is one.
	 * 
	 * @return The related ServiceTask, or null if there is no relates
	 *         ServiceTask.
	 */
	public ServiceTask getTask() {
		return this.task;
	}

	/**
	 * Sets a new message.
	 * 
	 * @param message
	 *            The new message.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the message.
	 * 
	 * @return The message.
	 */
	public String getMessage() {
		return this.message;
	}

	/**
	 * Sets a new date when the described event happened.
	 * 
	 * @param date
	 *            The new date.
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * Returns the date when the described event happened.
	 * 
	 * @return The date.
	 */
	public String getTimestamp() {
		return this.date;
	}

	@Override
	public String toString() {
		return this.process.getProcessId() + (this.task == null ? "" : ":" + this.task.getTaskId()) + ":" + this.date
				+ ":" + this.message;
	}
}
