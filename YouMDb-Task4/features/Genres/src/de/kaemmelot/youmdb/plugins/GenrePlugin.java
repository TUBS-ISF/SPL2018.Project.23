package de.kaemmelot.youmdb.plugins;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.Database;
import de.kaemmelot.youmdb.Plugin;
import de.kaemmelot.youmdb.YouMDb;
import de.kaemmelot.youmdb.gui.DetailMovieComposite;
import de.kaemmelot.youmdb.gui.DetailMovieCompositeAnchor;
import de.kaemmelot.youmdb.gui.DetailMovieCompositePlugin;
import de.kaemmelot.youmdb.gui.GenreEditList;
import de.kaemmelot.youmdb.gui.GenreSelectDialog;
import de.kaemmelot.youmdb.models.Genre;
import de.kaemmelot.youmdb.models.GenreAttribute;

public class GenrePlugin implements Plugin, DetailMovieCompositePlugin {
	private Label lblGenres;
	private Label genreList;
	
	public DetailMovieCompositeAnchor getDetailMovieCompositeAnchor() {
		return DetailMovieCompositeAnchor.Center;
	}

	public int getDetailMovieCompositeWidth() {
		return 2;
	}

	public int getDetailMovieCompositeHeight() {
		return 1;
	}

	public int addDetailMovieCompositeContent(int othersWidth, int othersHeight, Composite parent, final DetailMovieComposite _this) {
		MouseListener genreListener = new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				if (_this.getEditable())
					openGenreDialog(_this);
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		};
		lblGenres = new Label(parent, SWT.NONE);
		lblGenres.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblGenres.setText("Genres:");
		lblGenres.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
		lblGenres.addMouseListener(genreListener);
		
		genreList = new Label(parent, SWT.NONE);
		genreList.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		genreList.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, othersWidth - 1, 1));
		genreList.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
		genreList.addMouseListener(genreListener);
		
		return 1;
	}
	
	private void updateGenres(DetailMovieComposite _this) {
		StringBuilder genres = new StringBuilder();
		boolean firstGenre = true;
		GenreAttribute ga = _this.getMovie().getAttribute(GenreAttribute.class);
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
	
	private void openGenreDialog(DetailMovieComposite _this) {
		GenreSelectDialog dialog = new GenreSelectDialog(_this.getShell());
		GenreAttribute ga = _this.getMovie().getAttribute(GenreAttribute.class);
		Collection<Genre> currentGenres = ga != null ? ga.getGenres() : Collections.<Genre>emptyList();
		if (ga == null)
			ga = _this.getMovie().setAttribute(GenreAttribute.class, new GenreAttribute());
		Collection<Genre> dialogResult = dialog.open(Database.getInstance().getAll(Genre.class), currentGenres);
		if (!dialogResult.equals(currentGenres)) {
			ga.replaceGenres(dialogResult);
			updateGenres(_this);
		}
	}
	
	public void editableChanged(DetailMovieComposite _this) {
		lblGenres.setCursor(SWTResourceManager.getCursor(_this.getEditable() ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
		genreList.setCursor(SWTResourceManager.getCursor(_this.getEditable() ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
	}
	
	public void movieChanged(DetailMovieComposite _this) {
		if (_this.getMovie() != null)
			updateGenres(_this);
		else
			genreList.setText("");
	}
	
	public void registerPlugin(String[] args) {
		Database.registerClass(Genre.class);
		Database.registerClass(GenreAttribute.class);
		DetailMovieComposite.AddPlugin(this);
	}

	public void registerOverviewTabs(final YouMDb window) {	
		final CTabItem genrePage = new CTabItem(window.getTabFolder(), SWT.NONE);
		genrePage.setText("Genres");
		
		final GenreEditList genreEditList = new GenreEditList(window.getTabFolder());
		genrePage.setControl(genreEditList);
		window.getTabFolder().addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				if (window.getTabFolder().getSelection() == genrePage)
					genreEditList.updateGenreList();
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
	}
}
