package de.kaemmelot.youmdb;

import com.beust.jcommander.Parameter;

public class PostgreSQLDatabaseConfiguration extends DatabaseConfiguration {
	@Parameter(names="--host", description="PostgreSQL host. Default: localhost")
	public String host = "localhost";
	
	@Parameter(names="--port", description="PostgreSQL port. Default: 5432")
	public Integer port = 5432;
	
	@Parameter(names={"--database", "-d"}, description="PostgreSQL database name. Default: youmdb")
	public String database = "youmdb";
	
	@Parameter(names={"--user", "--username", "-u"}, description="PostgreSQL username for authentication")
	public String user;
	
	@Parameter(names={"--password", "-p"}, description="PostgreSQL password for authentication", password = true)
	public String password;
}
