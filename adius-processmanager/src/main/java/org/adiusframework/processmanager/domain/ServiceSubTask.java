package org.adiusframework.processmanager.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.adiusframework.service.xml.Category;
import org.hibernate.annotations.Entity;

/**
 * The ServiceSubTask class saves and allows to store information about a
 * ServiceSubTask in a database.
 */
@javax.persistence.Entity
@Entity
@Table
public class ServiceSubTask implements Serializable {

	/**
	 * UID for serialization
	 */
	private static final long serialVersionUID = 1784270548941276578L;

	/** Store the name of the table in which the ServiceSubTasks are stored. */
	public static final String TABLE = "servicesubtask";

	/**
	 * The name of the column which stores the identifier of this
	 * ServiceSubTask.
	 */
	public static final String SUBTASK_ID = "SubTask_ID";

	/**
	 * The name of the column which stores the identifier of the ServiceTask to
	 * which this ServiceSubTask belongs.
	 */
	public static final String TASK_ID = "Task_ID";

	/**
	 * The name of the column which stores the position-number of this
	 * ServiceSubTask.
	 */
	public static final String POSITION = "Position";

	/** The name of the column which stores the category of this ServiceSubTask. */
	public static final String CATEGORY = "Category";

	/**
	 * The name of the column which stores the sub-category of this
	 * ServiceSubTask.
	 */
	public static final String SUBCATEGORY = "SubCategory";

	/** The name of the column which stores the status of this ServiceSubTask. */
	public static final String STATUS = "Status";

	/** The identifier of this ServiceSubTask. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = SUBTASK_ID)
	private Integer subTaskId;

	/** The ServiceTask to which this ServiceSubTask belongs. */
	@ManyToOne
	@JoinColumn(name = ServiceTask.TASK_ID)
	private ServiceTask task;

	/** The position of this ServiceSubTask. */
	@Column(name = POSITION)
	private Integer position;

	/** The category of this ServiceSubTask. */
	@Column(name = CATEGORY)
	@Enumerated(EnumType.STRING)
	private Category category;

	/** The sub-category of this ServiceSubTask. */
	@Column(name = SUBCATEGORY)
	private String subCategory;

	/** The status of this ServiceSubTask. */
	@Column(name = STATUS)
	@Enumerated(EnumType.STRING)
	private ServiceSubTaskStatus status;

	/** A Set of properties which belong to this ServiceSubTask. */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = SUBTASK_ID)
	private Set<ServiceSubTaskProperty> properties;

	/**
	 * Creates a new ServiceSubTask with no special configuration.
	 */
	public ServiceSubTask() {
	}

	/**
	 * Creates a new ServiceSubTask with the given parameters.
	 * 
	 * @param task
	 *            The ServiceTask to which the ServiceSubTask should belong.
	 * @param category
	 *            The category of the ServiceSubTask.
	 * @param index
	 *            The position of teh ServiceSubTask.
	 */
	public ServiceSubTask(ServiceTask task, Category category, int index) {
		setTask(task);
		setCategory(category);
		setPosition(index);
		setStatus(ServiceSubTaskStatus.WAITING);
		setProperties(new HashSet<ServiceSubTaskProperty>());
	}

	/**
	 * Returns the position of the ServiceSubTask.
	 * 
	 * @return The position.
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * Sets a new position.
	 * 
	 * @param position
	 *            The new position.
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}

	/**
	 * Returns the identifier of the ServiceSubTask.
	 * 
	 * @return The identifier.
	 */
	public Integer getSubTaskId() {
		return subTaskId;
	}

	/**
	 * Sets a new identifier.
	 * 
	 * @param subTaskId
	 *            The new identifier.
	 */
	public void setSubTaskId(Integer subTaskId) {
		this.subTaskId = subTaskId;
	}

	/**
	 * Returns the ServiceTask to which this ServiceSubTask belongs.
	 * 
	 * @return The ServiceTask.
	 */
	public ServiceTask getTask() {
		return task;
	}

	/**
	 * Sets a new ServiceTask.
	 * 
	 * @param task
	 *            The new ServiceTask.
	 */
	public void setTask(ServiceTask task) {
		this.task = task;
	}

	/**
	 * Return the ServiceSubTaskStatus of this ServiceSubTask.
	 * 
	 * @return The current status.
	 */
	public ServiceSubTaskStatus getStatus() {
		return status;
	}

	/**
	 * Sets a new ServiceSubTaskStatus.
	 * 
	 * @param status
	 *            The new status.
	 */
	public void setStatus(ServiceSubTaskStatus status) {
		this.status = status;
	}

	/**
	 * Returns the Category of this ServiceSubTask.
	 * 
	 * @return The Category.
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * Sets a new Category.
	 * 
	 * @param category
	 *            The new Category.
	 */
	public void setCategory(Category category) {
		this.category = category;
	}

	/**
	 * Return the sub-category of this ServiceSubTask.
	 * 
	 * @return The sub-category.
	 */
	public String getSubCategory() {
		return subCategory;
	}

	/**
	 * Sets a new sub-category.
	 * 
	 * @param subCategory
	 *            The new sub-category.
	 */
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	/**
	 * Return the properties which belong to this ServiceSubTask.
	 * 
	 * @return A Set with the properties.
	 */
	public Set<ServiceSubTaskProperty> getProperties() {
		return properties;
	}

	/**
	 * Sets a new Set of properties.
	 * 
	 * @param properties
	 *            The new Set.
	 */
	public void setProperties(Set<ServiceSubTaskProperty> properties) {
		this.properties = properties;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder().append("SubTask of ").append(getTask()).append(" of category ")
				.append(getCategory());
		if (getSubCategory() != null)
			builder.append("(").append(getSubCategory()).append(")");
		builder.append(", status ").append(getStatus()).append(", properties ").append(getProperties());
		return builder.toString();
	}

}
