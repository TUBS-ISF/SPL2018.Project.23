package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;

import de.kaemmelot.youmdb.MovieDatabase;
import de.kaemmelot.youmdb.YouMDb;
import de.kaemmelot.youmdb.models.Movie;

public abstract class YoumdbWindow implements Listener {
	protected Shell shell;
	protected ExtendedList movieList;
	private Composite overviewPage;
	private Composite detailMoviePage;
	private DetailMovieComposite detailMovieComposite;
	private Composite pages;
	private StackLayout pagesLayout;
	private Button btnNewMovie;
	private Button btnAbortMovie;
	private Button btnSaveMovie;
	private Button btnBackMovie;
	private Button btnEditMovie;
	private Button btnDeleteMovie;
	private Composite movieListBar;
	private Composite editMovieBar;
	private Composite detailMovieBar;
	private CTabItem movieOverviewPage;
	private CTabFolder overviewTabFolder;
	//#if Genres
	private CTabItem genrePage;
	//#endif

	private static final int WINDOW_WIDTH = 450;
	private static final int WINDOW_HEIGHT = 300;
	public static final int MENU_HEIGHT = 20;
	private static final int TAB_HEIGHT = 18;
	public static final Point CONTENT_MIN_SIZE = new Point(WINDOW_WIDTH - 20, WINDOW_HEIGHT - MENU_HEIGHT - TAB_HEIGHT);
	//#if Posters
	public static final int IMAGE_HEIGHT = 90;
	public static final int IMAGE_WIDTH = 62;
	//#endif

	/**
	 * @deprecated THIS IS JUST FOR THE DRAFT. Shouldn't be used except by
	 *             WindowBuilder.
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
		refreshOverview(true);
		shell.open();
		shell.layout();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setMinimumSize(new Point(WINDOW_WIDTH, WINDOW_HEIGHT));
		shell.setText("YouMDb");
		GridLayout gl_shell = new GridLayout();
		gl_shell.marginWidth = 0;
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);

		pages = new Composite(shell, SWT.NONE);
		pages.setLayoutData(new GridData(GridData.FILL_BOTH));
		pagesLayout = new StackLayout();
		pages.setLayout(pagesLayout);

		// overview page
		overviewPage = new Composite(pages, SWT.NONE);
		GridLayout gl_overviewPage = new GridLayout(1, false);
		gl_overviewPage.marginHeight = 0;
		gl_overviewPage.marginWidth = 0;
		gl_overviewPage.verticalSpacing = 0;
		overviewPage.setLayout(gl_overviewPage);
		
		overviewTabFolder = new CTabFolder(overviewPage, SWT.BORDER | SWT.FLAT);
		overviewTabFolder.setTabHeight(TAB_HEIGHT); // this should be the default, but now we can be sure
		overviewTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		overviewTabFolder.setSelectionBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT));
		
		movieOverviewPage = new CTabItem(overviewTabFolder, SWT.NONE);
		movieOverviewPage.setText("Movies");
		
		Composite movieOverviewComposite = new Composite(overviewTabFolder, SWT.NONE);
		GridLayout gl_movieOverviewComposite = new GridLayout(1, false);
		gl_movieOverviewComposite.verticalSpacing = 0;
		gl_movieOverviewComposite.marginHeight = 0;
		gl_movieOverviewComposite.marginWidth = 0;
		movieOverviewComposite.setLayout(gl_movieOverviewComposite);
		movieOverviewPage.setControl(movieOverviewComposite);
		
		movieList = new ExtendedList(movieOverviewComposite);
		movieList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		movieList.setMinSize(CONTENT_MIN_SIZE);

		movieListBar = new Composite(movieOverviewComposite, SWT.NONE);
		movieListBar.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gdMovieListBar = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
		gdMovieListBar.heightHint = MENU_HEIGHT;
		movieListBar.setLayoutData(gdMovieListBar);

		btnNewMovie = new Button(movieListBar, SWT.NONE);
		btnNewMovie.setText("New Movie");
		btnNewMovie.addListener(SWT.MouseDown, this);
		
		//#if Genres		
		genrePage = new CTabItem(overviewTabFolder, SWT.NONE);
		genrePage.setText("Genres");
		
		final GenreEditList genreEditList = new GenreEditList(overviewTabFolder);
		genrePage.setControl(genreEditList);
		overviewTabFolder.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				if (overviewTabFolder.getSelection() == genrePage)
					genreEditList.updateGenreList();
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		//#else
//@		overviewTabFolder.setSingle(true);
		//#endif

		// detail movie page
		detailMoviePage = new Composite(pages, SWT.NONE);
		GridLayout gl_editMoviePage = new GridLayout(1, false);
		gl_editMoviePage.marginHeight = 0;
		gl_editMoviePage.marginWidth = 0;
		gl_editMoviePage.verticalSpacing = 0;
		detailMoviePage.setLayout(gl_editMoviePage);
		detailMovieComposite = new DetailMovieComposite(detailMoviePage, shell);
		detailMovieComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		detailMovieComposite.setMinSize(CONTENT_MIN_SIZE);

		// as edit
		editMovieBar = new Composite(detailMoviePage, SWT.NONE);
		GridData gdEditBar = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
		gdEditBar.heightHint = MENU_HEIGHT;
		editMovieBar.setLayoutData(gdEditBar);
		editMovieBar.setLayout(new FillLayout(SWT.HORIZONTAL));

		btnAbortMovie = new Button(editMovieBar, SWT.NONE);
		btnAbortMovie.setText("Abort");
		btnAbortMovie.addListener(SWT.MouseDown, this);

		btnSaveMovie = new Button(editMovieBar, SWT.NONE);
		btnSaveMovie.setText("Save");
		btnSaveMovie.addListener(SWT.MouseDown, this);

		// as detail
		detailMovieBar = new Composite(detailMoviePage, SWT.NONE);
		GridData gdDetailBar = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
		gdDetailBar.heightHint = MENU_HEIGHT;
		detailMovieBar.setLayoutData(gdDetailBar);
		detailMovieBar.setLayout(new FillLayout(SWT.HORIZONTAL));

		btnBackMovie = new Button(detailMovieBar, SWT.NONE);
		btnBackMovie.setText("Back");
		btnBackMovie.addListener(SWT.MouseDown, this);

		btnEditMovie = new Button(detailMovieBar, SWT.NONE);
		btnEditMovie.setText("Edit");
		btnEditMovie.addListener(SWT.MouseDown, this);

		btnDeleteMovie = new Button(detailMovieBar, SWT.NONE);
		btnDeleteMovie.setText("Delete");
		btnDeleteMovie.addListener(SWT.MouseDown, this);

		pagesLayout.topControl = overviewPage;
	}

	private void refreshOverview(boolean clean) {
		if (clean) {
			movieList.clearItems();
			for (Movie m : MovieDatabase.getInstance().getMovies())
				new ExtendedListItem(movieList, m)
					.addListener(SWT.MouseDown, this); // add movie to list and register onClick listener
		} else {
			for (Control c : movieList.getContentComposite().getChildren()) {
				if (c instanceof ExtendedListItem)
					((ExtendedListItem) c).refresh();
			}
		}
		movieList.refresh();
	}

	private void selectToolbar(Composite toolbar) {
		if (toolbar == editMovieBar) {
			detailMovieBar.setVisible(false);
			((GridData) detailMovieBar.getLayoutData()).exclude = true;
			editMovieBar.setVisible(true);
			((GridData) editMovieBar.getLayoutData()).exclude = false;
			detailMovieBar.getParent().layout();
		} else if (toolbar == detailMovieBar) {
			detailMovieBar.setVisible(true);
			((GridData) detailMovieBar.getLayoutData()).exclude = false;
			editMovieBar.setVisible(false);
			((GridData) editMovieBar.getLayoutData()).exclude = true;
			editMovieBar.getParent().layout();
		}
	}
	
	public void handleEvent(Event event) {
		// on click events
		if (event.widget instanceof ExtendedListItem) {
			detailMovieComposite.setMovie(((ExtendedListItem) event.widget).getMovie(), false);
			selectToolbar(detailMovieBar);
			pagesLayout.topControl = detailMoviePage;
			pages.layout();
		} else if (event.widget == btnNewMovie) {
			detailMovieComposite.setMovie(new Movie("", "", 0, 0), true);
			selectToolbar(editMovieBar);
			pagesLayout.topControl = detailMoviePage;
			pages.layout();
		} else if (event.widget == btnAbortMovie) {
			Movie movie = detailMovieComposite.getMovie();
			if (movie.getId() != null) { // existing movie
				MovieDatabase.getInstance().abortTransaction().refreshMovie(movie);
				detailMovieComposite.setMovie(movie, false);
				selectToolbar(detailMovieBar);
			} else {
				pagesLayout.topControl = overviewPage;
				detailMovieComposite.setMovie(null);
			}
			pages.layout();
		} else if (event.widget == btnSaveMovie) {
			MovieDatabase md = MovieDatabase.getInstance();
			Movie movie = detailMovieComposite.getMovie();
			boolean isNew = movie.getId() == null;
			if (isNew)
				md.startTransaction() // start + add didn't happen before, cause it's new
					.addMovie(movie);
			md.endTransaction()
				.refreshMovie(movie);
			detailMovieComposite.setMovie(movie, false); // refresh everything, so we see the current state
			selectToolbar(detailMovieBar);
			refreshOverview(isNew);
		} else if (event.widget == btnBackMovie) {
			pagesLayout.topControl = overviewPage;
			pages.layout();
		} else if (event.widget == btnEditMovie) {
			MovieDatabase.getInstance().startTransaction(); // track changes
			detailMovieComposite.setEditable(true);
			selectToolbar(editMovieBar);
			pages.layout();
		} else if (event.widget == btnDeleteMovie) {
			// TODO popup
			// TODO delete movie
		} else
			throw new UnsupportedOperationException("Unimplemented event for widget: " + event.widget.toString());
	}
}
