package org.adiusframework.util.db.hibernate;

import java.util.List;

import org.adiusframework.util.db.BulkLoader;
import org.adiusframework.util.db.BulkLoaderContainer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class HibernateUtil {

	public static Session getSession(SessionFactory factory) {
		return factory.getCurrentSession();
	}

	public static boolean ensureTransaction(SessionFactory factory) {

		// create a new transaction if handled by this method
		boolean handleTransaction = !(HibernateUtil.getSession(factory).getTransaction().isActive());
		if (handleTransaction)
			HibernateUtil.getSession(factory).beginTransaction();
		return handleTransaction;
	}

	@SuppressWarnings("unchecked")
	public static List<Object> nativeSQLListQuery(String sql, SessionFactory factory) {
		Session session = HibernateUtil.getSession(factory);
		boolean transaction = HibernateUtil.ensureTransaction(factory);
		if (!transaction)
			session.beginTransaction();
		try {
			return HibernateUtil.getSession(factory).createSQLQuery(sql).list();
		} finally {
			if (!transaction)
				session.getTransaction().commit();
		}
	}

	public static Object nativeSQLUniqueQuery(String sql, SessionFactory factory) {
		Session session = HibernateUtil.getSession(factory);
		boolean transaction = HibernateUtil.ensureTransaction(factory);
		if (!transaction)
			session.beginTransaction();

		try {
			return HibernateUtil.getSession(factory).createSQLQuery(sql).uniqueResult();
		} finally {
			if (!transaction)
				session.getTransaction().commit();
		}
	}

	public static Integer writeLoader(BulkLoader loader, SessionFactory factory) {

		// writing data to destination
		boolean handle = false;
		try {
			handle = HibernateUtil.ensureTransaction(factory);
			BulkLoaderWork work = new BulkLoaderWork(loader);
			HibernateUtil.getSession(factory).doWork(work);
			return work.getResult();
		} finally {
			if (handle)
				HibernateUtil.getSession(factory).getTransaction().commit();
		}
	}

	public static void commitTransation(boolean handled, SessionFactory factory) {
		if (handled)
			HibernateUtil.getSession(factory).getTransaction().commit();
	}

	public static Integer writeContainer(BulkLoaderContainer container, SessionFactory factory) {

		// write each loader into database
		int sumCount = 0;
		for (BulkLoader loader : container.values())
			sumCount += HibernateUtil.writeLoader(loader, factory);
		return sumCount;
	}
}
