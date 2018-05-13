package de.kaemmelot.youmdb;


import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.MovieDatabase.DatabaseDriver;
import de.kaemmelot.youmdb.gui.YoumdbWindow;

public class YouMDb extends YoumdbWindow {

	public static void main(String[] args) {
		List<String> argList = Arrays.asList(args);
		MovieDatabase.configure(DatabaseDriver.SQLite, null, null, "./YouM.db", null, null);
		// global feature configuration
		new FeatureConfiguration(argList.contains("Posters"), argList.contains("Ratings"));
		
		YouMDb me = new YouMDb();
		try {
			me.startup();
			me.run();
		} finally {
			me.shutdown();
		}
	}

	private MovieDatabase md = null;
	
	private void startup() {
		md = MovieDatabase.getInstance();
		String version = md.getDatabaseVersion();
		System.out.printf("Connected to database: %s%n", version);
		open();
	}
	
	private void shutdown() {
		SWTResourceManager.dispose();
		if (md != null) {
			System.out.println("Shutting down database.");
			md.dispose();
		}
	}

	private void run() {
		// wait until window is closed
		Display display = Display.getDefault();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}
