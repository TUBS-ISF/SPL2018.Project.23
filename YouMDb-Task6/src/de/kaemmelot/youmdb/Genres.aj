package de.kaemmelot.youmdb;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.gui.DetailMovieComposite;
import de.kaemmelot.youmdb.gui.GenreEditList;
import de.kaemmelot.youmdb.gui.GenreSelectDialog;
import de.kaemmelot.youmdb.gui.SearchComposite;
import de.kaemmelot.youmdb.gui.YoumdbWindow;
import de.kaemmelot.youmdb.models.Genre;
import de.kaemmelot.youmdb.models.GenreAttribute;
import de.kaemmelot.youmdb.models.Movie;

public aspect Genres {
	// Database
	List<Class<?>> around(): execution(static List<Class<?>> Database.getClasses()) {
		List<Class<?>> result = proceed();
		result.add(Genre.class);
		result.add(GenreAttribute.class);
		return result;
	}
	
	// YoumdbWindow
	void around(final YoumdbWindow that): execution(void YoumdbWindow.addOverviewTabs()) && this(that) {
		final CTabItem genrePage = new CTabItem(that.getTabFolder(), SWT.NONE);
		genrePage.setText("Genres");
		
		final GenreEditList genreEditList = new GenreEditList(that.getTabFolder());
		genrePage.setControl(genreEditList);
		that.getTabFolder().addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				if (that.getTabFolder().getSelection() == genrePage)
					genreEditList.updateGenreList();
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		proceed(that);
	}
	
	// SearchComposite
	private Combo SearchComposite.genreSelection = null;
	
	int around(): execution(int SearchComposite.getNumberOfSearchEntries()) {
		return 1 + proceed();
	}
	
	void around(final SearchComposite that): execution(void SearchComposite.addSearchMenuEntries()) && this(that) {
		proceed(that);
		that.genreSelection = new Combo(that, SWT.NONE);
		that.genreSelection.setItems(new String[] {""});
		GridData gd_genreSelection = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_genreSelection.widthHint = 100;
		that.genreSelection.setLayoutData(gd_genreSelection);
		that.genreSelection.select(0);
		that.genreSelection.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				that.showResults();
			}
		});
	}
	
	void around(SearchComposite that): execution(void SearchComposite.updateGenres()) && this(that) {
		// overwriting the empty method
		List<Genre> genres = Database.getInstance().getAll(Genre.class);
		String oldSelection = that.genreSelection.getItem(that.genreSelection.getSelectionIndex());
		String[] genreStrings = new String[genres.size() + 1];
		int s = 0;
		int select = 0;
		genreStrings[s++] = "";
		for (Genre g: genres) {
			if (g.getName().equals(oldSelection))
				select = s;
			genreStrings[s++] = g.getName();
		}
		that.genreSelection.setItems(genreStrings);
		that.genreSelection.select(select);
	}
	
	List<Movie> around(SearchComposite that): execution(List<Movie> SearchComposite.search()) && this(that) {
		List<Movie> result = proceed(that);
		
		if (that.genreSelection.getSelectionIndex() > 0) {
			Genre selectedGenre = Database.getInstance().getByName(Genre.class, that.genreSelection.getItem(that.genreSelection.getSelectionIndex()));
			GenreAttribute attr;
			for (Movie m: result.toArray(new Movie[result.size()])) {
				if ((attr = m.getAttribute(GenreAttribute.class)) == null || !attr.getGenres().contains(selectedGenre))
					result.remove(m);
			}
		}
		
		return result;
	}
	
	// DetailMovieComposite
	private Label DetailMovieComposite.lblGenres = null;
	private Label DetailMovieComposite.genreList = null;
	
	int around(): execution(int getCenterWidth()) {
		return 2 + proceed();
	}
	
	int around(): execution(int getCenterHeight()) {
		return 1 + proceed();
	}
	
	private boolean DetailMovieComposite.next_genres = false;
	int around(final DetailMovieComposite that, Composite content, int width, int maxHeight):
		execution(int addNextCenter(Composite, int, int)) && this(that) && args(content, width, maxHeight) {
		final int orig = proceed(that, content, width, maxHeight);
		if (orig == 0 && !that.next_genres) {
			MouseListener genreListener = new MouseListener() {
				public void mouseUp(MouseEvent e) {
				}
				public void mouseDown(MouseEvent e) {
					if (that.getEditable())
						that.openGenreDialog();
				}
				public void mouseDoubleClick(MouseEvent e) {
				}
			};
			that.lblGenres = new Label(content, SWT.NONE);
			that.lblGenres.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			that.lblGenres.setText("Genres:");
			that.lblGenres.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
			that.lblGenres.addMouseListener(genreListener);
			
			that.genreList = new Label(content, SWT.NONE);
			that.genreList.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			that.genreList.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, width - 1, 1));
			that.genreList.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
			that.genreList.addMouseListener(genreListener);
			
			that.next_genres = true;
			return 1;
		} else
			return orig;
	}

	private void DetailMovieComposite.updateGenres(Movie movie) {
		StringBuilder genres = new StringBuilder();
		boolean firstGenre = true;
		GenreAttribute ga = movie.getAttribute(GenreAttribute.class);
		if (ga != null) {
			for (Genre g : ga.getGenres()) {
				if (!firstGenre)
					genres.append(", ");
				firstGenre = false;
				genres.append(g.getName());
			}
		}
		genreList.setText(genres.toString());
		genreList.getParent().layout();
	}
	
	private void DetailMovieComposite.openGenreDialog() {
		GenreSelectDialog dialog = new GenreSelectDialog(getShell());
		GenreAttribute ga = getMovie().getAttribute(GenreAttribute.class);
		Collection<Genre> currentGenres = ga != null ? ga.getGenres() : Collections.<Genre>emptyList();
		if (ga == null)
			ga = getMovie().setAttribute(GenreAttribute.class, new GenreAttribute());
		Collection<Genre> dialogResult = dialog.open(Database.getInstance().getAll(Genre.class), currentGenres);
		if (!dialogResult.equals(currentGenres)) {
			ga.replaceGenres(dialogResult);
			updateGenres(getMovie());
		}
	}
	
	void around(DetailMovieComposite that, boolean editable): execution(void setEditable(boolean)) && this(that) && args(editable) {
		that.lblGenres.setCursor(SWTResourceManager.getCursor(editable ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
		that.genreList.setCursor(SWTResourceManager.getCursor(editable ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
		proceed(that, editable);
	}
	
	void around(DetailMovieComposite that, Movie movie, boolean editable): execution(void setMovie(Movie, boolean)) && this(that) && args(movie, editable) {
		if (movie != null)
			that.updateGenres(movie);
		else
			that.genreList.setText("");
		proceed(that, movie, editable);
	}
}
