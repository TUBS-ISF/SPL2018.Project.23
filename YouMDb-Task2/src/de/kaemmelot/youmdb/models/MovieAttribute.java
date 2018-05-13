package de.kaemmelot.youmdb.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class MovieAttribute {
	@Id
	@GeneratedValue
	private Long Id;

	/**
	 * @return the id
	 */
	public Long getId() {
		return Id;
	}
	
	protected MovieAttribute() {
	}
	
}
