package de.kaemmelot.youmdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.kaemmelot.youmdb.gui.YoumdbWindow;
import de.kaemmelot.youmdb.models.Genre;

public class YouMDb extends YoumdbWindow {
	private static String[] DEFAULT_GENRES = { "Action", "Adventure", "Comedy", "Crime", "Documentary", "Drama", "Historical", "Horror", "Musical", "Science Fiction", "Thriller", "War", "Western" };
	
	private void startup() {
		original();
		Database db = Database.getInstance();
		List<Genre> genres = db.getAll(Genre.class);
		List<String> defaultGenres = new ArrayList<String>(Arrays.asList(DEFAULT_GENRES));
		for (Genre genre : genres) {
			if (defaultGenres.contains(genre.getName()))
				defaultGenres.remove(genre.getName());
		}
		// Add remaining/missing genres
		if (!defaultGenres.isEmpty()) {
			db.startTransaction();
			for (String genre: defaultGenres)
				db.add(new Genre(genre, "This is a default genre."));
			db.endTransaction();
		}
	}
}
