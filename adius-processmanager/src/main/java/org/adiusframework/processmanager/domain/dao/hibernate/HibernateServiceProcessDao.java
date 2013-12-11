package org.adiusframework.processmanager.domain.dao.hibernate;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.dao.ServiceProcessDao;
import org.adiusframework.util.db.hibernate.GenericHibernateDao;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

/**
 * The HibernateServiceProcessDao implements the needs of the ServiceProcessDao
 * by adding the suitable query to find the searched ServiceProcesses in the
 * database.
 */
@Repository
public class HibernateServiceProcessDao extends GenericHibernateDao<ServiceProcess, Integer> implements
		ServiceProcessDao {

	/**
	 * Creates a HibernateServiceProcessDao without a SessionFactory.
	 */
	public HibernateServiceProcessDao() {
		super();
	}

	/**
	 * Creates a HibernateServiceProcessDao with the given SessionFactory.
	 * 
	 * @param factory
	 *            the given SessionFactory.
	 */
	public HibernateServiceProcessDao(SessionFactory factory) {
		super(factory);
	}

	@Override
	public ServiceProcess findByInternalId(String id) {
		String hql = "select p from ServiceProcess p where p.internalId = ?";
		return (ServiceProcess) this.getSession().createQuery(hql).setString(0, id).uniqueResult();
	}
}
