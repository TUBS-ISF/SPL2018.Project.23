package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;

public class GenreEditList extends Composite implements SelectionListener {
	private Text txtGenreName;
	private Text txtGenreDesc;
	private List genreList;
	private Button btnDelete;

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
		genreList.setItems(new String[] {"Example1", "Example2"});
		genreList.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		genreList.addSelectionListener(this);
		
		Composite editGenreComposite = new Composite(sashForm, SWT.NONE);
		editGenreComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		editGenreComposite.setLayout(new GridLayout(1, false));
		
		txtGenreName = new Text(editGenreComposite, SWT.NONE);
		txtGenreName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		txtGenreName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		
		txtGenreDesc = new Text(editGenreComposite, SWT.WRAP | SWT.H_SCROLL | SWT.CANCEL | SWT.MULTI);
		txtGenreDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sashForm.setWeights(new int[] {1, 2});
		
		Composite menuBar = new Composite(this, SWT.NONE);
		menuBar.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_menuBar = new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 1, 1);
		gd_menuBar.heightHint = 20;
		menuBar.setLayoutData(gd_menuBar);
		
		Button btnNew = new Button(menuBar, SWT.NONE);
		btnNew.setText("New");
		
		btnDelete = new Button(menuBar, SWT.NONE);
		btnDelete.setEnabled(false);
		btnDelete.setText("Delete");
	}

	public void widgetSelected(SelectionEvent e) {
		btnDelete.setEnabled(genreList.getSelectionIndex() != -1);
	}

	public void widgetDefaultSelected(SelectionEvent e) {
	}
}
