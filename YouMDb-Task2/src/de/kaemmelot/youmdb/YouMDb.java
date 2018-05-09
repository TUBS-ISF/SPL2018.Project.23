package de.kaemmelot.youmdb;


import de.kaemmelot.youmdb.MovieDatabase.DatabaseDriver;
import de.kaemmelot.youmdb.models.Movie;

public class YouMDb {

	public static void main(String[] args) {
		System.out.println("Configuring database.");
		MovieDatabase.Configure(DatabaseDriver.SQLite, null, null, "./YouM.db", null, null);
		MovieDatabase md = MovieDatabase.GetInstance();
		String version = md.GetDatabaseVersion();
		System.out.printf("Connected to database: %s%n", version);
	}

}
