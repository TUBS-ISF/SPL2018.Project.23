package de.kaemmelot.youmdb;

// Is now called Db because there cannot be a class and an aspect with the same name
public aspect Db {
	private Database YouMDb.db = null;

	before(YouMDb that): execution(void YouMDb.startup()) && this(that) {
		that.db = Database.getInstance();
		String version = that.db.getDatabaseVersion();
		System.out.printf("Connected to database: %s%n", version);
	}
	
	after(YouMDb that): execution(void YouMDb.shutdown()) && this(that) {
		if (that.db != null) {
			System.out.println("Shutting down database.");
			that.db.dispose();
		}
	}
	
	before(String[] args): execution(static void YouMDb.main(String[])) && args(args) {
		Database.setConfiguration(args);
	}
}
