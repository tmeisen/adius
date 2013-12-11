package org.adiusframework.util.db.hibernate;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactoryBuilder {

	/**
	 * Builds a new session factory depending on the given properties. The
	 * properties must match the naming convention of Hibernate.
	 * 
	 * @param configuration
	 *            Configuration file used for Hibernate.
	 * @param properties
	 *            Properties containing the settings for database connection.
	 * @return
	 */
	public static SessionFactory build(String configuration, Properties properties) {
		final Configuration cfg = new Configuration();
		cfg.configure(configuration);
		for (Object key : properties.keySet())
			cfg.setProperty(String.class.cast(key), properties.getProperty(String.class.cast(key)));
		return cfg.buildSessionFactory();
	}
}
