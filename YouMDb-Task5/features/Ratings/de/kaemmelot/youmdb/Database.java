package de.kaemmelot.youmdb;

import java.util.List;

import de.kaemmelot.youmdb.models.RatingAttribute;

public class Database {	
	public static List<Class<?>> getClasses() {
		List<Class<?>> result = original();
		result.add(RatingAttribute.class);
		return result;
	}
}
