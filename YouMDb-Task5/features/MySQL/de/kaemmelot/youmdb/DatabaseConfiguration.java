package de.kaemmelot.youmdb;

import com.beust.jcommander.Parameter;

public class DatabaseConfiguration {
	@Parameter(names="--host", description="MySQL host. Default: localhost")
	public String host = "localhost";
	
	@Parameter(names="--port", description="MySQL port. Default: 3306")
	public Integer port = 3306;
	
	@Parameter(names={"--database", "-d"}, description="MySQL database name. Default: YouMDb")
	public String database = "YouMDb";
	
	@Parameter(names={"--user", "--username", "-u"}, description="MySQL username for authentication")
	public String user;
	
	@Parameter(names={"--password", "-p"}, description="MySQL password for authentication", password = true)
	public String password;
}
