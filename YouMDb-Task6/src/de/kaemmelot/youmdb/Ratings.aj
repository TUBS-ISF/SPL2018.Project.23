package de.kaemmelot.youmdb;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.gui.DetailMovieComposite;
import de.kaemmelot.youmdb.gui.SearchComposite;
import de.kaemmelot.youmdb.gui.TextRedrawListener;
import de.kaemmelot.youmdb.models.Movie;
import de.kaemmelot.youmdb.models.RatingAttribute;

public privileged aspect Ratings {
	// Database
	List<Class<?>> around(): execution(static List<Class<?>> Database.getClasses()) {
		List<Class<?>> result = proceed();
		result.add(RatingAttribute.class);
		return result;
	}

	// SearchComposite
	private Combo SearchComposite.ratingSelection;

	int around(): execution(int SearchComposite.getNumberOfSearchEntries()) {
		return 3 + proceed();
	}
	
	after(final SearchComposite that): execution(void SearchComposite.addSearchMenuEntries()) && this(that) {
		Label greaterSign = new Label(that, SWT.NONE);
		greaterSign.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		greaterSign.setText("   ≥");
		
		that.ratingSelection = new Combo(that, SWT.NONE);
		that.ratingSelection.setItems(new String[] {"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
		GridData gd_ratingSelection = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_ratingSelection.widthHint = 15;
		that.ratingSelection.setLayoutData(gd_ratingSelection);
		that.ratingSelection.select(0);
		that.ratingSelection.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				that.showResults();
			}
		});
		
		Label starSign = new Label(that, SWT.NONE);
		starSign.setText("✦");
	}
	
	List<Movie> around(SearchComposite that): execution(List<Movie> SearchComposite.search()) && this(that) {
		List<Movie> result = proceed(that);
		
		if (that.ratingSelection.getSelectionIndex() > 0) {
			RatingAttribute attr;
			for (Movie m: result.toArray(new Movie[result.size()])) {
				if ((attr = m.getAttribute(RatingAttribute.class)) == null || attr.getRating() == null || attr.getRating() < that.ratingSelection.getSelectionIndex())
					result.remove(m);
			}
		}
		
		return result;
	}
	
	// DetailMovieComposite
	private Text DetailMovieComposite.txtRating;
	
	int around(): execution(int DetailMovieComposite.getCenterWidth()) {
		return 2 + proceed();
	}
	
	int around(): execution(int DetailMovieComposite.getCenterHeight()) {
		return 1 + proceed();
	}
	
	private boolean DetailMovieComposite.next_ratings = false;
	int around(final DetailMovieComposite that, Composite content, int width, int maxHeight):
		execution(int DetailMovieComposite.addNextCenter(Composite, int, int)) && this(that) && args(content, width, maxHeight) {
		final int orig = proceed(that, content, width, maxHeight);
		if (orig == 0 && !that.next_ratings) {
			for (int i = 0; i < width - 2; i++)
				that.addPlaceholder(content);
			
			Label lblRating = new Label(content, SWT.NONE);
			lblRating.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			lblRating.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
			lblRating.setText("Rating (out of 10):");
			
			that.txtRating = new Text(content, SWT.NONE);
			that.txtRating.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			GridData gd_txtRating = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
			gd_txtRating.widthHint = 25;
			that.txtRating.setLayoutData(gd_txtRating);
			that.txtRating.setEditable(that.editable);
			that.txtRating.addVerifyListener(new VerifyListener() {
				public void verifyText(VerifyEvent e) {
		            String newS = that.txtRating.getText().substring(0, e.start) + e.text + that.txtRating.getText().substring(e.end);
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
			that.txtRating.addListener(SWT.FocusOut, new Listener() {
				public void handleEvent(Event event) {
					RatingAttribute ra = that.getMovie().getAttribute(RatingAttribute.class);
					if (ra == null)
						ra = that.getMovie().setAttribute(RatingAttribute.class, new RatingAttribute());
					if (!that.txtRating.getText().isEmpty())
						ra.setRating(Integer.parseInt(that.txtRating.getText(), 10));
					else if ((that.txtRating.getText().isEmpty() || Integer.parseInt(that.txtRating.getText(), 10) == 0))
						ra.setRating(null);
				}
			});
			// https://stackoverflow.com/q/11522774
			that.txtRating.addPaintListener(that.getEditablePaintListener());
			new TextRedrawListener(that.txtRating);
			
			that.next_ratings = true;
			return 1;
		} else
			return orig;
	}

	before(DetailMovieComposite that, boolean editable): execution(void DetailMovieComposite.setEditable(boolean)) && this(that) && args(editable) {
		that.txtRating.setEditable(editable);
	}
	
	before(DetailMovieComposite that, Movie movie, boolean editable): execution(void DetailMovieComposite.setMovie(Movie, boolean)) && this(that) && args(movie, editable) {
		if (movie != null) {
			RatingAttribute ra = movie.getAttribute(RatingAttribute.class);
			if (ra == null)
				ra = movie.setAttribute(RatingAttribute.class, new RatingAttribute());
			that.txtRating.setText(ra.getRating() != null ? ra.getRating().toString() : "0");
		} else
			that.txtRating.setText("");
	}
}
