package de.kaemmelot.youmdb.models;

import javax.persistence.Entity;

@Entity
public class RatingAttribute extends MovieAttribute {
	public final static String NAME = "rating";

	private Integer rating = null;
	
	protected RatingAttribute() {
	}
	
	public RatingAttribute(Integer rating) {
		setRating(rating);
	}
	
	public Integer getRating() {
		return rating;
	}
	
	public void setRating(Integer rating) {
		this.rating = rating;
	}
}
