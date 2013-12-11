package org.adiusframework.util.db;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import org.adiusframework.util.reflection.AnnotationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Enables bulk loading for JPA annotated classes. The bulk loader scans the
 * given class for information about the object-relational mapping and uses this
 * information to create a bulk loader. Using the provided writeEntity method,
 * an entity of the type of the given class can be written to the bulk loader.
 * At the moment the JPA annotations have to be part of the fields of the class.
 * Method annotations are not supported.
 * 
 * @author tmeisen
 * @version 0.1.0
 * 
 */
public class EntityBulkLoader extends AbstractBulkLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBulkLoader.class);

	private Class<?> entityClass;

	private List<FieldData> fieldDataList;

	/**
	 * Constructs a new entity bulk loader.
	 * 
	 * @param entityClass
	 *            The class defining the entity whose data have to be written by
	 *            the bulk loader.
	 * @param open
	 *            If set to true the bulk loader is opened after construction.
	 */
	public EntityBulkLoader(Class<?> entityClass, boolean open) {
		this(entityClass);
		if (open)
			this.open();
	}

	/**
	 * Constructs a new entity bulk loader.
	 * 
	 * @param entityClass
	 *            The class defining the entity whose data have to be written by
	 *            the bulk loader.
	 * @param open
	 *            If set to true the bulk loader is opened after construction.
	 * @param rowControl
	 *            If set the row control feature is enabled.
	 */
	public EntityBulkLoader(Class<?> entityClass, boolean open, boolean rowControl) {
		this(entityClass);
		if (open)
			this.open();
		if (rowControl)
			this.setRowCountControlOn(0);
	}

	/**
	 * Constructs a new entity bulk loader.
	 * 
	 * @param entityClass
	 *            The class defining the entity whose data have to be written by
	 *            the bulk loader.
	 */
	public EntityBulkLoader(Class<?> entityClass) {
		this.fieldDataList = new Vector<FieldData>();
		this.setEntityClass(entityClass);
	}

	/**
	 * Writs all objects stored in the collection into the bulk by calling the
	 * method write for each object.
	 * 
	 * @param dataObjects
	 */
	public void writeAll(Collection<?> dataObjects) {
		LOGGER.debug("Writing " + dataObjects);
		for (Object dataObject : dataObjects) {
			write(dataObject);
		}
	}

	/**
	 * Writes an object to the bulk loader, the object has to be an instance of
	 * the entity class the bulk loader has been constructed for. Otherwise an
	 * exception is thrown.
	 * 
	 * @param dataObject
	 *            The object whose data have to be written into the bulk loader.
	 */
	public void write(Object dataObject) {

		// check requirements
		if (dataObject == null || !this.isOpened()) {
			LOGGER.info("Bulk loader is closed, writing not possible.");
			return;
		}
		if (!this.entityClass.isInstance(dataObject)) {
			LOGGER.info("Bulk loader does not support objects of type " + dataObject.getClass().getName());
			return;
		}

		// get data of given object and write to bulk loader
		List<Object> dataList = new Vector<Object>();
		for (FieldData fieldData : this.fieldDataList) {
			Object data = AnnotationUtil.readMemberData(fieldData.getGetter(), dataObject);
			if (fieldData.isJoinedColumn()) {

				// if join column data is given the data only represents the
				// entity (hence the unique id of the entity has to be
				// determined)
				data = AnnotationUtil.readMemberData(fieldData.getJoinedColumnGetter(), data);
			}
			dataList.add(data);
		}
		super.writeData(dataList.toArray());
	}

	/**
	 * Sets the entity of the entity bulk loader, the entity must implement the
	 * entity and table annotations of the JPA.
	 * 
	 * @param entityClass
	 *            A class that defines the entity used for the bulk loading.
	 */
	private void setEntityClass(Class<?> entityClass) {

		// validate the given entityClass
		// check if entity is annotated
		if (entityClass.getAnnotation(Entity.class) == null)
			throw new EntityNotFoundException("The entity does not contain the javax.persistence.entity annotation.");

		// check if table is annotated
		Table tableAnnotation = (Table) AnnotationUtil.getAnnotationInHierarchy(entityClass, Table.class);
		if (tableAnnotation == null)
			throw new EntityNotFoundException("The entity does not contain the javax.persistence.table annotation.");
		this.entityClass = entityClass;

		// determine column and joinColumn annotations in fields and determine
		// accessible options
		List<Field> fields;
		fields = AnnotationUtil.findFieldsWithAnnotation(entityClass, Column.class, Id.class, true);
		determineGetterOfColumnFields(fields, false);
		fields = AnnotationUtil.findFieldsWithAnnotation(entityClass, JoinColumn.class, true);
		determineGetterOfColumnFields(fields, true);

		// setup bulk loader superclass members
		String columns = "";
		for (int i = 0; i < this.fieldDataList.size(); i++) {
			columns += this.fieldDataList.get(i).getColumn()
					+ (i < this.fieldDataList.size() - 1 ? AbstractBulkLoader.COLUMN_SEPARATOR : "");
		}
		this.setTable(tableAnnotation.name());
		this.setColumns(columns);
		LOGGER.debug("Bulk loader initialized with " + this.getTable() + " " + this.getColumns());
	}

	/**
	 * Determines the getter of a given field list and adds the information to
	 * the field data list. The method does not checks whether the information
	 * has already been added to the list.
	 * 
	 * @param fields
	 *            The list of fields that have to be checked.
	 * @param isJoinedColumn
	 *            Flag whether the given fields are annotated with joinColumn or
	 *            only Column.
	 */
	private void determineGetterOfColumnFields(List<Field> fields, boolean isJoinedColumn) {
		for (Field field : fields) {
			Member getter = AnnotationUtil.findAccessibleOfMember(this.entityClass, field);
			if (isJoinedColumn) {
				Member idGetter = this.determineIdGetterOfJoinedColumnField(field);
				String column = JoinColumn.class.cast(field.getAnnotation(JoinColumn.class)).name();
				this.fieldDataList.add(new FieldData(column, field, getter, idGetter));
			} else {
				String column = Column.class.cast(field.getAnnotation(Column.class)).name();
				this.fieldDataList.add(new FieldData(column, field, getter));
			}
		}
	}

	/**
	 * A joined column references an object of a different type. Such an object
	 * is identified by a unique id which is flagged with the id annotation. The
	 * method scans for the id annotation in the type class of the given field
	 * and finds the getter member to read this data.
	 * 
	 * @param field
	 *            The field that has to be scanned.
	 * @return Member that represents the getter.
	 */
	private Member determineIdGetterOfJoinedColumnField(Field field) {
		Class<?> classType = field.getType();
		List<Field> idFields = AnnotationUtil.findFieldsWithAnnotation(classType, Id.class, true);
		if (idFields.size() > 1)
			throw new EntityExistsException("The field " + field.getName() + " has a multi column primary key");
		else if (idFields.size() == 0)
			throw new EntityNotFoundException("No id column found in " + field.getName());
		Member getter = AnnotationUtil.findAccessibleOfMember(classType, idFields.get(0));
		return getter;
	}

	/**
	 * Private class to store data needed by the bulk loader. The field data
	 * class stores the getter and the type of the field.
	 * 
	 * @author tmeisen
	 * 
	 */
	private class FieldData {
		Field field;
		Member getter;
		Member joinedColumnGetter;
		String column;

		public FieldData(String column, Field field, Member getter) {
			this.column = column;
			this.field = field;
			this.getter = getter;
			this.joinedColumnGetter = null;
		}

		public FieldData(String column, Field field, Member getter, Member joinedColumnGetter) {
			this(column, field, getter);
			this.joinedColumnGetter = joinedColumnGetter;
		}

		public Field getField() {
			return this.field;
		}

		public Member getGetter() {
			return this.getter;
		}

		public String getColumn() {
			return this.column;
		}

		public boolean isMethod() {
			return Method.class.isInstance(this.getter);
		}

		public boolean isField() {
			return Field.class.isInstance(this.getter);
		}

		public boolean isJoinedColumn() {
			return this.joinedColumnGetter != null;
		}

		public Member getJoinedColumnGetter() {
			return this.joinedColumnGetter;
		}

		@Override
		public String toString() {
			return this.getColumn() + " - " + this.getField().getName() + " - " + this.getGetter().getName() + " ["
					+ this.isMethod() + "," + this.isField() + "," + this.isJoinedColumn()
					+ (this.isJoinedColumn() ? " " + this.joinedColumnGetter.toString() : "") + "]";
		}
	}
}
