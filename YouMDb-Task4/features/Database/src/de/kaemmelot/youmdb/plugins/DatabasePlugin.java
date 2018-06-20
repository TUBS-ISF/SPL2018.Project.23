package de.kaemmelot.youmdb.plugins;

import de.kaemmelot.youmdb.Database;
import de.kaemmelot.youmdb.Plugin;
import de.kaemmelot.youmdb.YouMDb;
import de.kaemmelot.youmdb.YouMDbPlugin;
import de.kaemmelot.youmdb.models.Movie;
import de.kaemmelot.youmdb.models.MovieAttribute;

public class DatabasePlugin implements Plugin, YouMDbPlugin {
	private Database db = null;
	
	public void startup() {
		db = Database.getInstance();
		String version = db.getDatabaseVersion();
		System.out.printf("Connected to database: %s%n", version);
	}

	public void shutdown() {
		if (db != null) {
			System.out.println("Shutting down database.");
			db.dispose();
		}
	}

	public void registerPlugin(String[] args) {
		Database.registerClass(Movie.class);
		Database.registerClass(MovieAttribute.class);
	}

	public void registerOverviewTabs(YouMDb window) { }
}
