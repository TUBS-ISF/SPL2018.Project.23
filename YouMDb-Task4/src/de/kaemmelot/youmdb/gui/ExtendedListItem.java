package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.models.Movie;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class ExtendedListItem extends Composite {
	private Movie movie;
	private final Label lblName;
	private final Label lblDescription;
	
	public ExtendedListItem(ExtendedList parentList, Movie movie) {
		super(parentList.getContentComposite(), SWT.NONE);
		final Composite parent = parentList.getContentComposite();
		this.movie = movie;
		
		setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		
		// split plugins to their anchor and count their height and width
		int leftWidth = 0;
		int rightWidth = 0;
		int leftHeight = 0;
		int rightHeight = 0;
		List<ExtendedListItemPlugin> leftPlugins = new ArrayList<ExtendedListItemPlugin>();
		List<ExtendedListItemPlugin> rightPlugins = new ArrayList<ExtendedListItemPlugin>();
		for (ExtendedListItemPlugin plugin : plugins) {
			switch (plugin.getExtendedListItemAnchor()) {
			case Left:
				leftPlugins.add(plugin);
				if (plugin.getExtendedListItemWidth() > leftWidth)
					leftWidth = plugin.getExtendedListItemWidth();
				if (plugin.getExtendedListItemHeight() > 0)
					leftHeight += plugin.getExtendedListItemHeight();
				break;
			case Right:
				rightPlugins.add(plugin);
				if (plugin.getExtendedListItemWidth() > rightWidth)
					rightWidth = plugin.getExtendedListItemWidth();
				if (plugin.getExtendedListItemHeight() > 0)
					rightHeight += plugin.getExtendedListItemHeight();
				break;
			default:
				throw new UnsupportedOperationException("Unknown anchor point");
			}
		}
		final int maxHeight = Math.max(leftHeight, Math.max(2, rightHeight));
		
		GridLayout gridLayout = new GridLayout(leftWidth + rightWidth + 1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 1;
		setLayout(gridLayout);
		setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		setCursor(SWTResourceManager.getCursor(SWT.CURSOR_HAND));
		
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				if (getSize().x > parent.getSize().x)
					setSize(parent.getSize().x, getSize().y);
			}
		});
		
		for (ExtendedListItemPlugin plugin : leftPlugins)
			plugin.addExtendedListItemContent(maxHeight, this, this);
				
		lblName = new Label(this, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblName.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblName.addListener(SWT.MouseDown, passThroughListener);
		
		lblDescription = new Label(this, SWT.WRAP);
		lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblDescription.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		lblDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblDescription.addListener(SWT.MouseDown, passThroughListener);

		for (ExtendedListItemPlugin plugin : rightPlugins)
			plugin.addExtendedListItemContent(maxHeight, this, this);
		
		refresh();
	}
	
	final private Listener passThroughListener = new Listener() {
		public void handleEvent(Event event) {
			ExtendedListItem.this.notifyListeners(event.type, event);
		}
	};
	
	public Listener getPassThroughListener() {
		return passThroughListener;
	}
	
	public Movie getMovie() {
		return movie;
	}
	
	public void refresh() {		
		lblName.setText(movie.getName());
		lblDescription.setText(movie.getDescription());
		for (ExtendedListItemPlugin plugin: plugins)
			plugin.movieChanged(this);
	}
	
	@Override
	public void dispose() {
		movie = null;
		for (ExtendedListItemPlugin plugin: plugins)
			plugin.movieChanged(this);
		super.dispose();
	}

	private static List<ExtendedListItemPlugin> plugins = new ArrayList<ExtendedListItemPlugin>();
	
	public static void AddPlugin(ExtendedListItemPlugin plugin) {
		plugins.add(plugin);
	}
}
