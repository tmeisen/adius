package org.adiusframework.processmanager.domain.dao.hibernate;

import java.util.List;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.ServiceTask;
import org.adiusframework.processmanager.domain.dao.ServiceTaskDao;
import org.adiusframework.util.db.hibernate.GenericHibernateDao;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

/**
 * The HibernateServiceTaskDao implements the needs of the ServiceTaskDao by
 * adding the suitable query to find the searched ServiceTasks in the database.
 */
@Repository
public class HibernateServiceTaskDao extends GenericHibernateDao<ServiceTask, Integer> implements ServiceTaskDao {

	/**
	 * Creates a HibernateServiceTaskDao without a SessionFactory.
	 */
	public HibernateServiceTaskDao() {
		super();
	}

	/**
	 * Creates a HibernateServiceTaskDao with the given SessionFactory.
	 * 
	 * @param factory
	 *            the given SessionFactory.
	 */
	public HibernateServiceTaskDao(SessionFactory factory) {
		super(factory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ServiceTask> findByProcess(ServiceProcess process) {
		String hql = "select t from ServiceTask t where t.process = ?";
		return this.getSession().createQuery(hql).setEntity(0, process).list();
	}

}
