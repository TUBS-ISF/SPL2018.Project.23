package de.kaemmelot.youmdb;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;

import de.kaemmelot.youmdb.gui.SearchComposite;
import de.kaemmelot.youmdb.gui.YoumdbWindow;

public aspect Search {
	void around(final YoumdbWindow that): execution(void YoumdbWindow.addOverviewTabs()) && this(that) {
		final CTabItem searchPage = new CTabItem(that.getTabFolder(), SWT.NONE);
		searchPage.setText("Search");
		
		final SearchComposite searchComposite = new SearchComposite(that.getTabFolder(), that);
		searchPage.setControl(searchComposite);
		that.getTabFolder().addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				if (that.getTabFolder().getSelection() == searchPage)
					searchComposite.updateGenres(); // TODO should this be moved?
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		proceed(that);
	}
}
