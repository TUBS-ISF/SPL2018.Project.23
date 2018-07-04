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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.experimental.swt.SWTUtils;

import de.kaemmelot.youmdb.Database;
import de.kaemmelot.youmdb.models.Genre;
import de.kaemmelot.youmdb.models.GenreAttribute;
import de.kaemmelot.youmdb.models.Movie;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DetailMovieComposite {
	private Label lblGenres;
	private Label genreList;
	
	private int getCenterWidth() {
		return 2 + original();
	}
	
	private int getCenterHeight() {
		return 1 + original();
	}
	
	private boolean next_genres = false;
	private int addNextCenter(Composite content, int width, int maxHeight) {
		final int orig = original(content, width, maxHeight);
		if (orig == 0 && !next_genres) {
			MouseListener genreListener = new MouseListener() {
				public void mouseUp(MouseEvent e) {
				}
				public void mouseDown(MouseEvent e) {
					if (getEditable())
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
			genreList.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, width - 1, 1));
			genreList.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
			genreList.addMouseListener(genreListener);
			
			next_genres = true;
			return 1;
		} else
			return orig;
	}

	private void updateGenres(Movie movie) {
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
	
	private void openGenreDialog() {
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
	
	public void setEditable(boolean editable) {
		lblGenres.setCursor(SWTResourceManager.getCursor(editable ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
		genreList.setCursor(SWTResourceManager.getCursor(editable ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
		original(editable);
	}
	
	public void setMovie(Movie movie, boolean editable) {
		if (movie != null)
			updateGenres(movie);
		else
			genreList.setText("");
		original(movie, editable);
	}
}
