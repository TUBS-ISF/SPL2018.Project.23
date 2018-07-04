package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.Database;
import de.kaemmelot.youmdb.models.Genre;

import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;

public class GenreEditList extends Composite implements SelectionListener, Listener {
	private Text txtGenreName;
	private Text txtGenreDesc;
	private List genreList;
	private Button btnNew;
	private Button btnDelete;
	
	private Genre currentGenre = null;

	public GenreEditList(Composite parent) {
		super(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		SashForm sashForm = new SashForm(this, SWT.BORDER | SWT.SMOOTH);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		genreList = new List(sashForm, SWT.H_SCROLL);
		genreList.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		genreList.addSelectionListener(this);
		
		final PaintListener editablePaintListener = new PaintListener() {
			public void paintControl(PaintEvent e) {
				e.gc.setAntialias(SWT.ON);
				e.gc.setForeground(((Text) e.widget).getForeground());
				if (((Text) e.widget).getEditable())
					e.gc.drawRoundRectangle(e.x, e.y, e.width - 1, e.height - 1, 6, 6);
			}
		};
		
		Composite editGenreComposite = new Composite(sashForm, SWT.NONE);
		editGenreComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		editGenreComposite.setLayout(new GridLayout(1, false));
		
		txtGenreName = new Text(editGenreComposite, SWT.WRAP);
		txtGenreName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		txtGenreName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		txtGenreName.setEditable(false);
		// https://stackoverflow.com/q/11522774
		txtGenreName.addPaintListener(editablePaintListener);
		new TextRedrawListener(txtGenreName);
		txtGenreName.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				handleNameChanged();
			}
			public void focusGained(FocusEvent e) {
			}
		});
		txtGenreName.addKeyListener(new KeyListener() {
			public void keyReleased(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == '\r') {
					handleNameChanged();
					txtGenreDesc.forceFocus();
				}
			}
		});
		txtGenreName.setEnabled(false);
		
		Label lblDesc = new Label(editGenreComposite, SWT.NONE);
		lblDesc.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblDesc.setText("Description:");
		lblDesc.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));
		
		txtGenreDesc = new Text(editGenreComposite, SWT.WRAP | SWT.H_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtGenreDesc.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		txtGenreDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		txtGenreDesc.setEditable(false);
		// https://stackoverflow.com/q/11522774
		txtGenreDesc.addPaintListener(editablePaintListener);
		new TextRedrawListener(txtGenreDesc);
		txtGenreDesc.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				Database db = Database.getInstance();
				db.startTransaction();
				currentGenre.setDescription(txtGenreDesc.getText());
				db.endTransaction();
			}
			public void focusGained(FocusEvent e) {
			}
		});
		txtGenreDesc.setEnabled(false);
		
		sashForm.setWeights(new int[] {1, 3});
		
		Composite menuBar = new Composite(this, SWT.NONE);
		menuBar.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_menuBar = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
		gd_menuBar.heightHint = YoumdbWindow.MENU_HEIGHT;
		menuBar.setLayoutData(gd_menuBar);
		
		btnNew = new Button(menuBar, SWT.NONE);
		btnNew.setText("New");
		btnNew.addListener(SWT.MouseDown, this);
		
		btnDelete = new Button(menuBar, SWT.NONE);
		btnDelete.setEnabled(false);
		btnDelete.setText("Delete");
		btnDelete.addListener(SWT.MouseDown, this);
		
		updateGenreList();
	}
	
	private void handleNameChanged() {
		if (currentGenre == null && txtGenreName.getText().length() > 0) {
			// create new genre
			currentGenre = new Genre(txtGenreName.getText());
			Database db = Database.getInstance();
			db.startTransaction();
			db.add(currentGenre);
			db.endTransaction().refresh(currentGenre); // got an id now
			txtGenreDesc.setEditable(true);
			txtGenreDesc.setEnabled(true);
			updateGenreList(currentGenre.getName());
		} else if (currentGenre == null) {
			// abort new genre
			txtGenreName.setEditable(false);
		} else if (currentGenre != null && txtGenreName.getText().length() > 0) {
			// edit name
			Database db = Database.getInstance();
			db.startTransaction();
			currentGenre.setName(txtGenreName.getText());
			db.endTransaction();
			updateGenreList(currentGenre.getName());
		}
	}
	
	public void updateGenreList() {
		updateGenreList(null);
	}
	
	private void updateGenreList(String selection) {
		java.util.List<Genre> genres = Database.getInstance().getAll(Genre.class);
		String[] genreItems = new String[genres.size()];
		for (int g = 0; g < genres.size(); g++)
			genreItems[g] = genres.get(g).getName();

		genreList.setItems(genreItems);
		genreList.setSelection(selection != null ? Arrays.asList(genreItems).indexOf(selection) : -1);
		
		btnDelete.setEnabled(selection != null);
		
		txtGenreName.setEditable(selection != null);
		txtGenreName.setEnabled(selection != null);
		txtGenreDesc.setEditable(selection != null);
		txtGenreDesc.setEnabled(selection != null);
		if (selection != null) {
			currentGenre = Database.getInstance().getByName(Genre.class, selection);
			txtGenreName.setText(currentGenre.getName());
			txtGenreDesc.setText(currentGenre.getDescription());
		} else {
			currentGenre = null;
			txtGenreName.setText("");
			txtGenreDesc.setText("");
		}
	}
	
	public void widgetSelected(SelectionEvent e) {
		final int selection = genreList.getSelectionIndex();
		btnDelete.setEnabled(selection != -1);
		if (selection == -1)
			return;
		
		currentGenre = Database.getInstance().getByName(Genre.class, genreList.getItem(genreList.getSelectionIndex()));
		txtGenreName.setEditable(true);
		txtGenreName.setEnabled(true);
		txtGenreDesc.setEditable(true);
		txtGenreDesc.setEnabled(true);
		txtGenreName.setText(currentGenre.getName());
		txtGenreDesc.setText(currentGenre.getDescription());
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void handleEvent(Event event) {
		// on click events
		if (event.widget == btnNew) {
			genreList.setSelection(-1);
			currentGenre = null;
			txtGenreName.setEditable(true);
			txtGenreDesc.setEditable(false);
			txtGenreName.setEnabled(true);
			txtGenreDesc.setEnabled(false);
			txtGenreName.setText("");
			txtGenreDesc.setText("");
			if (!txtGenreName.setFocus()) {
				txtGenreName.setEditable(false);
				txtGenreName.setEnabled(false);
			}
			btnDelete.setEnabled(false);
		} else if (event.widget == btnDelete) {
			// TODO deleting a genre while movies reference it is causing an exception
			txtGenreName.setEditable(false);
			txtGenreName.setEnabled(false);
			txtGenreDesc.setEditable(false);
			txtGenreDesc.setEnabled(false);
			txtGenreName.setText("");
			txtGenreDesc.setText("");
			Database db = Database.getInstance();
			db.startTransaction();
			db.remove(currentGenre);
			db.endTransaction();
			currentGenre = null;
			updateGenreList(); // selection = -1 & btnDelete.disable
		} else
			throw new UnsupportedOperationException("Unimplemented event for widget: " + event.widget.toString());
	}
}
