/**
 * 
 * @author tmeisen
 * created March 03, 2010
 */
package org.adiusframework.util.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.adiusframework.util.IsConfigured;
import org.hibernate.criterion.Criterion;

/**
 * @author tmeisen
 */
public interface GenericDao<T, ID extends Serializable> extends IsConfigured {

	/**
	 * Finds an object given by the id, returns null when the object was not
	 * found
	 * 
	 * @param id
	 * @return Found object, if no object has been found null is return.
	 */
	public T findById(ID id);

	/**
	 * Saves an object
	 * 
	 * @param object
	 *            Object to save
	 */
	public void save(T object);

	/**
	 * Saves all objects in the collection.
	 * 
	 * @param objects
	 *            Objects to save
	 */
	public void saveAll(Collection<T> objects);

	/**
	 * Updates an object
	 * 
	 * @param object
	 *            Object to update
	 */
	public void update(T object);

	/**
	 * Saves or updates an object, depending if the primary key is set
	 * 
	 * @param object
	 *            Object to save or update
	 */
	public void saveOrUpdate(T object);

	/**
	 * Gets an object from the session, cache or db (in this order) and merges
	 * the passed objects values into the found object.
	 * 
	 * @param object
	 *            Object to merge
	 * @return the found object
	 */
	public T merge(T object);

	/**
	 * Deletes object in the database and sets primary key to null
	 * 
	 * @param object
	 *            Object to be deleted
	 */
	public void delete(T object);

	/**
	 * Returns a java.util.List of the objects fitting the specified criterias
	 * 
	 * @param criterion
	 *            Criterion specifying the criterias of the query
	 * @return List of found objects
	 */
	public List<T> findByCriteria(Criterion... criterion);

	public void reattach(T object);

	/**
	 * Refreshes the given object in the session.
	 * 
	 * @param object
	 */
	public void refresh(T object);

	/**
	 * @return all the stored objects
	 */
	public List<T> getAll();

}
