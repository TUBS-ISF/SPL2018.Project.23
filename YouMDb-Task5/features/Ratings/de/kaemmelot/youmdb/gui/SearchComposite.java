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
import de.kaemmelot.youmdb.models.RatingAttribute;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

public class SearchComposite extends Composite {
	private Combo ratingSelection;

	protected int getNumberOfSearchEntries() {
		return 3 + original();
	}
	
	protected void addSearchMenuEntries() {
		original();
		Label greaterSign = new Label(this, SWT.NONE);
		greaterSign.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		greaterSign.setText("   ≥");
		
		ratingSelection = new Combo(this, SWT.NONE);
		ratingSelection.setItems(new String[] {"", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
		GridData gd_ratingSelection = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_ratingSelection.widthHint = 15;
		ratingSelection.setLayoutData(gd_ratingSelection);
		ratingSelection.select(0);
		ratingSelection.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				showResults();
			}
		});
		
		Label starSign = new Label(this, SWT.NONE);
		starSign.setText("✦");
	}
	
	private List<Movie> search() {
		List<Movie> result = original();
		
		if (ratingSelection.getSelectionIndex() > 0) {
			RatingAttribute attr;
			for (Movie m: result.toArray(new Movie[result.size()])) {
				if ((attr = m.getAttribute(RatingAttribute.class)) == null || attr.getRating() == null || attr.getRating() < ratingSelection.getSelectionIndex())
					result.remove(m);
			}
		}
		
		return result;
	}
}
