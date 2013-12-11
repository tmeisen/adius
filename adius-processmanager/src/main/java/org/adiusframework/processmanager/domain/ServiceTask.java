package org.adiusframework.processmanager.domain;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Entity;

/**
 * The ServiceTask class saves and allows to store information about a
 * ServiceTask in a database. A ServiceTask contains several ServiceSubTasks
 * which are managed in a Map.
 */
@javax.persistence.Entity
@Entity
@Table
public class ServiceTask implements Serializable {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 1784270548941276578L;

	/** The name of the table in which the ServiceTasks are stored. */
	public static final String TABLE = "servicetask";

	/**
	 * The name of the column which stores an identification number of the
	 * service-task.
	 */
	public static final String TASK_ID = "Task_ID";

	/** The name of the column which stores the type of the service-task. */
	public static final String TYPE = "Type";

	/** The name of the column which stores the status of the service-task. */
	public static final String STATUS = "Status";

	/** The identification number of the service-task. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = TASK_ID)
	private Integer taskId;

	/** The ServiceProcess to which this ServiceTask belongs. */
	@ManyToOne
	@JoinColumn(name = ServiceProcess.SERVICEPROCESS_ID)
	private ServiceProcess process;

	/** The type of this ServiceTask. */
	@Column(name = TYPE)
	private String type;

	/** The status of this ServiceTask. */
	@Column(name = STATUS)
	@Enumerated(EnumType.STRING)
	private ServiceTaskStatus status;

	/**
	 * The ServiceSubTasks of this ServiceTask, mapped by their position. Only
	 * one should have the status "open"!
	 */
	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@MapKey(name = "position")
	private Map<Integer, ServiceSubTask> subTasks;

	/**
	 * Creates a new ServiceTask without any special configuration.
	 */
	public ServiceTask() {
	}

	/**
	 * Creates a new ServiceTask with a given type and a ServiceProcess to which
	 * this ServiceTask belongs to.
	 * 
	 * @param process
	 *            The ServiceProcess.
	 * @param type
	 *            The type.
	 */
	public ServiceTask(ServiceProcess process, String type) {
		setProcess(process);
		setType(type);
		setStatus(ServiceTaskStatus.OPEN);
		setSubTasks(new HashMap<Integer, ServiceSubTask>());
	}

	/**
	 * Sets a new identifier.
	 * 
	 * @param taskId
	 *            The new ID.
	 */
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	/**
	 * Return the current identifier.
	 * 
	 * @return The current ID.
	 */
	public Integer getTaskId() {
		return this.taskId;
	}

	/**
	 * Sets a new ServiceProcess.
	 * 
	 * @param process
	 *            The new ServiceProcess.
	 */
	public void setProcess(ServiceProcess process) {
		this.process = process;
	}

	/**
	 * Returns the ServiceProcess to which this ServiceTask belongs.
	 * 
	 * @return The ServiceProcess.
	 */
	public ServiceProcess getProcess() {
		return this.process;
	}

	/**
	 * Sets a new type.
	 * 
	 * @param type
	 *            The new type.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Return the current type.
	 * 
	 * @return The current type.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets a new ServiceTaskStatus.
	 * 
	 * @param status
	 *            The new status.
	 */
	public void setStatus(ServiceTaskStatus status) {
		this.status = status;
	}

	/**
	 * Returns the current ServiceTaskStatus.
	 * 
	 * @return The current status.
	 */
	public ServiceTaskStatus getStatus() {
		return this.status;
	}

	/**
	 * Returns all ServiceSubTasks which belong to this ServiceTask.
	 * 
	 * @return A Map with all ServiceSubTasks.
	 */
	public Map<Integer, ServiceSubTask> getSubTasks() {
		return subTasks;
	}

	/**
	 * Sets a new Map with ServiceSubTasks.
	 * 
	 * @param subTasks
	 *            The new Map.
	 */
	public void setSubTasks(Map<Integer, ServiceSubTask> subTasks) {
		this.subTasks = subTasks;
	}

	/**
	 * Determines if this ServiceTask has ServiceSubTasks.
	 * 
	 * @return True if it has, false otherwise.
	 */
	public boolean hasSubTasks() {
		return (getSubTasks() != null) && (getSubTasks().size() > 0);
	}

	/**
	 * Determines if this ServiceTask can be closed.
	 * 
	 * @return True if it can, false otherwise.
	 */
	public boolean isCloseable() {
		if (!hasSubTasks())
			return true;
		boolean closeable = true;
		for (ServiceSubTask subTask : getSubTasks().values()) {
			closeable &= subTask.getStatus().equals(ServiceSubTaskStatus.CLOSED);
		}
		return closeable;
	}

	@Override
	public String toString() {
		return new StringBuilder().append(getTaskId()).append(" ").append(getType()).append(" ")
				.append(getSubTasks() == null ? 0 : getSubTasks().size()).append(" subtasks").toString();
	}

	/**
	 * Determines the next ServiceSubTask which has the status "open" or the
	 * first ServiceSubTask which has the status "waiting".
	 * 
	 * @return The next ServiceSubTask or null if it has no sub-tasks or all are
	 *         closed.
	 */
	public ServiceSubTask getNextOpenSubTask() {
		ServiceSubTask openTask = null;
		for (ServiceSubTask subTask : getSubTasks().values()) {
			if (subTask.getStatus().equals(ServiceSubTaskStatus.WAITING)
					&& (openTask == null || subTask.getPosition() < openTask.getPosition()))
				openTask = subTask;
			else if (subTask.getStatus().equals(ServiceSubTaskStatus.OPEN))
				return subTask;
		}
		return openTask;
	}

	/**
	 * Closes the first ServiceSubTask which has the status "open".
	 * 
	 * @return True if one was closed, false otherwise.
	 */
	public boolean closeSubTask() {
		for (ServiceSubTask subTask : getSubTasks().values()) {
			if (subTask.getStatus().equals(ServiceSubTaskStatus.OPEN)) {
				subTask.setStatus(ServiceSubTaskStatus.CLOSED);
				return true;
			}
		}
		return false;
	}

}
