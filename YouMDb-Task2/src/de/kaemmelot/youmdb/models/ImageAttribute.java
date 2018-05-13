package de.kaemmelot.youmdb.models;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

@Entity
public class ImageAttribute extends MovieAttribute {
	public final static String NAME = "image";
	
	@Type(type = "image")
	private byte[] image = null;
	
	protected ImageAttribute() {
	}
	
	public ImageAttribute(BufferedImage image) {
		setImage(image);
	}
	
	public BufferedImage getImage() {
		try {
			return ImageIO.read(new ByteArrayInputStream(image));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void setImage(BufferedImage image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "png", baos);
			baos.flush();
			this.image = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
			this.image = null;
		}
	}
}
