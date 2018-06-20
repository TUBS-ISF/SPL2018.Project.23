package de.kaemmelot.youmdb.plugins;

import com.beust.jcommander.Parameter;

import de.kaemmelot.youmdb.Database;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.Environment;

import com.beust.jcommander.JCommander;
import de.kaemmelot.youmdb.Plugin;
import de.kaemmelot.youmdb.YouMDb;

public class MySQLPlugin implements Plugin {
	@Parameter(names="--host", description="MySQL host. Default: localhost")
	private String host = "localhost";
	
	@Parameter(names="--port", description="MySQL port. Default: 3306")
	private Integer port = 3306;
	
	@Parameter(names={"--database", "-d"}, description="MySQL database name. Default: YouMDb")
	private String database = "YouMDb";
	
	@Parameter(names={"--user", "--username", "-u"}, description="MySQL username for authentication")
	private String user;
	
	@Parameter(names={"--password", "-p"}, description="MySQL password for authentication", password = true)
	private String password;
	
	@Parameter(names = "--help", help = true)
	private boolean help;
	
	public void registerPlugin(String[] args) {
		JCommander com = JCommander.newBuilder()
			.addObject(this)
			.build();
		com.parse(args);
		
		if (user == null)
			com.usage();

		// put all settings in a map
		Map<String, String> conf = new HashMap<String, String>();
		conf.put(Environment.URL, "jdbc:mysql://" + host + ':' + port + '/' + database + "?useLegacyDatetimeCode=false&serverTimezone=UTC");
		conf.put(Environment.USER, user);
		if (password != null)
			conf.put(Environment.PASS, password);
		conf.put(Environment.HBM2DDL_AUTO, "update");
		Database.setConfiguration(conf, "select version();");
	}

	public void registerOverviewTabs(YouMDb window) { }
}
