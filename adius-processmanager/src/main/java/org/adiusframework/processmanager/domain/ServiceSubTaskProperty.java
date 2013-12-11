package org.adiusframework.processmanager.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Entity;

/**
 * The ServiceSubTaskProperty class provides the possibility to store Properties
 * which are connected to a ServiceSubTask in a database.
 */
@javax.persistence.Entity
@Entity
@Table
public class ServiceSubTaskProperty {

	/** The name of the table in which the properties are stored. */
	public static final String TABLE = "servicesubtaskproperty";

	/** The name of the column which stores an identifier for this property. */
	public static final String PROPERTY_ID = "Property_ID";

	/** The name of the column which stores the name of the property. */
	public static final String NAME = "Name";

	/** The name of the column which stores the value of the property. */
	public static final String VALUE = "Value";

	/** The identifier for the property. */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = PROPERTY_ID)
	private Integer propertyId;

	/** The ServiceSubTask to which this property belongs. */
	@ManyToOne
	@JoinColumn(name = ServiceSubTask.SUBTASK_ID)
	private ServiceSubTask subTask;

	/** The name of the property. */
	@Column(name = NAME)
	private String name;

	/** The value of the property. */
	@Column(name = VALUE)
	private String value;

	/**
	 * Creates a new ServiceSubTaskProperty with no special configuration.
	 */
	public ServiceSubTaskProperty() {
	}

	/**
	 * Creates a new ServiceSubTaskProperty with the given owner, name and
	 * value.
	 * 
	 * @param subTask
	 *            The ServcieSubTask to which this property belongs.
	 * @param name
	 *            The name of the property.
	 * @param value
	 *            The value of the property.
	 */
	public ServiceSubTaskProperty(ServiceSubTask subTask, String name, String value) {
		setSubTask(subTask);
		setName(name);
		setValue(value);
	}

	/**
	 * Returns the identifier of this property.
	 * 
	 * @return The property ID.
	 */
	public Integer getPropertyId() {
		return propertyId;
	}

	/**
	 * Sets a new identifier for this property.
	 * 
	 * @param propertyId
	 *            The new ID.
	 */
	protected void setPropertyId(Integer propertyId) {
		this.propertyId = propertyId;
	}

	/**
	 * Returns the ServiceSubTask to which this property belongs.
	 * 
	 * @return The ServiceSubTask.
	 */
	public ServiceSubTask getSubTask() {
		return subTask;
	}

	/**
	 * Sets a new ServiceSubTask.
	 * 
	 * @param subTask
	 *            The new ServiceSubTask.
	 */
	public void setSubTask(ServiceSubTask subTask) {
		this.subTask = subTask;
	}

	/**
	 * Returns the name of the property.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets a new name.
	 * 
	 * @param name
	 *            The new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the value of the property.
	 * 
	 * @return the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets a new value.
	 * 
	 * @param value
	 *            The new value.
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
