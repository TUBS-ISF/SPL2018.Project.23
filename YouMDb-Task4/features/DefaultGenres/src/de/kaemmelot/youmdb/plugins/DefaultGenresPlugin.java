package de.kaemmelot.youmdb.plugins;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.kaemmelot.youmdb.Database;
import de.kaemmelot.youmdb.Plugin;
import de.kaemmelot.youmdb.YouMDb;
import de.kaemmelot.youmdb.YouMDbPlugin;
import de.kaemmelot.youmdb.models.Genre;

public class DefaultGenresPlugin implements Plugin, YouMDbPlugin {
	private static String[] DEFAULT_GENRES = { "Action", "Adventure", "Comedy", "Crime", "Documentary", "Drama", "Historical", "Horror", "Musical", "Science Fiction", "Thriller", "War", "Western" };
	
	public void registerPlugin(String[] args) { }

	public void registerOverviewTabs(final YouMDb window) { }

	public void startup() {
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

	public void shutdown() { }
}
