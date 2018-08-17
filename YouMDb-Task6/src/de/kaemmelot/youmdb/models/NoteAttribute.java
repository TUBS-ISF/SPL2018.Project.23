package de.kaemmelot.youmdb.models;

import javax.persistence.Entity;

import de.kaemmelot.youmdb.models.MovieAttribute;

@Entity
public class NoteAttribute extends MovieAttribute {
	private String note = "";
	
	public String getNote() {
		return note;
	}
	
	public void setNote(String note) {
		if (note == null)
			this.note = "";
		else
			this.note = note;
	}
}
