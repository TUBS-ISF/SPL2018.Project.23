package de.kaemmelot.youmdb.models;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import de.kaemmelot.youmdb.models.MovieAttribute;

@Entity
public class PosterAttribute extends MovieAttribute {
	@Type(type = "image")
	private byte[] image = null;
	
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
}
