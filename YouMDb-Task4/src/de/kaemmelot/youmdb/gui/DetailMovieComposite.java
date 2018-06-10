package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.experimental.swt.SWTUtils;

import de.kaemmelot.youmdb.MovieDatabase;
import de.kaemmelot.youmdb.models.Genre;
import de.kaemmelot.youmdb.models.Movie;

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
	//#if Ratings
	private final Text txtRating;
	//#endif
	//#if Genres
	private final Label lblGenres;
	private final Label genreList;
	//#endif
	//#if Posters
	private final CLabel lblImage;

	private org.eclipse.swt.graphics.Image image;
	//#endif
	
	private Movie currentMovie = null;
	private boolean editable = false;
	
	//#if Posters
	private final org.eclipse.swt.graphics.Image noImage = SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png");
	//#endif

	private void addPlaceholder(Composite parent) {
		(new Label(parent, SWT.NONE)).setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
	}
	
	public DetailMovieComposite(Composite parent, final Shell shell) {
		super(parent, SWT.BORDER);
		setExpandHorizontal(true);
		setExpandVertical(true);
		setLayout(new GridLayout());
		
		Composite content = new Composite(this, SWT.NONE);
		content.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl;
		//#if Posters
		gl = new GridLayout(4, false);
		//#else
//@		gl = new GridLayout(3, false);
		//#endif
		content.setLayout(gl);
		
		final PaintListener editablePaintListener = new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setAntialias(SWT.ON);
				e.gc.setForeground(((Text) e.widget).getForeground());
				if (editable)
					e.gc.drawRoundRectangle(e.x, e.y, e.width - 1, e.height - 1, 6, 6);
			}
		};

		//#if Posters	
		lblImage = new CLabel(content, SWT.BORDER | SWT.SHADOW_OUT);
		final int rows = 3
		//#if Ratings
		+ 1
		//#endif
		;
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
		lblImage.setSize(new Point(YoumdbWindow.IMAGE_WIDTH, YoumdbWindow.IMAGE_HEIGHT));
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
						
						getMovie().setImage(convImg);
						
						refreshImage();
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		});
		//#endif
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
		new TextRedrawListener(txtName);
		
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
		new TextRedrawListener(txtYear);

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
		new TextRedrawListener(txtLength);
		
		//#if Ratings
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
				if (!txtRating.getText().isEmpty())
					currentMovie.setRating(Integer.parseInt(txtRating.getText(), 10));
				else if ((txtRating.getText().isEmpty() || Integer.parseInt(txtRating.getText(), 10) == 0))
					currentMovie.setRating(null);
			}
		});
		// https://stackoverflow.com/q/11522774
		txtRating.addPaintListener(editablePaintListener);
		new TextRedrawListener(txtRating);
		//#endif
		
		//#if Genres
		MouseListener genreListener = new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				if (editable)
					openGenreDialog();
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		};
		lblGenres = new Label(content, SWT.NONE);
		lblGenres.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblGenres.setText("Genres:");
		lblGenres.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
		lblGenres.addMouseListener(genreListener);
		
		genreList = new Label(content, SWT.NONE);
		genreList.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		//#if Posters
		genreList.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1));
		//#else
//@		genreList.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1));
		//#endif
		genreList.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
		genreList.addMouseListener(genreListener);
		//#endif
				
		Label lblDesc = new Label(content, SWT.NONE);
		lblDesc.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblDesc.setText("Description:");
		//#if Posters
		addPlaceholder(content);
		//#endif
		addPlaceholder(content);
		addPlaceholder(content);
		
		txtDesc = new Text(content, SWT.WRAP | SWT.MULTI);
		txtDesc.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		//#if Posters
		txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		//#else
//@		txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		//#endif
		txtDesc.setEditable(editable);
		txtDesc.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				currentMovie.setDescription(txtDesc.getText());
			}
		});
		// https://stackoverflow.com/q/11522774
		txtDesc.addPaintListener(editablePaintListener);
		new TextRedrawListener(txtDesc);

		setContent(content);
	}
	
	//#if Posters
	protected void refreshImage() {
		if (image != null) // release old image
			image.dispose();

		image = null;
		org.eclipse.swt.graphics.Image newImg = noImage;
		if (getMovie() != null && getMovie().getImage() != null) {
			newImg = image = new org.eclipse.swt.graphics.Image(getFont().getDevice(),
					SWTUtils.convertAWTImageToSWT(getMovie().getImage()
					.getScaledInstance(YoumdbWindow.IMAGE_WIDTH, YoumdbWindow.IMAGE_HEIGHT, Image.SCALE_SMOOTH)));
		}
		
		lblImage.setImage(newImg);
	}
	//#endif

	public void setEditable(boolean editable) {
		this.editable  = editable;
		txtName.setEditable(editable);
		txtYear.setEditable(editable);
		txtLength.setEditable(editable);
		//#if Ratings
		txtRating.setEditable(editable);
		//#endif
		txtDesc.setEditable(editable);
		//#if Posters
		lblImage.setCursor(SWTResourceManager.getCursor(editable ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
		//#endif
		//#if Genres
		lblGenres.setCursor(SWTResourceManager.getCursor(editable ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
		genreList.setCursor(SWTResourceManager.getCursor(editable ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
		//#endif
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
			//#if Ratings
			txtRating.setText(currentMovie.getRating() != null ? currentMovie.getRating().toString() : "0");
			//#endif
			//#if Genres
			updateGenres();
			//#endif
			setEditable(editable);
		} else {
			txtName.setText("");
			txtDesc.setText("");
			txtYear.setText("");
			txtLength.setText("");
			//#if Ratings
			txtRating.setText("");
			//#endif
			//#if Genres
			genreList.setText("");
			//#endif
		}
		//#if Posters
		refreshImage();
		//#endif
		layout();
	}
	
	public Movie getMovie() {
		return currentMovie;
	}
	
	//#if Genres
	private void updateGenres() {
		StringBuilder genres = new StringBuilder();
		boolean firstGenre = true;
		for (Genre g : currentMovie.getGenres()) {
			if (!firstGenre)
				genres.append(", ");
			firstGenre = false;
			genres.append(g.getName());
		}
		genreList.setText(genres.toString());
		genreList.getParent().layout();
	}
	
	private void openGenreDialog() {
		GenreSelectDialog dialog = new GenreSelectDialog(getShell());
		Collection<Genre> currentGenres = currentMovie.getGenres();
		Collection<Genre> dialogResult = dialog.open(MovieDatabase.getInstance().getGenres(), currentGenres);
		if (!dialogResult.equals(currentGenres)) {
			currentMovie.replaceGenres(dialogResult);
			updateGenres();
		}
	}
	//#endif
}
