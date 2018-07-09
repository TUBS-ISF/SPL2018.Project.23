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
		
		if (conf.user == null)
			com.usage();

		// put all settings in a map
		configuration = new HashMap<String, String>();
		configuration.put(Environment.URL, "jdbc:postgresql://" + conf.host + ':' + conf.port + '/' + conf.database);
		configuration.put(Environment.USER, conf.user);
		if (conf.password != null)
			configuration.put(Environment.PASS, conf.password);
		configuration.put(Environment.HBM2DDL_AUTO, "update");
		original(args);
	}
}
