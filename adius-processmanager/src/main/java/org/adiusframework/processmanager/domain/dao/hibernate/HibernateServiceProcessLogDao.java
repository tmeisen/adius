package org.adiusframework.processmanager.domain.dao.hibernate;

import java.util.List;

import org.adiusframework.processmanager.domain.ServiceProcess;
import org.adiusframework.processmanager.domain.ServiceProcessLog;
import org.adiusframework.processmanager.domain.ServiceTask;
import org.adiusframework.processmanager.domain.dao.ServiceProcessLogDao;
import org.adiusframework.util.db.hibernate.GenericHibernateDao;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

/**
 * The HibernateServiceProcessLogDao implements the needs of the
 * ServiceProcessLogDao by adding suitable queries to find the searched
 * ServiceProcessLogs in the database.
 */
@Repository
public class HibernateServiceProcessLogDao extends GenericHibernateDao<ServiceProcessLog, Integer> implements
		ServiceProcessLogDao {

	/**
	 * Creates a HibernateServiceProcessLogDao without a SessionFactory.
	 */
	public HibernateServiceProcessLogDao() {
		super();
	}

	/**
	 * Creates a HibernateServiceProcessLogDao with the given SessionFactory.
	 * 
	 * @param factory
	 *            the given SessionFactory.
	 */
	public HibernateServiceProcessLogDao(SessionFactory factory) {
		super(factory);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ServiceProcessLog> findLogs(ServiceProcess process) {
		String hql = "select l from ServiceProcessLog l where l.process = ?";
		return this.getSession().createQuery(hql).setEntity(0, process).list();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ServiceProcessLog> findLogs(ServiceTask task) {
		String hql = "select l from ServiceProcessLog l where l.task = ?";
		return this.getSession().createQuery(hql).setEntity(0, task).list();
	}
}
