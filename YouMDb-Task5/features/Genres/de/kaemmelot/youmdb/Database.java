package de.kaemmelot.youmdb;

import java.util.List;

import de.kaemmelot.youmdb.models.Genre;
import de.kaemmelot.youmdb.models.GenreAttribute;

public class Database {	
	public static List<Class<?>> getClasses() {
		List<Class<?>> result = original();
		result.add(Genre.class);
		result.add(GenreAttribute.class);
		return result;
	}
}
