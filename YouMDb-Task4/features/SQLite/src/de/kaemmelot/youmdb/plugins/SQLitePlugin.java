package de.kaemmelot.youmdb.plugins;

import com.beust.jcommander.Parameter;

import de.kaemmelot.youmdb.Database;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Environment;

import com.beust.jcommander.JCommander;
import de.kaemmelot.youmdb.Plugin;
import de.kaemmelot.youmdb.YouMDb;

public class SQLitePlugin implements Plugin {
	@Parameter(names={"--database", "-d"}, description="SQLite database file. Default: ./YouM.db")
	private String database = "./YouM.db";
	
	@Parameter(names = "--help", help = true)
	private boolean help;
	
	public void registerPlugin(String[] args) {
		JCommander com = JCommander.newBuilder()
			.addObject(this)
			.build();
		com.parse(args);

		// put all settings in a map
		Map<String, String> conf = new HashMap<String, String>();
		// https://docs.jboss.org/hibernate/orm/5.0/manual/en-US/html/ch03.html
		conf.put(Environment.DRIVER,     "org.sqlite.JDBC");
		conf.put(Environment.DIALECT,    "org.hibernate.dialect.SQLiteDialect");
		conf.put(Environment.URL,        "jdbc:sqlite:" + database);
		conf.put(Environment.AUTOCOMMIT, "true"); // needed, since SQLite would block itself
 		// host, port, user and password isn't needed
		conf.put(Environment.HBM2DDL_AUTO, "update");
		Database.setConfiguration(conf, "select 'SQLite ' || sqlite_version();");
	}

	public void registerOverviewTabs(YouMDb window) { }
}
