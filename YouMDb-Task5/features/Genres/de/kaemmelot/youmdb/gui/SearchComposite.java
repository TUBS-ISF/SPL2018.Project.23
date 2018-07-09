package de.kaemmelot.youmdb.gui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.Database;
import de.kaemmelot.youmdb.models.Genre;
import de.kaemmelot.youmdb.models.GenreAttribute;
import de.kaemmelot.youmdb.models.Movie;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

public class SearchComposite extends Composite {
	private Combo genreSelection;
	private List<Genre> genres;
	
	protected int getNumberOfSearchEntries() {
		return 1 + original();
	}
	
	protected void addSearchMenuEntries() {
		original();
		genreSelection = new Combo(this, SWT.NONE);
		genreSelection.setItems(new String[] {""});
		GridData gd_genreSelection = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_genreSelection.widthHint = 100;
		genreSelection.setLayoutData(gd_genreSelection);
		genreSelection.select(0);
		genreSelection.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				showResults();
			}
		});
	}
	
	public void updateGenres(List<Genre> genres) {
		String oldSelection = genreSelection.getItem(genreSelection.getSelectionIndex());
		this.genres = genres;
		String[] genreStrings = new String[genres.size() + 1];
		int s = 0;
		int select = 0;
		genreStrings[s++] = "";
		for (Genre g: genres) {
			if (g.getName().equals(oldSelection))
				select = s;
			genreStrings[s++] = g.getName();
		}
		genreSelection.setItems(genreStrings);
		genreSelection.select(s);
	}
	
	private List<Movie> search() {
		List<Movie> result = original();
		
		if (genreSelection.getSelectionIndex() > 0) {
			Genre selectedGenre = Database.getInstance().getByName(Genre.class, genreSelection.getItem(genreSelection.getSelectionIndex()));
			GenreAttribute attr;
			for (Movie m: result.toArray(new Movie[result.size()])) {
				if ((attr = m.getAttribute(GenreAttribute.class)) == null || !attr.getGenres().contains(selectedGenre))
					result.remove(m);
			}
		}
		
		return result;
	}
}
