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

import de.kaemmelot.youmdb.FeatureConfiguration;
import de.kaemmelot.youmdb.models.ImageAttribute;
import de.kaemmelot.youmdb.models.Movie;
import de.kaemmelot.youmdb.models.RatingAttribute;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DetailMovieComposite extends ScrolledComposite {
	private final Text txtName;
	private final Text txtDesc;
	private final Text txtYear;
	private final Text txtLength;
	private final Text txtRating;
	private final CLabel lblImage;
	
	private org.eclipse.swt.graphics.Image image;
	private Movie currentMovie = null;
	private boolean editable = false;
	
	private final org.eclipse.swt.graphics.Image noImage = SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png");
	
	private final static int IMAGE_HEIGHT = 90;
	private final static int IMAGE_WIDTH = 62;

	private void addPlaceholder(Composite parent) {
		(new Label(parent, SWT.NONE)).setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
	}
	
	public DetailMovieComposite(Composite parent, final Shell shell) {
		super(parent, SWT.NONE);
		setExpandHorizontal(true);
		setExpandVertical(true);
		setLayout(new GridLayout());
		
		final Listener redrawListener = new Listener() {
			public void handleEvent(Event event) {
				redraw();
			}
		};
		
		Composite content = new Composite(this, SWT.NONE);
		content.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl;
		if (FeatureConfiguration.getInstance().usePosters())
			gl = new GridLayout(4, false);
		else
			gl = new GridLayout(3, false);
		content.setLayout(gl);
		
		PaintListener editablePaintListener = new PaintListener() { // TODO fix size and selection problems
			public void paintControl(PaintEvent e) {
				e.gc.setAntialias(SWT.ON);
				e.gc.setForeground(((Text) e.widget).getForeground());
				if (editable)
					e.gc.drawRoundRectangle(e.x, e.y, e.width - 1, e.height - 1, 6, 6);
			}
		};

		if (FeatureConfiguration.getInstance().usePosters()) {	
			lblImage = new CLabel(content, SWT.BORDER | SWT.SHADOW_OUT);
			int rows = 3;
			if (FeatureConfiguration.getInstance().useRatings())
				rows++;
			GridData gd_lblImage = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, rows);
			// This sometimes causes problems with showing the image
			//gd_lblImage.widthHint = 62;
			//gd_lblImage.heightHint = 90;
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
					if (!editable)
						return;
					
					FileDialog fd = new FileDialog(shell, SWT.OPEN);
					fd.setText("Select poster");
					fd.setFilterExtensions(new String[] {"*.jpg", "*.jpeg", "*.png", "*.gif"});
					String selected = fd.open();
					if (selected != null) { // null == cancel
						try {
							BufferedImage origImg = ImageIO.read(new File(selected).toURI().toURL());
							// just to be sure it's in the correct format
							BufferedImage convImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), BufferedImage.TYPE_INT_RGB);
							convImg.createGraphics().drawImage(origImg, 0, 0, null);
							
							Movie movie = getMovie();
							if (movie.containsAttribute(ImageAttribute.NAME))
								((ImageAttribute) movie.getAttribute(ImageAttribute.NAME)).setImage(convImg);
							else
								movie.addAttribute(ImageAttribute.NAME, new ImageAttribute(convImg));
							
							refreshImage();
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
					}
				}
			});
		} else
			lblImage = null;
		// TODO user: delete image?
		
		Label lblName = new Label(content, SWT.NONE);
		lblName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");
		
		txtName = new Text(content, SWT.NONE);
		txtName.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		txtName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		txtName.setEditable(editable);
		txtName.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				if (!txtName.getText().isEmpty())
					currentMovie.setName(txtName.getText());
				else
					txtName.setText(currentMovie.getName());
			}
		});
		// https://stackoverflow.com/q/11522774
		txtName.addPaintListener(editablePaintListener);
		txtName.addListener(SWT.Gesture | SWT.FocusIn | SWT.FocusOut | SWT.DragDetect | SWT.MouseUp | SWT.Selection, redrawListener);
		
		addPlaceholder(content);
		
		Label lblYear = new Label(content, SWT.NONE);
		lblYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblYear.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
		lblYear.setText("Year:");
		
		txtYear = new Text(content, SWT.NONE);
		txtYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		GridData gd_txtYear = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_txtYear.widthHint = 25;
		txtYear.setLayoutData(gd_txtYear);
		txtYear.setEditable(editable);
		txtYear.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
	            String newS = txtYear.getText().substring(0, e.start) + e.text + txtYear.getText().substring(e.end);
	            boolean isInt = true;
	            try {
	            	Integer.parseInt(newS, 10);
	            } catch (NumberFormatException nfex) {
	            	isInt = false;
	            }
	            e.doit = isInt;
			}
		});
		txtYear.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				if (!txtYear.getText().isEmpty()) {
					Integer year = Integer.parseInt(txtYear.getText(), 10);
					if (year >= 1900 && year <= 2100)
						currentMovie.setReleaseYear(year);
					else
						txtYear.setText(currentMovie.getReleaseYear().toString());
				} else {
					currentMovie.setReleaseYear(0);
					txtYear.setText(currentMovie.getReleaseYear().toString());
				}
			}
		});
		// https://stackoverflow.com/q/11522774
		txtYear.addPaintListener(editablePaintListener);
		txtYear.addListener(SWT.Gesture | SWT.FocusIn | SWT.FocusOut | SWT.DragDetect | SWT.MouseUp | SWT.Selection, redrawListener);

		addPlaceholder(content);
		
		Label lblLength = new Label(content, SWT.NONE);
		lblLength.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblLength.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
		lblLength.setText("Length (mins):");
		
		txtLength = new Text(content, SWT.NONE);
		txtLength.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		GridData gd_txtLength = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_txtLength.widthHint = 25;
		txtLength.setLayoutData(gd_txtLength);
		txtLength.setEditable(editable);
		txtLength.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
	            String newS = txtLength.getText().substring(0, e.start) + e.text + txtLength.getText().substring(e.end);
	            boolean isInt = true;
	            try {
	            	isInt = Integer.parseInt(newS, 10) >= 0;
	            } catch (NumberFormatException nfex) {
	            	isInt = false;
	            }
	            e.doit = isInt;
			}
		});
		txtLength.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				if (!txtLength.getText().isEmpty())
					currentMovie.setLength(Integer.parseInt(txtLength.getText(), 10));
				else
					currentMovie.setLength(0);
			}
		});
		// https://stackoverflow.com/q/11522774
		txtLength.addPaintListener(editablePaintListener);
		txtLength.addListener(SWT.Gesture | SWT.FocusIn | SWT.FocusOut | SWT.DragDetect | SWT.MouseUp | SWT.Selection, redrawListener);
		
		if (FeatureConfiguration.getInstance().useRatings()) {
			addPlaceholder(content);
			
			Label lblRating = new Label(content, SWT.NONE);
			lblRating.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			lblRating.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
			lblRating.setText("Rating (out of 10):");
			
			txtRating = new Text(content, SWT.NONE);
			txtRating.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			GridData gd_txtRating = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
			gd_txtRating.widthHint = 25;
			txtRating.setLayoutData(gd_txtRating);
			txtRating.setEditable(editable);
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
					if (!txtRating.getText().isEmpty() && currentMovie.containsAttribute(RatingAttribute.NAME))
						((RatingAttribute) currentMovie.getAttribute(RatingAttribute.NAME)).setRating(Integer.parseInt(txtRating.getText(), 10));
					else if (!txtRating.getText().isEmpty() && Integer.parseInt(txtRating.getText(), 10) != 0)
						currentMovie.addAttribute(RatingAttribute.NAME, new RatingAttribute(Integer.parseInt(txtRating.getText(), 10)));
					else if ((txtRating.getText().isEmpty() || Integer.parseInt(txtRating.getText(), 10) == 0) &&
							currentMovie.containsAttribute(RatingAttribute.NAME))
						((RatingAttribute) currentMovie.getAttribute(RatingAttribute.NAME)).setRating(null);
				}
			});
			// https://stackoverflow.com/q/11522774
			txtRating.addPaintListener(editablePaintListener);
			txtRating.addListener(SWT.Gesture | SWT.FocusIn | SWT.FocusOut | SWT.DragDetect | SWT.MouseUp | SWT.Selection, redrawListener);
		} else
			txtRating = null;
		
		Label lblDesc = new Label(content, SWT.NONE);
		lblDesc.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblDesc.setText("Description:");
		if (!FeatureConfiguration.getInstance().usePosters())
			(new Label(content, SWT.NONE)).setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		(new Label(content, SWT.NONE)).setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		(new Label(content, SWT.NONE)).setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		
		txtDesc = new Text(content, SWT.WRAP | SWT.MULTI);
		txtDesc.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		if (FeatureConfiguration.getInstance().usePosters())
			txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		else
			txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		txtDesc.setEditable(editable);
		txtDesc.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				currentMovie.setDescription(txtDesc.getText());
			}
		});
		// https://stackoverflow.com/q/11522774
		txtDesc.addPaintListener(editablePaintListener);
		txtDesc.addListener(SWT.Gesture | SWT.FocusIn | SWT.FocusOut | SWT.DragDetect | SWT.MouseUp | SWT.Selection, redrawListener);

		setContent(content);
	}
	
	protected void refreshImage() {
		if (!FeatureConfiguration.getInstance().usePosters())
			return;
		if (image != null) // release old image
			image.dispose();

		image = null;
		org.eclipse.swt.graphics.Image newImg = noImage;
		if (getMovie() != null && getMovie().containsAttribute(ImageAttribute.NAME) &&
				((ImageAttribute) getMovie().getAttribute(ImageAttribute.NAME)).getImage() != null) {
			newImg = image = new org.eclipse.swt.graphics.Image(getFont().getDevice(),
					SWTUtils.convertAWTImageToSWT(((ImageAttribute) getMovie().getAttribute(ImageAttribute.NAME)).getImage()
					.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH)));
		}
		
		lblImage.setImage(newImg);
	}

	public void setEditable(boolean editable) {
		this.editable  = editable;
		txtName.setEditable(editable);
		txtYear.setEditable(editable);
		txtLength.setEditable(editable);
		if (FeatureConfiguration.getInstance().useRatings())
			txtRating.setEditable(editable);
		txtDesc.setEditable(editable);
		if (FeatureConfiguration.getInstance().usePosters())
			lblImage.setCursor(SWTResourceManager.getCursor(editable ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
	}
	
	public void setMovie(Movie movie) {
		setMovie(movie, false);
	}
	
	public void setMovie(Movie movie, boolean editable) {
		currentMovie = movie;
		if (movie != null) {
			txtName.setText(movie.getName());
			txtDesc.setText(movie.getDescription());
			txtYear.setText(movie.getReleaseYear().toString());
			txtLength.setText(movie.getLength().toString());
			if (FeatureConfiguration.getInstance().useRatings()) {
				if (currentMovie.containsAttribute(RatingAttribute.NAME) &&
						((RatingAttribute) currentMovie.getAttribute(RatingAttribute.NAME)).getRating() != null)
					txtRating.setText(((RatingAttribute) currentMovie.getAttribute(RatingAttribute.NAME)).getRating().toString());
				else
					txtRating.setText("0");
			}
			setEditable(editable);
		} else {
			txtName.setText("");
			txtDesc.setText("");
			txtYear.setText("");
			txtLength.setText("");
		}
		refreshImage();
	}
	
	public Movie getMovie() {
		return currentMovie;
	}
}
