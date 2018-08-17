package de.kaemmelot.youmdb;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.gui.DetailMovieComposite;
import de.kaemmelot.youmdb.models.Movie;
import de.kaemmelot.youmdb.models.NoteAttribute;

public privileged aspect Notes {
	// Database
	List<Class<?>> around(): execution(static List<Class<?>> Database.getClasses()) {
		List<Class<?>> result = proceed();
		result.add(NoteAttribute.class);
		return result;
	}

	// DetailMovieComposite
	private CTabItem DetailMovieComposite.notePage = null;
	private Text DetailMovieComposite.noteText = null;
	
	void around(final DetailMovieComposite that): execution(void DetailMovieComposite.addMovieTabs()) && this(that) {
		that.notePage = new CTabItem(that.pages, SWT.NONE);
		that.notePage.setText("Notes");
		
		final Composite content = new Composite(that.pages, SWT.NONE);
		content.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		content.setLayout(layout);
		
		that.notePage.setControl(content);
		
		that.noteText = new Text(content, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		that.noteText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		that.noteText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (that.getMovie() == null)
					return;
				Database db = Database.getInstance();
				boolean ownTransaction = !db.isWithinTransaction(); // can be used independent of movie editing
				if (ownTransaction)
					db.startTransaction();
				NoteAttribute attr = that.getMovie().getAttribute(NoteAttribute.class);
				if (attr == null) {
					that.getMovie().setAttribute(NoteAttribute.class, attr = (new NoteAttribute()));
					db.add(attr);
				}
				attr.setNote(that.noteText.getText());
				if (ownTransaction)
					db.endTransaction();
			}
		});
		
		proceed(that);
	}
	
	before(DetailMovieComposite that, Movie movie, boolean editable): execution(void DetailMovieComposite.setMovie(Movie, boolean)) && this(that) && args(movie, editable) {
		NoteAttribute attr = movie.getAttribute(NoteAttribute.class);
		that.noteText.setText(attr != null ? attr.getNote() : "");
	}
}
