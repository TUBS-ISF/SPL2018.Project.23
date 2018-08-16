package de.kaemmelot.youmdb;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.experimental.swt.SWTUtils;

import de.kaemmelot.youmdb.gui.DetailMovieComposite;
import de.kaemmelot.youmdb.gui.ExtendedListItem;
import de.kaemmelot.youmdb.models.Movie;
import de.kaemmelot.youmdb.models.PosterAttribute;

public privileged aspect Posters {
	public static final int IMAGE_HEIGHT = 90;
	public static final int IMAGE_WIDTH = 62;
	
	// Database
	List<Class<?>> around(): execution(static List<Class<?>> Database.getClasses()) {
		List<Class<?>> result = proceed();
		result.add(PosterAttribute.class);
		return result;
	}

	// ExtendedListItem
	private CLabel ExtendedListItem.lblImage = null;
	
	int around(): execution(int ExtendedListItem.getLeftWidth()) {
		return 1 + proceed();
	}
	
	int around(): execution(int ExtendedListItem.getLeftHeight()) {
		return 2 + proceed();
	}
	
	private boolean ExtendedListItem.next_posters = false;
	int around(ExtendedListItem that, Composite content, int width, int maxHeight):
		execution(int ExtendedListItem.addNextLeft(Composite, int, int)) && this(that) && args(content, width, maxHeight) {
		if (!that.next_posters) {
			that.lblImage = new CLabel(content, SWT.BORDER | SWT.SHADOW_OUT);
			GridData lblImageGd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
			that.lblImage.setLayoutData(lblImageGd);
			that.lblImage.setTopMargin(2);
			that.lblImage.setRightMargin(2);
			that.lblImage.setLeftMargin(2);
			that.lblImage.setBottomMargin(2);
			that.lblImage.setSize(new Point(IMAGE_WIDTH, IMAGE_HEIGHT));
			that.lblImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			that.lblImage.setText(null);
			that.lblImage.addListener(SWT.MouseDown, that.passThroughListener);
			
			that.next_posters = true;
			return 2;
		} else
			return proceed(that, content, width, maxHeight);
	}
	
	after(ExtendedListItem that): execution(void ExtendedListItem.movieChanged()) && this(that) {
		//org.eclipse.swt.graphics.Image img = that.lblImage.getImage();
		if (that.movie != null) {
			PosterAttribute pa = that.movie.getAttribute(PosterAttribute.class);
			if (pa != null && pa.getImage() != null) {
				that.lblImage.setImage(new org.eclipse.swt.graphics.Image(that.getFont().getDevice(),
						SWTUtils.convertAWTImageToSWT(pa.getImage()
								.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH))));
			} else
				that.lblImage.setImage(SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png"));
		} else
			that.lblImage.dispose();
		//if (img != null)
		//	img.dispose();
	}
	
	// DetailMovieComposite
	private CLabel DetailMovieComposite.lblImage;

	//private org.eclipse.swt.graphics.Image image;
	private final static org.eclipse.swt.graphics.Image noImage = SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png");
	
	int around(): execution(int DetailMovieComposite.getLeftWidth()) {
		return 1 + proceed();
	}
	
	int around(): execution(int DetailMovieComposite.getLeftHeight()) {
		return 3 + proceed();
	}
	
	private boolean DetailMovieComposite.next_posters = false;
	int around(final DetailMovieComposite that, Composite content, int width, int maxHeight):
		execution(int DetailMovieComposite.addNextLeft(Composite, int, int)) && this(that) && args(content, width, maxHeight) {
		if (!that.next_posters) {
			that.lblImage = new CLabel(content, SWT.BORDER | SWT.SHADOW_OUT);
			GridData gd_lblImage = new GridData(SWT.LEFT, SWT.CENTER, false, false, width, maxHeight);
			that.lblImage.setLayoutData(gd_lblImage);
			that.lblImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			that.lblImage.setTopMargin(2);
			that.lblImage.setBottomMargin(2);
			that.lblImage.setRightMargin(2);
			that.lblImage.setLeftMargin(2);
			that.lblImage.setText(null);
			that.lblImage.setImage(noImage);
			that.lblImage.setSize(new Point(IMAGE_WIDTH, IMAGE_HEIGHT));
			that.lblImage.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
			that.lblImage.addListener(SWT.MouseDown, new Listener() {
				public void handleEvent(Event event) {
					if (!that.getEditable())
						return;
					
					FileDialog fd = new FileDialog(that.getShell(), SWT.OPEN);
					fd.setText("Select poster");
					fd.setFilterExtensions(new String[] {"*.jpg", "*.jpeg", "*.png", "*.gif"});
					String selected = fd.open();
					if (selected != null) { // null == cancel
						try {
							BufferedImage origImg = ImageIO.read(new File(selected).toURI().toURL());
							// just to be sure it's in the correct format
							BufferedImage convImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), BufferedImage.TYPE_INT_RGB);
							convImg.createGraphics().drawImage(origImg, 0, 0, null);
	
							PosterAttribute pa = that.getMovie().getAttribute(PosterAttribute.class);
							if (pa == null)
								pa = that.getMovie().setAttribute(PosterAttribute.class, new PosterAttribute());
							pa.setImage(convImg);
							
							that.movieChanged(that.getMovie());
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
				}
			});
			// TODO user: delete image?
			
			that.next_posters = true;
			return maxHeight;
		} else
			return proceed(that, content, width, maxHeight);
	}

	before(DetailMovieComposite that, boolean editable): execution(void DetailMovieComposite.setEditable(boolean)) && this(that) && args(editable) {
		that.lblImage.setCursor(SWTResourceManager.getCursor(editable ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
	}
	
	private void DetailMovieComposite.movieChanged(Movie movie) {
		//if (image != null) // release old image
		//	image.dispose();

		//image = null;
		org.eclipse.swt.graphics.Image newImg = noImage;
		PosterAttribute pa = movie != null ? movie.getAttribute(PosterAttribute.class) : null;
		if (pa != null && pa.getImage() != null) {
			newImg = /*image = */new org.eclipse.swt.graphics.Image(this.getFont().getDevice(),
					SWTUtils.convertAWTImageToSWT(pa.getImage()
					.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH)));
		}
		
		lblImage.setImage(newImg);
	}
	
	before(DetailMovieComposite that, Movie movie, boolean editable):
		execution(void DetailMovieComposite.setMovie(Movie, boolean)) && this(that) && args(movie, editable) {
		that.movieChanged(movie);
	}
}
