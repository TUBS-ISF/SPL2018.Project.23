package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.experimental.swt.SWTUtils;

import de.kaemmelot.youmdb.models.Movie;
import de.kaemmelot.youmdb.models.PosterAttribute;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DetailMovieComposite {
	private CLabel lblImage;

	//private org.eclipse.swt.graphics.Image image;
	private final org.eclipse.swt.graphics.Image noImage = SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png");
	
	public static final int IMAGE_HEIGHT = 90;
	public static final int IMAGE_WIDTH = 62;
	
	private int getLeftWidth() {
		return 1 + original();
	}
	
	private int getLeftHeight() {
		return 3 + original();
	}
	
	private boolean next_posters = false;
	private int addNextLeft(Composite content, int width, int maxHeight) {
		if (!next_posters) {
			lblImage = new CLabel(content, SWT.BORDER | SWT.SHADOW_OUT);
			GridData gd_lblImage = new GridData(SWT.LEFT, SWT.CENTER, false, false, width, maxHeight);
			lblImage.setLayoutData(gd_lblImage);
			lblImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			lblImage.setTopMargin(2);
			lblImage.setBottomMargin(2);
			lblImage.setRightMargin(2);
			lblImage.setLeftMargin(2);
			lblImage.setText(null);
			lblImage.setImage(noImage);
			lblImage.setSize(new Point(IMAGE_WIDTH, IMAGE_HEIGHT));
			lblImage.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
			lblImage.addListener(SWT.MouseDown, new Listener() {
				public void handleEvent(Event event) {
					if (!getEditable())
						return;
					
					FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
					fd.setText("Select poster");
					fd.setFilterExtensions(new String[] {"*.jpg", "*.jpeg", "*.png", "*.gif"});
					String selected = fd.open();
					if (selected != null) { // null == cancel
						try {
							BufferedImage origImg = ImageIO.read(new File(selected).toURI().toURL());
							// just to be sure it's in the correct format
							BufferedImage convImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), BufferedImage.TYPE_INT_RGB);
							convImg.createGraphics().drawImage(origImg, 0, 0, null);
	
							PosterAttribute pa = getMovie().getAttribute(PosterAttribute.class);
							if (pa == null)
								pa = getMovie().setAttribute(PosterAttribute.class, new PosterAttribute());
							pa.setImage(convImg);
							
							movieChanged(getMovie());
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
				}
			});
			// TODO user: delete image?
			
			next_posters = true;
			return maxHeight;
		} else
			return original(content, width, maxHeight);
	}

	public void setEditable(boolean editable) {
		lblImage.setCursor(SWTResourceManager.getCursor(editable ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
		original(editable);
	}
	
	private void movieChanged(Movie movie) {
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
	
	public void setMovie(Movie movie, boolean editable) {
		movieChanged(movie);
		original(movie, editable);
	}
}
