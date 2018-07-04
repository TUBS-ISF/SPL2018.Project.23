package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Listener;

public abstract class YoumdbWindow implements Listener {
	private void addOverviewTabs() {
		final CTabItem genrePage = new CTabItem(getTabFolder(), SWT.NONE);
		genrePage.setText("Genres");
		
		final GenreEditList genreEditList = new GenreEditList(getTabFolder());
		genrePage.setControl(genreEditList);
		getTabFolder().addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				if (getTabFolder().getSelection() == genrePage)
					genreEditList.updateGenreList();
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		original();
	}
}
