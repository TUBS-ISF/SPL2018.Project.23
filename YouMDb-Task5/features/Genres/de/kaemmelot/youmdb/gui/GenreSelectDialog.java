package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.kaemmelot.youmdb.models.Genre;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.SWTResourceManager;

public class GenreSelectDialog extends Dialog {

	private List genreList;
	private Genre[] genres;
	private Collection<Genre> selectedGenres;
	private Collection<Genre> result;
	
	protected Shell shell;

	public GenreSelectDialog(Shell parent) {
		super(parent, SWT.NONE);
		setText("Select genres");
	}

	public Collection<Genre> open(Collection<Genre> genres, Collection<Genre> selectedGenres) {
		this.genres = genres.toArray(new Genre[genres.size()]);
		this.selectedGenres = selectedGenres;
		result = selectedGenres;
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		return result;
	}

	private void createContents() {
		shell = new Shell(getParent(), SWT.BORDER | SWT.TITLE | SWT.ON_TOP | SWT.FOCUSED); // TODO how to keep focused?
		shell.setMinimumSize(new Point(450, 300));
		shell.setSize(450, 300);
		shell.setText(getText());
		GridLayout gl_shell = new GridLayout(4, false);
		gl_shell.verticalSpacing = 0;
		gl_shell.marginWidth = 0;
		gl_shell.marginHeight = 0;
		gl_shell.horizontalSpacing = 0;
		shell.setLayout(gl_shell);
		
		genreList = new List(shell, SWT.BORDER | SWT.H_SCROLL | SWT.MULTI);
		genreList.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		genreList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		
		java.util.List<String> genres = new ArrayList<String>();
		for (int i = 0; i < this.genres.length; i++)
			genres.add(i, this.genres[i].getName());
		
		java.util.List<String> selectedGenres = new ArrayList<String>();
		for (Genre g : this.selectedGenres)
			selectedGenres.add(g.getName());
		
		genreList.setItems(genres.toArray(new String[genres.size()]));
		genreList.setSelection(selectedGenres.toArray(new String[selectedGenres.size()]));
		
		Button btnAbort = new Button(shell, SWT.NONE);
		btnAbort.setText("Abort");
		btnAbort.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				GenreSelectDialog.this.shell.close();
			}
			
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnOk.setText("OK");
		btnOk.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				java.util.List<Genre> tmpRes = new ArrayList<Genre>(genreList.getSelectionCount());
				for (int sel : genreList.getSelectionIndices())
					tmpRes.add(GenreSelectDialog.this.genres[sel]);
				
				result = tmpRes;
				GenreSelectDialog.this.shell.close();
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		Button btnNone = new Button(shell, SWT.NONE);
		btnNone.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnNone.setText("None");
		btnNone.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {				
				result = new ArrayList<Genre>(0);
				GenreSelectDialog.this.shell.close();
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		new Label(shell, SWT.NONE); // placeholder
	}
}
