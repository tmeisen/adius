package org.adiusframework.processmanager.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Entity;

/**
 * The ServiceProcess class saves and allows to store information about a
 * ServiceProcess in a database. A ServiceProcess contains several ServiceTasks
 * which are managed in a Map.
 */
@javax.persistence.Entity
@Entity
@Table(name = ServiceProcess.TABLE)
public class ServiceProcess implements Serializable {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = -8412674287228921335L;

	/** The name of the table in which the ServiceProcesses are stored. */
	public static final String TABLE = "serviceprocess";

	/**
	 * The name of the column which stores the identification number of the
	 * ServiceProcess.
	 */
	public static final String SERVICEPROCESS_ID = "ServiceProcess_ID";

	/** The name of the column "type" in the database. */
	public static final String TYPE = "Type";

	/**
	 * The name of the column which stores the internal identification number of
	 * the ServiceProcess.
	 */
	private static final String INTERNAL_ID = "Internal_ID";

	/**
	 * The name of the column which stores the external identification number of
	 * the ServiceProcess.
	 */
	private static final String EXTERNAL_ID = "External_ID";

	/**
	 * The name of the column which stores the parent id of the domain specific
	 * entity the service process is related to.
	 */
	private static final String ENTITY_ID = "Entity_ID";

	/**
	 * Column name of the domain
	 */
	private static final String DOMAIN = "Domain";

	/** The identification number of the ServiceProcess in the database. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = SERVICEPROCESS_ID)
	private Integer serviceProcessId;

	/** The internal identification number of the ServiceProcess. */
	@Column(name = INTERNAL_ID, unique = true)
	private String internalId;

	/** The external identification number of the ServiceProcess. */
	@Column(name = EXTERNAL_ID)
	private String externalId;

	/** The SimulationStep to which the ServiceProcess belongs. */
	@Column(name = ENTITY_ID)
	private Integer entityId;

	/** The type of the ServiceProcess. */
	@Column(name = TYPE)
	private String type;

	/** The type of the ServiceProcess. */
	@Column(name = DOMAIN)
	private String domain;

	/**
	 * The ServiceTasks which belong to this ServiceProcess mapped by their
	 * identification number.
	 */
	@OneToMany(mappedBy = "process", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@MapKey(name = "taskId")
	private Map<Integer, ServiceTask> tasks;

	/**
	 * Creates a new ServiceProcess with no ServiceTasks.
	 */
	public ServiceProcess() {
		tasks = new HashMap<Integer, ServiceTask>();
	}

	/**
	 * Sets a new identification number.
	 * 
	 * @param serviceProcessId
	 *            The new identification number.
	 */
	public void setProcessId(Integer serviceProcessId) {
		this.serviceProcessId = serviceProcessId;
	}

	/**
	 * Returns the identification number.
	 * 
	 * @return The identification number.
	 */
	public Integer getProcessId() {
		return this.serviceProcessId;
	}

	/**
	 * Return the domain specific entity id of this service process.
	 * 
	 * @return The id of the entity the process belongs to.
	 */
	public Integer getEntityId() {
		return entityId;
	}

	/**
	 * Sets the id of the domain specific entity id.
	 * 
	 * @param parentId
	 *            The specific entity id.
	 */
	public void setEntityId(int parentId) {
		this.entityId = parentId;
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
	 * Return the current type of the ServiceProcess.
	 * 
	 * @return The current type.
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Sets a new Map of ServiceTasks which belong to this ServiceProcess.
	 * 
	 * @param tasks
	 *            The new Map of ServiceTasks.
	 */
	public void setTasks(Map<Integer, ServiceTask> tasks) {
		this.tasks = tasks;
	}

	/**
	 * Returns the Map of ServiceTasks which currently belong to this
	 * ServiceProcess.
	 * 
	 * @return The current tasks.
	 */
	public Map<Integer, ServiceTask> getTasks() {
		return this.tasks;
	}

	/**
	 * Sets a new internal identification number.
	 * 
	 * @param internalId
	 *            The new internal ID.
	 */
	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	/**
	 * Returns the internal identification number.
	 * 
	 * @return The internal ID.
	 */
	public String getInternalId() {
		return this.internalId;
	}

	/**
	 * Sets a new external identification number.
	 * 
	 * @param externalId
	 *            The external ID.
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * Returns the external identification number.
	 * 
	 * @return The external ID.
	 */
	public String getExternalId() {
		return this.externalId;
	}

	/**
	 * Determines if one of the ServiceTasks is open.
	 * 
	 * @return True if at least one is open, false otherwise.
	 */
	public boolean hasOpenTask() {
		return getOpenTask() != null;
	}

	/**
	 * Returns the domain
	 * 
	 * @return The domain.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Sets the domain
	 * 
	 * @param domain
	 *            The new domain of the service process
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * Determines the ServiceTask which status is \"Open\".
	 * 
	 * @return The ServiceTask or null if no or more than one ServiceTask is
	 *         opened.
	 */
	public ServiceTask getOpenTask() {
		ServiceTask openTask = null;
		for (ServiceTask task : getTasks().values()) {
			if (task.getStatus().equals(ServiceTaskStatus.OPEN)) {
				if (openTask != null)
					return null;
				openTask = task;
			}
		}
		return openTask;
	}

	@Override
	public String toString() {
		return this.getProcessId() + " of entity " + this.getEntityId() + " with "
				+ (this.tasks != null ? this.tasks.size() : "0") + " tasks";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.internalId == null) ? 0 : this.internalId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServiceProcess other = (ServiceProcess) obj;
		if (this.internalId == null) {
			if (other.internalId != null)
				return false;
		} else if (!this.internalId.equals(other.internalId))
			return false;
		return true;
	}

}
