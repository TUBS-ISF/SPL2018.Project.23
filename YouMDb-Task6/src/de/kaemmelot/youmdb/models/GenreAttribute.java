package de.kaemmelot.youmdb.models;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class GenreAttribute extends MovieAttribute {
	@OneToMany(cascade = CascadeType.ALL)
	private Set<Genre> genres = new HashSet<Genre>();

	/**
	 * @return the assigned Genres
	 */
	public Set<Genre> getGenres() {
		return Collections.unmodifiableSet(genres);
	}
	
	/**
	 * @param genre the Genre to add
	 */
	public void addGenre(Genre genre) {
		genres.add(genre);
	}
	
	/**
	 * @param genre the Genre to remove
	 */
	public void removeGenre(Genre genre) {
		genres.remove(genre);
	}
	
	/**
	 * @param genres all genres that should be included after the current are kicked out
	 */
	public void replaceGenres(Collection<Genre> genres) {
		this.genres.clear();
		this.genres.addAll(genres);
	}
}
