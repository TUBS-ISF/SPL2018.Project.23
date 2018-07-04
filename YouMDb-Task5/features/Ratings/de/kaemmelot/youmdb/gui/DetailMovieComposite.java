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
import de.kaemmelot.youmdb.models.RatingAttribute;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DetailMovieComposite {
	private Text txtRating;
	
	private int getCenterWidth() {
		return 2 + original();
	}
	
	private int getCenterHeight() {
		return 1 + original();
	}
	
	private boolean next_ratings = false;
	private int addNextCenter(Composite content, int width, int maxHeight) {
		final int orig = original(content, width, maxHeight);
		if (orig == 0 && !next_ratings) {
			for (int i = 0; i < width - 2; i++)
				this.addPlaceholder(content);
			
			Label lblRating = new Label(content, SWT.NONE);
			lblRating.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			lblRating.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
			lblRating.setText("Rating (out of 10):");
			
			txtRating = new Text(content, SWT.NONE);
			txtRating.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			GridData gd_txtRating = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
			gd_txtRating.widthHint = 25;
			txtRating.setLayoutData(gd_txtRating);
			txtRating.setEditable(this.editable);
			txtRating.addVerifyListener(new VerifyListener() {
				public void verifyText(VerifyEvent e) {
		            String newS = txtRating.getText().substring(0, e.start) + e.text + txtRating.getText().substring(e.end);
		            boolean isValidInt = true;
		            try {
		            	Integer i = Integer.parseInt(newS, 10);
		            	isValidInt = i >= 0 && i <= 10;
		            } catch (NumberFormatException nfex) {
		            	isValidInt = false;
		            }
		            e.doit = isValidInt;
				}
			});
			txtRating.addListener(SWT.FocusOut, new Listener() {
				public void handleEvent(Event event) {
					RatingAttribute ra = getMovie().getAttribute(RatingAttribute.class);
					if (ra == null)
						ra = getMovie().setAttribute(RatingAttribute.class, new RatingAttribute());
					if (!txtRating.getText().isEmpty())
						ra.setRating(Integer.parseInt(txtRating.getText(), 10));
					else if ((txtRating.getText().isEmpty() || Integer.parseInt(txtRating.getText(), 10) == 0))
						ra.setRating(null);
				}
			});
			// https://stackoverflow.com/q/11522774
			txtRating.addPaintListener(getEditablePaintListener());
			new TextRedrawListener(txtRating);
			
			next_ratings = true;
			return 1;
		} else
			return orig;
	}

	public void setEditable(boolean editable) {
		txtRating.setEditable(editable);
		original(editable);
	}
	
	public void setMovie(Movie movie, boolean editable) {
		if (movie != null) {
			RatingAttribute ra = movie.getAttribute(RatingAttribute.class);
			if (ra == null)
				ra = movie.setAttribute(RatingAttribute.class, new RatingAttribute());
			txtRating.setText(ra.getRating() != null ? ra.getRating().toString() : "0");
		} else
			txtRating.setText("");
		original(movie, editable);
	}
}
