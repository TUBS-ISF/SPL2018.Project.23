package de.kaemmelot.youmdb.models;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MOVIES")
public class Movie {
	@Id
	@GeneratedValue
	private Long Id;
	
	private String name;
	
	private String description;
	
	private Integer releaseYear;
	
	private Integer length;
	
	@ElementCollection
	private Map<String, MovieAttribute> attributes = new HashMap<String, MovieAttribute>();
	
	public Movie() {
	}
	
	public Movie(String name, String desc, Integer releaseYear, Integer length) {
		this.name = name;
		this.description = desc;
		this.releaseYear = releaseYear;
		this.length = length;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
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
	 * Check if movie contains an attribute 
	 * @param name the attribute name
	 * @return if the attribute is contained
	 */
	public boolean containsAttribute(String name) {
		return attributes.containsKey(name);
	}

	/**
	 * @return the attribute
	 */
	public MovieAttribute getAttribute(String name) {
		return attributes.get(name);
	}

	/**
	 * Get all attributes from this movie
	 * @return the attributes
	 */
	public Collection<MovieAttribute> getAttributes() {
		return attributes.values();
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

	/**
	 * @param name the name for the attribute
	 * @param attribute the attribute to set
	 */
	public void addAttribute(String name, MovieAttribute attribute) {
		this.attributes.put(name, attribute);
	}

	/**
	 * @param name the name for the attribute
	 */
	public void removeAttribute(String name) {
		this.attributes.remove(name);
	}
}
