package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;

import de.kaemmelot.youmdb.FeatureConfiguration;
import de.kaemmelot.youmdb.MovieDatabase;
import de.kaemmelot.youmdb.YouMDb;
import de.kaemmelot.youmdb.models.ImageAttribute;
import de.kaemmelot.youmdb.models.Movie;
import de.kaemmelot.youmdb.models.RatingAttribute;

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
	private Composite editMovieBar;
	private Composite detailMovieBar;
	
	private static final Point CONTENT_MIN_SIZE = new Point(450, 280);

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
		shell.setMinimumSize(new Point(450, 300));
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
		overviewPage.setLayout(gl_overviewPage);

		movieList = new ExtendedList(overviewPage);
		movieList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		movieList.setMinSize(CONTENT_MIN_SIZE);

		Composite movieListBar = new Composite(overviewPage, SWT.NONE);
		movieListBar.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1));
		movieListBar.setLayout(new FillLayout(SWT.HORIZONTAL));

		btnNewMovie = new Button(movieListBar, SWT.NONE);
		btnNewMovie.setText("New Movie");
		btnNewMovie.addListener(SWT.MouseDown, this);

		// detail movie page
		detailMoviePage = new Composite(pages, SWT.NONE);
		GridLayout gl_editMoviePage = new GridLayout(1, false);
		gl_editMoviePage.marginHeight = 0;
		gl_editMoviePage.marginWidth = 0;
		detailMoviePage.setLayout(gl_editMoviePage);
		detailMovieComposite = new DetailMovieComposite(detailMoviePage, shell);
		detailMovieComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		detailMovieComposite.setMinSize(CONTENT_MIN_SIZE);

		// as edit
		editMovieBar = new Composite(detailMoviePage, SWT.NONE);
		GridData gdEditBar = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
		gdEditBar.heightHint = 25;
		editMovieBar.setLayoutData(gdEditBar);
		editMovieBar.setLayout(new FillLayout(SWT.HORIZONTAL));

		btnAbortMovie = new Button(editMovieBar, SWT.BORDER);
		btnAbortMovie.setText("Abort");
		btnAbortMovie.addListener(SWT.MouseDown, this);

		btnSaveMovie = new Button(editMovieBar, SWT.NONE);
		btnSaveMovie.setText("Save");
		btnSaveMovie.addListener(SWT.MouseDown, this);

		// as detail
		detailMovieBar = new Composite(detailMoviePage, SWT.NONE);
		GridData gdDetailBar = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
		gdDetailBar.heightHint = 25;
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

	private void selectDetailPageToolbar(boolean edit) {
		if (edit) {
			detailMovieBar.setVisible(false);
			editMovieBar.setVisible(true);
		} else {
			detailMovieBar.setVisible(true);
			editMovieBar.setVisible(false);
		}
	}
	
	public void handleEvent(Event event) {
		// on click events
		if (event.widget instanceof ExtendedListItem) {
			detailMovieComposite.setMovie(((ExtendedListItem) event.widget).getMovie(), false);
			selectDetailPageToolbar(false);
			pagesLayout.topControl = detailMoviePage;
			pages.layout();
		} else if (event.widget == btnNewMovie) {
			detailMovieComposite.setMovie(new Movie("", "", 0, 0), true);
			selectDetailPageToolbar(true);
			pagesLayout.topControl = detailMoviePage;
			pages.layout();
		} else if (event.widget == btnAbortMovie) {
			Movie movie = detailMovieComposite.getMovie();
			if (movie.getId() != null) { // existing movie
				MovieDatabase.getInstance().abortTransaction().refreshMovie(movie);
				detailMovieComposite.setMovie(movie, false);
				selectDetailPageToolbar(false);
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
			if (FeatureConfiguration.getInstance().usePosters() && movie.containsAttribute(ImageAttribute.NAME))
				md.addMovieAttribute(movie.getAttribute(ImageAttribute.NAME));
			if (FeatureConfiguration.getInstance().useRatings() && movie.containsAttribute(RatingAttribute.NAME))
				md.addMovieAttribute(movie.getAttribute(RatingAttribute.NAME));
			md.endTransaction()
				.refreshMovie(movie);
			detailMovieComposite.setMovie(movie, false); // refresh everything, so we see the current state
			selectDetailPageToolbar(false);
			refreshOverview(isNew);
		} else if (event.widget == btnBackMovie) {
			pagesLayout.topControl = overviewPage;
			pages.layout();
		} else if (event.widget == btnEditMovie) {
			MovieDatabase.getInstance().startTransaction(); // track changes
			detailMovieComposite.setEditable(true);
			selectDetailPageToolbar(true);
			pages.layout();
		} else if (event.widget == btnDeleteMovie) {
			// TODO popup
			// TODO delete movie
		} else
			throw new UnsupportedOperationException("Unimplemented event for widget: " + event.widget.toString());
	}
}
