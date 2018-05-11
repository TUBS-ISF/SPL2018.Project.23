package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ToolItem;

import de.kaemmelot.youmdb.YouMDb;

public abstract class YoumdbWindow implements Listener {	
	protected Shell shell;
	protected ExtendedList movieList;
	private Composite overviewPage;
	private Composite editMoviePage;
	private Composite detailMoviePage;
	private StackLayout pagesLayout;
	private ToolItem tltmNewMovie;
	private ToolItem tltmAbortMovie;
	private ToolItem tltmSaveMovie;
	private ToolItem tltmBackMovie;
	private ToolItem tltmEditMovie;
	private ToolItem tltmDeleteMovie;

	/**
	 * @deprecated THIS IS JUST FOR THE DRAFT. Shouldn't be used except by WindowBuilder.
	 */
	public static void main(String[] args) {
		try {
			YoumdbWindow window = new YouMDb();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		createContents();
		shell.open();
		shell.layout();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setMinimumSize(new Point(450, 300));
		shell.setText("YouMDb");
		GridLayout gl_shell = new GridLayout();
		gl_shell.marginWidth = 0;
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);
		
		Composite pages = new Composite(shell, SWT.NONE);
		pages.setLayoutData(new GridData(GridData.FILL_BOTH));
		StackLayout pagesLayout = new StackLayout();
		pages.setLayout(pagesLayout);
		
		// overview page
		overviewPage = new Composite(pages, SWT.NONE);
		GridLayout gl_overviewPage = new GridLayout(1, false);
		gl_overviewPage.marginHeight = 0;
		gl_overviewPage.marginWidth = 0;
		overviewPage.setLayout(gl_overviewPage);
		
		movieList = new ExtendedList(overviewPage);
		movieList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		movieList.setMinSize(new Point(450, 280));
		
		ToolBar movieListToolBar = new ToolBar(overviewPage, SWT.FLAT | SWT.RIGHT);
		movieListToolBar.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false));
		movieListToolBar.setSize(new Point(90, 20));
		
		tltmNewMovie = new ToolItem(movieListToolBar, SWT.NONE);
		tltmNewMovie.setText("New Movie");
		tltmNewMovie.addListener(SWT.MouseDown, this);
		
		// edit movie page
		editMoviePage = new Composite(pages, SWT.NONE);
		GridLayout gl_editMoviePage = new GridLayout(1, false);
		gl_editMoviePage.marginHeight = 0;
		gl_editMoviePage.marginWidth = 0;
		editMoviePage.setLayout(gl_editMoviePage);
		
		ToolBar editMovieToolBar = new ToolBar(editMoviePage, SWT.FLAT | SWT.RIGHT);
		editMovieToolBar.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		editMovieToolBar.setSize(new Point(90, 20));
		
		tltmAbortMovie = new ToolItem(editMovieToolBar, SWT.NONE);
		tltmAbortMovie.setText("Abort");
		tltmAbortMovie.addListener(SWT.MouseDown, this);
		
		tltmSaveMovie = new ToolItem(editMovieToolBar, SWT.NONE);
		tltmSaveMovie.setText("Save");
		tltmSaveMovie.addListener(SWT.MouseDown, this);
		
		// detail movie page
		detailMoviePage = new Composite(pages, SWT.NONE);
		GridLayout gl_detailMoviePage = new GridLayout(1, false);
		gl_detailMoviePage.marginHeight = 0;
		gl_detailMoviePage.marginWidth = 0;
		detailMoviePage.setLayout(gl_detailMoviePage);
		
		ToolBar detailMovieToolBar = new ToolBar(detailMoviePage, SWT.FLAT | SWT.RIGHT);
		detailMovieToolBar.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		detailMovieToolBar.setSize(new Point(90, 20));
		
		tltmBackMovie = new ToolItem(detailMovieToolBar, SWT.NONE);
		tltmBackMovie.setText("Back");
		tltmBackMovie.addListener(SWT.MouseDown, this);
		
		tltmEditMovie = new ToolItem(detailMovieToolBar, SWT.NONE);
		tltmEditMovie.setText("Edit");
		tltmEditMovie.addListener(SWT.MouseDown, this);
		
		tltmDeleteMovie = new ToolItem(detailMovieToolBar, SWT.NONE);
		tltmDeleteMovie.setText("Delete");
		tltmDeleteMovie.addListener(SWT.MouseDown, this);
		
		pagesLayout.topControl = overviewPage;
	}

	public void handleEvent(Event event) {
		if (event.widget == tltmNewMovie) {
			pagesLayout.topControl = editMoviePage;
			// TODO reset interface
			// TODO create new movie from user input
		} else if (false) { // TODO movie on click
			pagesLayout.topControl = detailMoviePage;
			// TODO reset interface
		} else if (event.widget == tltmAbortMovie) {
			// TODO back to edit or overview
		} else if (event.widget == tltmSaveMovie) {
			pagesLayout.topControl = detailMoviePage;
			// TODO save/create movie
			// TODO reset interface
		} else if (event.widget == tltmBackMovie) {
			pagesLayout.topControl = overviewPage;
		} else if (event.widget == tltmEditMovie) {
			pagesLayout.topControl = editMoviePage;
			// TODO reset interface
		} else if (event.widget == tltmDeleteMovie) {
			// TODO popup
			// TODO delete movie
		} else
			throw new UnsupportedOperationException("Unimplemented event for widget: " + event.widget.toString());
	}
}
