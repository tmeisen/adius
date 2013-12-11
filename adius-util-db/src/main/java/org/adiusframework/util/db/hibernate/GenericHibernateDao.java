package org.adiusframework.util.db.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import org.adiusframework.util.db.GenericDao;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

public class GenericHibernateDao<T, ID extends Serializable> implements GenericDao<T, ID> {
	private Class<T> persistentClass;

	private SessionFactory factory;

	@SuppressWarnings("unchecked")
	public GenericHibernateDao() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0];
	}

	public GenericHibernateDao(SessionFactory factory) {
		this.factory = factory;
	}

	protected Session getSession() {
		return getFactory().getCurrentSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.adiusframework.util.db.hibernate.GenericDao#getFactory()
	 */
	public SessionFactory getFactory() {
		return this.factory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.adiusframework.util.db.hibernate.GenericDao#setFactory(org.hibernate
	 * .SessionFactory)
	 */
	@Required
	@Autowired
	public void setFactory(SessionFactory factory) {
		this.factory = factory;
	}

	@Override
	public boolean checkConfiguration() {
		return persistentClass != null && factory != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.adiusframework.util.db.hibernate.GenericDao#findById(ID)
	 */
	@Override
	public T findById(ID id) {
		T result = this.persistentClass.cast(this.getSession().get(this.persistentClass, id));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.adiusframework.util.db.hibernate.GenericDao#reattach(T)
	 */
	@Override
	public void reattach(T object) {
		getSession().buildLockRequest(LockOptions.READ).lock(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.adiusframework.util.db.hibernate.GenericDao#save(T)
	 */
	@Override
	public void save(T object) {
		getSession().save(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.adiusframework.util.db.GenericDao#saveAll(java.util.Collection)
	 */
	@Override
	public void saveAll(Collection<T> objects) {
		for (T object : objects) {
			save(object);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.adiusframework.util.db.hibernate.GenericDao#update(T)
	 */
	@Override
	public void update(T object) {
		this.getSession().update(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.adiusframework.util.db.hibernate.GenericDao#merge(T)
	 */
	@Override
	public T merge(T object) {
		return this.persistentClass.cast(this.getSession().merge(object));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.adiusframework.util.db.hibernate.GenericDao#saveOrUpdate(T)
	 */
	@Override
	public void saveOrUpdate(T object) {
		this.getSession().saveOrUpdate(object);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.adiusframework.util.db.hibernate.GenericDao#delete(T)
	 */
	@Override
	public void delete(T object) {
		this.getSession().delete(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.adiusframework.util.db.hibernate.GenericDao#findByCriteria(org.hibernate
	 * .criterion.Criterion)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(Criterion... criterion) {
		Criteria criteria = this.getSession().createCriteria(this.persistentClass);
		for (Criterion c : criterion)
			criteria.add(c);
		List<T> result = criteria.list();
		return result;
	}

	@Override
	public void refresh(T object) {
		getSession().refresh(object);
	}
	
	@Override
	public List<T> getAll() {
		
		@SuppressWarnings("unchecked")
		List<T> result = getSession().createCriteria(this.persistentClass).list();
		return result;
	}

}
