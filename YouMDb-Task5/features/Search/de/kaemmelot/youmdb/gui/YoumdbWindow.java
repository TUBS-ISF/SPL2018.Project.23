package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Listener;

import de.kaemmelot.youmdb.Database;
import de.kaemmelot.youmdb.models.Genre;

public abstract class YoumdbWindow implements Listener {
	private void addOverviewTabs() {
		final CTabItem searchPage = new CTabItem(getTabFolder(), SWT.NONE);
		searchPage.setText("Search");
		
		final SearchComposite searchComposite = new SearchComposite(getTabFolder(), this);
		searchPage.setControl(searchComposite);
		getTabFolder().addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				if (getTabFolder().getSelection() == searchPage)
					searchComposite.updateGenres(Database.getInstance().getAll(Genre.class));
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		original();
	}
}
