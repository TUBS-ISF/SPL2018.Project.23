package de.kaemmelot.youmdb;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.models.ActorRolePair;
import de.kaemmelot.youmdb.models.Movie;
import de.kaemmelot.youmdb.gui.ActorRoleDialog;
import de.kaemmelot.youmdb.gui.DetailMovieComposite;
import de.kaemmelot.youmdb.gui.YoumdbWindow;
import de.kaemmelot.youmdb.models.ActorRoleAttribute;

public privileged aspect Actors {
	// Database
	List<Class<?>> around(): execution(static List<Class<?>> Database.getClasses()) {
		List<Class<?>> result = proceed();
		result.add(ActorRolePair.class);
		result.add(ActorRoleAttribute.class);
		return result;
	}
	
	// DetailMovieComposite
	private CTabItem DetailMovieComposite.actorsPage = null;
	private Table DetailMovieComposite.actorsTable = null;
	private TableColumn DetailMovieComposite.actorColumn = null;
	private TableColumn DetailMovieComposite.roleColumn = null;
	private Composite DetailMovieComposite.menu = null;
	private Button DetailMovieComposite.btnNewActorRole = null;
	private Button DetailMovieComposite.btnEditActorRole = null;
	private Button DetailMovieComposite.btnDeleteActorRole = null;
	private Button DetailMovieComposite.btnUpActorRole = null;
	private Button DetailMovieComposite.btnDownActorRole = null;
	
	void around(final DetailMovieComposite that): execution(void DetailMovieComposite.addMovieTabs()) && this(that) {
		that.actorsPage = new CTabItem(that.pages, SWT.NONE);
		that.actorsPage.setText("Actors");
		
		final Composite content = new Composite(that.pages, SWT.NONE);
		content.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		content.setLayout(layout);
		
		that.actorsPage.setControl(content);
		
		that.actorsTable = new Table(content, SWT.FULL_SELECTION);
		that.actorsTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		that.actorsTable.setHeaderVisible(true);
		
		that.actorColumn = new TableColumn(that.actorsTable, SWT.CENTER);
		that.actorColumn.setText("Actor");
		
		that.roleColumn = new TableColumn(that.actorsTable, SWT.CENTER);
		that.roleColumn.setText("Role");

		that.resized(); // initialize size
		that.getShell().addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				that.resized();
			}
		});
		
		that.menu = new Composite(content, SWT.NONE);
		final RowLayout menuLayout = new RowLayout(SWT.HORIZONTAL);
		menuLayout.marginBottom = 0;
		menuLayout.marginTop = 0;
		menuLayout.marginHeight = 1;
		menuLayout.spacing = 0;
		menuLayout.fill = true;
		menuLayout.center = true;
		that.menu.setLayout(menuLayout);
		final GridData gdmenu = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
		gdmenu.heightHint = YoumdbWindow.MENU_HEIGHT;
		that.menu.setLayoutData(gdmenu);

		that.btnNewActorRole = new Button(that.menu, SWT.NONE);
		that.btnNewActorRole.setText("New");
		that.btnNewActorRole.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				ActorRoleAttribute attr = that.getMovie().getAttribute(ActorRoleAttribute.class);
				if (attr == null)
					that.getMovie().setAttribute(ActorRoleAttribute.class, attr = (new ActorRoleAttribute()));
				ActorRoleDialog ard = new ActorRoleDialog(that.getShell()); // ask user for input
				ActorRolePair arp = ard.open(attr.getActorRoles().size());
				attr.addActorRole(arp);
				TableItem newItem = new TableItem(that.actorsTable, SWT.NONE); // add to table
				newItem.setText(0, arp.getActor());
				newItem.setText(1, arp.getRole());
				Database.getInstance().add(attr).add(arp);
				that.actorsTable.setSelection(newItem);
				that.layout();
			}
		});

		that.btnEditActorRole = new Button(that.menu, SWT.NONE);
		that.btnEditActorRole.setText("Edit");
		that.btnEditActorRole.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				int sel = that.actorsTable.getSelectionIndex();
				if (sel == -1)
					return;
				TableItem item = that.actorsTable.getSelection()[0];
				ActorRoleAttribute attr = that.getMovie().getAttribute(ActorRoleAttribute.class);
				if (attr == null)
					return;
				ActorRolePair arp = null;
				for (ActorRolePair a : attr.getActorRoles()) {
					if (a.getOrder() == sel) {
						arp = a;
						break;
					}
				}
				if (arp == null)
					return;
				ActorRoleDialog ard = new ActorRoleDialog(that.getShell()); // ask user for input
				arp = ard.open(arp);
				item.setText(0, arp.getActor());
				item.setText(1, arp.getRole());
				that.actorsTable.setSelection(item);
			}
		});

		that.btnDeleteActorRole = new Button(that.menu, SWT.NONE);
		that.btnDeleteActorRole.setText("Delete");
		that.btnDeleteActorRole.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				int sel = that.actorsTable.getSelectionIndex();
				if (sel == -1)
					return;
				ActorRoleAttribute attr = that.getMovie().getAttribute(ActorRoleAttribute.class);
				if (attr == null)
					return;
				Database.getInstance().remove(attr.removeActorRole(sel));
				that.actorsTable.remove(sel);
				that.layout();
			}
		});

		Label menuDivider = new Label(that.menu, SWT.NONE);
		menuDivider.setText("|");
		
		that.btnUpActorRole = new Button(that.menu, SWT.NONE);
		that.btnUpActorRole.setText("Move up");
		that.btnUpActorRole.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				int sel = that.actorsTable.getSelectionIndex();
				if (sel == -1 || sel == 0)
					return;
				ActorRoleAttribute attr = that.getMovie().getAttribute(ActorRoleAttribute.class);
				if (attr == null)
					return;
				ActorRolePair prev = attr.switchActorRoles(sel - 1, sel);
				// remove the upper and add it new one index lower
				that.actorsTable.remove(sel - 1);
				TableItem newItem = new TableItem(that.actorsTable, SWT.NONE, sel); // add to table
				newItem.setText(0, prev.getActor());
				newItem.setText(1, prev.getRole());
				that.actorsTable.layout();
				that.actorsTable.setSelection(sel - 1);
				tableSelectionChanged(that);
			}
		});

		that.btnDownActorRole = new Button(that.menu, SWT.NONE);
		that.btnDownActorRole.setText("Move down");
		that.btnDownActorRole.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				int sel = that.actorsTable.getSelectionIndex();
				if (sel == -1 || sel == that.actorsTable.getItemCount() - 1)
					return;
				ActorRoleAttribute attr = that.getMovie().getAttribute(ActorRoleAttribute.class);
				if (attr == null)
					return;
				ActorRolePair prev = attr.switchActorRoles(sel + 1, sel);
				// remove the lower and add it new one index higher
				that.actorsTable.remove(sel + 1);
				TableItem newItem = new TableItem(that.actorsTable, SWT.NONE, sel); // add to table
				newItem.setText(0, prev.getActor());
				newItem.setText(1, prev.getRole());
				that.actorsTable.layout();
				that.actorsTable.setSelection(sel + 1);
				tableSelectionChanged(that);
			}
		});
		
		// disable buttons if not needed
		that.actorsTable.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				tableSelectionChanged(that);
			}
		});
		tableSelectionChanged(that); // initialize
		
		// hide menu at first
		that.menu.setVisible(false);
		gdmenu.exclude = true;
		
		// Show menu if Actors page is clicked
		that.pages.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				actorTabSelectionChanged(that, event.item);
			}
		});
		
		proceed(that);
	}
	
	before(DetailMovieComposite that, Movie movie, boolean editable): execution(void DetailMovieComposite.setMovie(Movie, boolean)) && this(that) && args(movie, editable) {
		// remove old
		that.actorsTable.removeAll();
		// add current
		ActorRoleAttribute attr;
		if (movie != null && (attr = movie.getAttribute(ActorRoleAttribute.class)) != null) {
			for (ActorRolePair a : attr.getActorRoles()) {
				TableItem newItem = new TableItem(that.actorsTable, SWT.NONE); // add to table
				newItem.setText(0, a.getActor());
				newItem.setText(1, a.getRole());
			}
		}
		tableSelectionChanged(that);
	}
	
	after(DetailMovieComposite that): execution(void DetailMovieComposite.setEditable(boolean)) && this(that) {
		actorTabSelectionChanged(that, that.pages.getSelection());
	}
	
	private void tableSelectionChanged(DetailMovieComposite that) {
		int sel = that.actorsTable.getSelectionIndex();
		boolean active = sel != -1;
		that.btnEditActorRole.setEnabled(active);
		that.btnDeleteActorRole.setEnabled(active);
		that.btnUpActorRole.setEnabled(active && sel != 0);
		that.btnDownActorRole.setEnabled(active && sel != that.actorsTable.getItemCount() - 1);
		if (active)
			that.resized();
	}
	
	private void actorTabSelectionChanged(DetailMovieComposite that, Widget selection) {
		boolean vis = that.getEditable() && selection == that.actorsPage;
		that.menu.setVisible(vis);
		((GridData) that.menu.getLayoutData()).exclude = !vis;
		that.actorsTable.getParent().layout();
		//that.menu.pack();
		//that.actorsTable.pack();
		//that.menu.pack();
		//that.actorsTable.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		//that.layout();
	}
	
	private void DetailMovieComposite.resized() {
		int width = getShell().getSize().y;
		actorColumn.setWidth(width / 2);
		roleColumn.setWidth(width - (width / 2));
		layout();
		actorsTable.getParent().layout();
	}
}
