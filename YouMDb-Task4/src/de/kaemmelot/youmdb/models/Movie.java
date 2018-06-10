package de.kaemmelot.youmdb.models;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
	
	//#if Posters
	@Type(type = "image")
	private byte[] image = null;
	//#endif
	
	//#if Ratings
	private Integer rating = null;
	//#endif
	
	//#if Genres
	@OneToMany(cascade = CascadeType.ALL)
	private Set<Genre> genres;
	//#endif
	
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
	
	//#if Posters
	/**
	 * @return The poster image as BufferedImage
	 */
	public BufferedImage getImage() {
		if (image == null)
			return null;
		try {
			return ImageIO.read(new ByteArrayInputStream(image));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	//#endif
	
	//#if Ratings
	/**
	 * @return the movie rating or null
	 */
	public Integer getRating() {
		return rating;
	}
	//#endif

	//#if Genres
	/**
	 * @return the assigned Genres
	 */
	public Set<Genre> getGenres() {
		return Collections.unmodifiableSet(genres);
	}
	//#endif
	
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
	
	//#if Posters
	/**
	 * @param image the poster to set, can be null
	 */
	public void setImage(BufferedImage image) {
		if (image == null) {
			this.image = null;
			return;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", baos);
			baos.flush();
			this.image = baos.toByteArray(); // TODO somehow this gets really big
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
			this.image = null;
		}
	}
	//#endif
	
	//#if Ratings
	/**
	 * @param rating movie rating or null
	 */
	public void setRating(Integer rating) {
		this.rating = rating;
	}
	//#endif
	
	//#if Genres
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
	//#endif
}
