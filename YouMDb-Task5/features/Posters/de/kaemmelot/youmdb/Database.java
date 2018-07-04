package de.kaemmelot.youmdb;

import java.util.List;

import de.kaemmelot.youmdb.models.PosterAttribute;

public class Database {	
	public static List<Class<?>> getClasses() {
		List<Class<?>> result = original();
		result.add(PosterAttribute.class);
		return result;
	}
}
