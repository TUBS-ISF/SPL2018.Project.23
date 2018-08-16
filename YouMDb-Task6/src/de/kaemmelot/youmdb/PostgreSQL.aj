package de.kaemmelot.youmdb;

import java.util.HashMap;

import org.hibernate.cfg.Environment;

import com.beust.jcommander.JCommander;

public privileged aspect PostgreSQL {	
	before(String[] args): execution(static void setConfiguration(String[])) && args(args) {
		PostgreSQLDatabaseConfiguration conf = new PostgreSQLDatabaseConfiguration();
		JCommander com = JCommander.newBuilder()
				.addObject(conf)
				.build();
		com.parse(args);
		
		if (conf.user == null)
			com.usage();

		// put all settings in a map
		Database.configuration = new HashMap<String, String>();
		Database.configuration.put(Environment.URL, "jdbc:postgresql://" + conf.host + ':' + conf.port + '/' + conf.database);
		Database.configuration.put(Environment.USER, conf.user);
		if (conf.password != null)
			Database.configuration.put(Environment.PASS, conf.password);
		Database.configuration.put(Environment.HBM2DDL_AUTO, "update");
	}
}
