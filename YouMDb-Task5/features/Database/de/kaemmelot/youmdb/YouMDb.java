package de.kaemmelot.youmdb;

import de.kaemmelot.youmdb.gui.YoumdbWindow;

public class YouMDb extends YoumdbWindow {
	private Database db = null;
	
	public static void main(String[] args) {
		Database.setConfiguration(args);
		original(args);
	}
	
	private void startup() {
		db = Database.getInstance();
		String version = db.getDatabaseVersion();
		System.out.printf("Connected to database: %s%n", version);
		original();
	}
	
	private void shutdown() {
		original();
		if (db != null) {
			System.out.println("Shutting down database.");
			db.dispose();
		}
	}
}
