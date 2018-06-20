package de.kaemmelot.youmdb.models;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.MapKeyType;
import org.hibernate.annotations.Type;

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
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = MovieAttribute.class)
	@MapKeyType(value = @Type(type = "class"))
	private Map<Class<? extends MovieAttribute>, MovieAttribute> attributes = new HashMap<Class<? extends MovieAttribute>, MovieAttribute>();
	
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
	 * @param cl the attribute class
	 * @return the attribute or null
	 */
	@SuppressWarnings("unchecked")
	public <T extends MovieAttribute> T getAttribute(Class<T> cl) {
		return (T) attributes.getOrDefault(cl, null);
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
	 * @param cl the attribute class
	 * @param ma the attribute
	 * @return the attribute itself
	 */
	public <T extends MovieAttribute> T setAttribute(Class<T> cl, T ma) {
		if (attributes.containsKey(cl))
			attributes.remove(cl);
		attributes.put(cl, ma);
		return ma;
	}
}
