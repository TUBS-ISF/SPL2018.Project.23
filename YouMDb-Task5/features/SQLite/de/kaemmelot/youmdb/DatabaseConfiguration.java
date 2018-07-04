package de.kaemmelot.youmdb;

import com.beust.jcommander.Parameter;

public class DatabaseConfiguration {
	@Parameter(names={"--database", "-d"}, description="SQLite database file. Default: ./YouM.db")
	public String database = "./YouM.db";
}
