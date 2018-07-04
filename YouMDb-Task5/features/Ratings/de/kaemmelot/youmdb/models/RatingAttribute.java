package de.kaemmelot.youmdb.models;

import javax.persistence.Entity;

import de.kaemmelot.youmdb.models.MovieAttribute;

@Entity
public class RatingAttribute extends MovieAttribute {
	private Integer rating = null;
	
	/**
	 * @return the movie rating or null
	 */
	public Integer getRating() {
		return rating;
	}
	
	/**
	 * @param rating movie rating or null
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}
}
