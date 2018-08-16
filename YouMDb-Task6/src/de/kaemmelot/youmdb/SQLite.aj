package de.kaemmelot.youmdb;

import java.util.HashMap;

import org.hibernate.Session;
import org.hibernate.cfg.Environment;

import com.beust.jcommander.JCommander;

public privileged aspect SQLite {	
	before(String[] args): execution(static void setConfiguration(String[])) && args(args) {
		SQLiteDatabaseConfiguration conf = new SQLiteDatabaseConfiguration();
		JCommander com = JCommander.newBuilder()
				.addObject(conf)
				.build();
		com.parse(args);

		// put all settings in a map
		Database.configuration = new HashMap<String, String>();
		// https://docs.jboss.org/hibernate/orm/5.0/manual/en-US/html/ch03.html
		Database.configuration.put(Environment.DRIVER,     "org.sqlite.JDBC");
		Database.configuration.put(Environment.DIALECT,    "org.hibernate.dialect.SQLiteDialect");
		Database.configuration.put(Environment.URL,        "jdbc:sqlite:" + conf.database);
		Database.configuration.put(Environment.AUTOCOMMIT, "true"); // needed, since SQLite would block itself
 		// host, port, user and password isn't needed
		Database.configuration.put(Environment.HBM2DDL_AUTO, "update");
	}
	
	String around(Database that): execution(String getDatabaseVersion()) && this(that) {
		Session session = null;
		String result;
		try {
			session = that.sessionFactory.openSession();
			result = (String) session.createNativeQuery("select 'SQLite ' || sqlite_version();").getSingleResult();
		} finally {
			if (session != null) session.close();
		}
		return result;
	}
}
