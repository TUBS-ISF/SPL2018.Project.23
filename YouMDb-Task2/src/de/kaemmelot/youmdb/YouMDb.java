package de.kaemmelot.youmdb;


import org.eclipse.swt.widgets.Display;

import de.kaemmelot.youmdb.MovieDatabase.DatabaseDriver;
import de.kaemmelot.youmdb.gui.ExtendedListItem;
import de.kaemmelot.youmdb.gui.YoumdbWindow;
import de.kaemmelot.youmdb.models.Movie;

public class YouMDb extends YoumdbWindow {

	public static void main(String[] args) {
		MovieDatabase.Configure(DatabaseDriver.SQLite, null, null, "./YouM.db", null, null);
		new FeatureConfiguration(true); // TODO
		
		YouMDb me = new YouMDb();
		me.startup();
		me.run();
		me.shutdown();
	}

	private void startup() {
		String version = MovieDatabase.GetInstance().GetDatabaseVersion();
		System.out.printf("Connected to database: %s%n", version);
		open();
	}
	
	private void shutdown() {
		System.out.println("Shutting down database.");
		MovieDatabase.GetInstance().Shutdown();
	}

	private void run() {
		/*MovieDatabase md = MovieDatabase.GetInstance();
		Movie m;
		System.out.println("Creating movie foo");
		m = new Movie();
		m.setName("foo");
		m.setDescription("foos desc");
		m.setLength(30);
		m.setReleaseYear(1992);
		md.StartTransaction();
		MovieDatabase.GetInstance().AddMovie(m);
		md.EndTransaction();
		System.out.println("Creating movie bar");
		m = new Movie();
		m.setName("bar");
		m.setDescription("bars desc");
		m.setLength(4);
		m.setReleaseYear(1806);
		md.StartTransaction();
		MovieDatabase.GetInstance().AddMovie(m);
		md.EndTransaction();
		System.out.println("Listing movies");
		Movie m2 = null;
		for (Movie movie : md.GetMovies()) {
			System.out.printf("%s: %s - %s (%d mins); year: %s%n", movie.getId(), movie.getName(), movie.getDescription(), movie.getLength(), movie.getReleaseYear());
			if (movie.getName().equals("foo"))
				m2 = movie;
		}
		System.out.println("Removing movie foo");
		md.StartTransaction();
		md.RemoveMovie(m2);
		md.EndTransaction();
		System.out.println("Removing movie bar");
		md.StartTransaction();
		md.RemoveMovie(m);
		md.EndTransaction();
		System.out.println("Listing movies again");
		for (Movie movie : md.GetMovies())
			System.out.printf("%s: %s - %s (%d mins); year: %s%n", movie.getId(), movie.getName(), movie.getDescription(), movie.getLength(), movie.getReleaseYear());
		System.out.println("done");*/
		
		new ExtendedListItem(movieList, null, "Movie #1", "It's a great movie!");
		new ExtendedListItem(movieList, null, "Movie #2", "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.");
		new ExtendedListItem(movieList, null, "Movie #3", "It's a great movie!");
		new ExtendedListItem(movieList, null, "Movie #4", "It's a great movie!");
		new ExtendedListItem(movieList, null, "Movie #5", "It's a great movie!");
		movieList.Refresh();

		// wait until window is closed
		Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
