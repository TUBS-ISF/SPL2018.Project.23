package de.kaemmelot.youmdb.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GENRES")
public class Genre {
	@Id
	@GeneratedValue
	private Long Id;
	
	@Column(unique = true)
	private String name;
	
	private String description;

	public Genre() {
	}
	
	public Genre(String name) {
		this(name, "");
	}
	
	public Genre(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
