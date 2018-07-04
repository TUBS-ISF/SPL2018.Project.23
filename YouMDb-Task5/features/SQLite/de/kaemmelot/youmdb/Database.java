package de.kaemmelot.youmdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;

import com.beust.jcommander.JCommander;

public class Database {
	public static void setConfiguration(String[] args) {
		DatabaseConfiguration conf = new DatabaseConfiguration();
		JCommander com = JCommander.newBuilder()
				.addObject(conf)
				.build();
		com.parse(args);

		// put all settings in a map
		configuration = new HashMap<String, String>();
		// https://docs.jboss.org/hibernate/orm/5.0/manual/en-US/html/ch03.html
		configuration.put(Environment.DRIVER,     "org.sqlite.JDBC");
		configuration.put(Environment.DIALECT,    "org.hibernate.dialect.SQLiteDialect");
		configuration.put(Environment.URL,        "jdbc:sqlite:" + conf.database);
		configuration.put(Environment.AUTOCOMMIT, "true"); // needed, since SQLite would block itself
 		// host, port, user and password isn't needed
		configuration.put(Environment.HBM2DDL_AUTO, "update");
		original(args);
	}
	
	public String getDatabaseVersion() {
		Session session = null;
		String result;
		try {
			session = sessionFactory.openSession();
			result = (String) session.createNativeQuery("select 'SQLite ' || sqlite_version();").getSingleResult();
		} finally {
			if (session != null) session.close();
		}
		return result;
	}
}
