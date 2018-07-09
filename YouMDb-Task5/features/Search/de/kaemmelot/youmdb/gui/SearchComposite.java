package de.kaemmelot.youmdb.gui;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.Database;
import de.kaemmelot.youmdb.models.Genre;
import de.kaemmelot.youmdb.models.GenreAttribute;
import de.kaemmelot.youmdb.models.Movie;

import org.eclipse.swt.widgets.Text;

public class SearchComposite extends Composite {
	private Text textSearch;
	private ExtendedList resultCollection;
	private YoumdbWindow window;

	protected int getNumberOfSearchEntries() {
		return 2;
	}
	
	protected void addSearchMenuEntries() {
		textSearch = new Text(this, SWT.BORDER);
		GridData gd_textSearch = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_textSearch.widthHint = 150;
		textSearch.setLayoutData(gd_textSearch);
		textSearch.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				showResults();
			}
		});
	}
	
	SearchComposite(Composite parent, YoumdbWindow window) {
		super(parent, SWT.NONE);
		this.window = window;
		
		GridLayout searchLayout = new GridLayout(getNumberOfSearchEntries(), false);
		searchLayout.verticalSpacing = 2;
		searchLayout.horizontalSpacing = 2;
		searchLayout.marginWidth = 0;
		searchLayout.marginHeight = 0;
		setLayout(searchLayout);
		
		addSearchMenuEntries();
		
		// placeholder
		new Label(this, SWT.NONE);
		
		resultCollection = new ExtendedList(this);
		resultCollection.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		resultCollection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, getNumberOfSearchEntries(), 1));
		resultCollection.setExpandHorizontal(true);
		resultCollection.setExpandVertical(true);
	}

	protected void showResults() {
		resultCollection.clearItems();
		List<Movie> movies = search();
		for (Movie m : movies)
			new ExtendedListItem(resultCollection, m)
				.addListener(SWT.MouseDown, window); // add movie to list and register onClick listener
		resultCollection.refresh();
	}
	
	private List<Movie> search() {
		List<Movie> result = Database.getInstance().getAll(Movie.class);
		if (textSearch.getText().length() > 0) {
			for (Movie m: result.toArray(new Movie[result.size()])) {
				if (!m.getName().contains(textSearch.getText()) && !m.getDescription().contains(textSearch.getText()))
					result.remove(m);
			}
		}
		return result;
	}
}
