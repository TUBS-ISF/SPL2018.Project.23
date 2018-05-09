package de.kaemmelot.youmdb.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "MOVIES")
public class Movie {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private Integer Id;
	
	private String name;
	
	private String description;
	
	private Integer releaseYear;
	
	private Integer length;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return Id;
	}

	/**
	 * @return the movie name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the movie description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the release year of the movie
	 */
	public Integer getReleaseYear() {
		return releaseYear;
	}

	/**
	 * @return the length of the movie in minutes
	 */
	public Integer getLength() {
		return length;
	}

	/**
	 * @param id the id to set
	 * @deprecated should only be used by ORM
	 */
	public void setId(Integer id) {
		Id = id;
	}

	/**
	 * @param name the movie name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param description the movie description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param releaseYear the release year of the movie to set
	 */
	public void setReleaseYear(Integer releaseYear) {
		this.releaseYear = releaseYear;
	}

	/**
	 * @param length the movie length in minutes to set
	 */
	public void setLength(Integer length) {
		this.length = length;
	}
}
