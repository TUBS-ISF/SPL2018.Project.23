package de.kaemmelot.youmdb;

// used to declare global precedence
public aspect Base {
	declare precedence: Base, Db, MySQL, PostgreSQL, SQLite, Search, Posters, Ratings, Genres, DefaultGenres, Actors, Notes;
}
