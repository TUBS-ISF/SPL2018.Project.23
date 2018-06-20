package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.models.Movie;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class DetailMovieComposite extends ScrolledComposite {
	private Text txtName;
	private final Text txtDesc;
	private Text txtYear;
	private Text txtLength;
	
	private Movie currentMovie = null;
	private boolean editable = false;

	private final PaintListener editablePaintListener = new PaintListener() {
		public void paintControl(PaintEvent e) {
			e.gc.setAntialias(SWT.ON);
			e.gc.setForeground(((Text) e.widget).getForeground());
			if (editable)
				e.gc.drawRoundRectangle(e.x, e.y, e.width - 1, e.height - 1, 6, 6);
		}
	};

	public void addPlaceholder(Composite parent) {
		(new Label(parent, SWT.NONE)).setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
	}
	
	public PaintListener getEditablePaintListener() {
		return editablePaintListener;
	}
	
	public boolean getEditable() {
		return editable;
	}
	
	public DetailMovieComposite(Composite parent, final Shell shell) {
		super(parent, SWT.BORDER);
		setExpandHorizontal(true);
		setExpandVertical(true);
		setLayout(new GridLayout());
		
		Composite content = new Composite(this, SWT.NONE);
		content.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		
		// split plugins to their anchor and count their height and width
		int leftWidth = 0;
		int centerWidth = 3;
		int rightWidth = 0;
		int leftHeight = 0;
		int centerHeight = 3;
		int rightHeight = 0;
		List<DetailMovieCompositePlugin> leftPlugins = new ArrayList<DetailMovieCompositePlugin>();
		List<DetailMovieCompositePlugin> centerPlugins = new ArrayList<DetailMovieCompositePlugin>();
		List<DetailMovieCompositePlugin> rightPlugins = new ArrayList<DetailMovieCompositePlugin>();
		for (DetailMovieCompositePlugin plugin : plugins) {
			switch (plugin.getDetailMovieCompositeAnchor()) {
			case Left:
				leftPlugins.add(plugin);
				if (plugin.getDetailMovieCompositeWidth() > leftWidth)
					leftWidth = plugin.getDetailMovieCompositeWidth();
				if (plugin.getDetailMovieCompositeHeight() > 0)
					leftHeight += plugin.getDetailMovieCompositeHeight();
				break;
			case Center:
				centerPlugins.add(plugin);
				if (plugin.getDetailMovieCompositeWidth() > centerWidth)
					centerWidth = plugin.getDetailMovieCompositeWidth();
				if (plugin.getDetailMovieCompositeHeight() > 0)
					centerHeight += plugin.getDetailMovieCompositeHeight();
				break;
			case Right:
				rightPlugins.add(plugin);
				if (plugin.getDetailMovieCompositeWidth() > rightWidth)
					rightWidth = plugin.getDetailMovieCompositeWidth();
				if (plugin.getDetailMovieCompositeHeight() > 0)
					rightHeight += plugin.getDetailMovieCompositeHeight();
				break;
			default:
				throw new UnsupportedOperationException("Unknown anchor point");
			}
		}
		final int maxHeight = Math.max(leftHeight, Math.max(centerHeight, rightHeight));
		
		content.setLayout(new GridLayout(leftWidth + centerWidth + rightWidth, false));

		// plugin indexes
		int li = 0;
		int ci = -3;
		int ri = 0;
		// height by anchor
		int lHeight = 0;
		int cHeight = 0;
		int rHeight = 0;
		
		// as long as there are plugins to print or placeholders to fill
		while (li < leftPlugins.size() || ci < centerPlugins.size() || ri < rightPlugins.size() || lHeight > cHeight || cHeight > rHeight) {
			if (lHeight <= cHeight && cHeight <= rHeight) { // add left
				if (li < leftPlugins.size())
					lHeight += leftPlugins.get(li++).addDetailMovieCompositeContent(leftWidth, maxHeight, content, this);
				else { // no more plugins, fill with placeholders
					for (int i = 0; i < leftWidth; i++)
						addPlaceholder(content);
					lHeight++;
				}
			} else if (lHeight > cHeight && cHeight <= rHeight) { // add center
				if (ci < 0) { // special: first add name, year and length
					if (cHeight == 0)
						addName(content, centerWidth);
					else if (cHeight == 1)
						addYear(content, centerWidth);
					else if (cHeight == 2)
						addLength(content, centerWidth);
					cHeight++;
					ci++;
				} else if (ci < centerPlugins.size())
					cHeight += centerPlugins.get(ci++).addDetailMovieCompositeContent(centerWidth, maxHeight, content, this);
				else { // no more plugins, fill with placeholders
					for (int i = 0; i < centerWidth; i++)
						addPlaceholder(content);
					cHeight++;
				}
			} else { // add right
				if (ri < rightPlugins.size())
					rHeight += rightPlugins.get(ri++).addDetailMovieCompositeContent(rightWidth, maxHeight, content, this);
				else { // no more plugins, fill with placeholders
					for (int i = 0; i < rightWidth; i++)
						addPlaceholder(content);
					rHeight++;
				}
			}
		}
		
		Label lblDesc = new Label(content, SWT.NONE);
		lblDesc.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblDesc.setText("Description:");
		for (int i = 0; i < leftWidth + centerWidth + rightWidth - 1; i++)
			addPlaceholder(content);
		
		txtDesc = new Text(content, SWT.WRAP | SWT.MULTI);
		txtDesc.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, leftWidth + centerWidth + rightWidth, 1));
		txtDesc.setEditable(editable);
		txtDesc.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				currentMovie.setDescription(txtDesc.getText());
			}
		});
		// https://stackoverflow.com/q/11522774
		txtDesc.addPaintListener(editablePaintListener);
		new TextRedrawListener(txtDesc);

		setContent(content);
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		txtName.setEditable(editable);
		txtYear.setEditable(editable);
		txtLength.setEditable(editable);
		txtDesc.setEditable(editable);
		for (DetailMovieCompositePlugin plugin : plugins)
			plugin.editableChanged(this);
	}
	
	public void setMovie(Movie movie) {
		setMovie(movie, false);
	}
	
	public void setMovie(Movie movie, boolean editable) {
		currentMovie = movie;
		if (movie != null) {
			txtName.setText(movie.getName());
			txtDesc.setText(movie.getDescription());
			txtYear.setText(movie.getReleaseYear().toString());
			txtLength.setText(movie.getLength().toString());
			setEditable(editable);
		} else {
			txtName.setText("");
			txtDesc.setText("");
			txtYear.setText("");
			txtLength.setText("");
		}
		for (DetailMovieCompositePlugin plugin : plugins)
			plugin.movieChanged(this);
		layout();
	}
	
	public Movie getMovie() {
		return currentMovie;
	}
	
	public void addName(Composite parent, int width) {
		Label lblName = new Label(parent, SWT.NONE);
		lblName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");
		
		txtName = new Text(parent, SWT.NONE);
		txtName.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		txtName.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, width - 1, 1));
		txtName.setEditable(editable);
		txtName.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				if (!txtName.getText().isEmpty())
					currentMovie.setName(txtName.getText());
				else
					txtName.setText(currentMovie.getName());
			}
		});
		// https://stackoverflow.com/q/11522774
		txtName.addPaintListener(editablePaintListener);
		new TextRedrawListener(txtName);
	}
		
	public void addYear(Composite parent, int width) {
		for (int i = 0; i < width - 2; i++)
			addPlaceholder(parent);
		
		Label lblYear = new Label(parent, SWT.NONE);
		lblYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblYear.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
		lblYear.setText("Year:");
		
		txtYear = new Text(parent, SWT.NONE);
		txtYear.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		GridData gd_txtYear = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_txtYear.widthHint = 25;
		txtYear.setLayoutData(gd_txtYear);
		txtYear.setEditable(editable);
		txtYear.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
	            String newS = txtYear.getText().substring(0, e.start) + e.text + txtYear.getText().substring(e.end);
	            boolean isInt = true;
	            try {
	            	Integer.parseInt(newS, 10);
	            } catch (NumberFormatException nfex) {
	            	isInt = false;
	            }
	            e.doit = isInt;
			}
		});
		txtYear.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				if (!txtYear.getText().isEmpty()) {
					Integer year = Integer.parseInt(txtYear.getText(), 10);
					if (year >= 1900 && year <= 2100)
						currentMovie.setReleaseYear(year);
					else
						txtYear.setText(currentMovie.getReleaseYear().toString());
				} else {
					currentMovie.setReleaseYear(0);
					txtYear.setText(currentMovie.getReleaseYear().toString());
				}
			}
		});
		// https://stackoverflow.com/q/11522774
		txtYear.addPaintListener(editablePaintListener);
		new TextRedrawListener(txtYear);
	}
		
	public void addLength(Composite parent, int width) {
		for (int i = 0; i < width - 2; i++)
			addPlaceholder(parent);
		
		Label lblLength = new Label(parent, SWT.NONE);
		lblLength.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblLength.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
		lblLength.setText("Length (mins):");
		
		txtLength = new Text(parent, SWT.NONE);
		txtLength.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		GridData gd_txtLength = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_txtLength.widthHint = 25;
		txtLength.setLayoutData(gd_txtLength);
		txtLength.setEditable(editable);
		txtLength.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
	            String newS = txtLength.getText().substring(0, e.start) + e.text + txtLength.getText().substring(e.end);
	            boolean isInt = true;
	            try {
	            	isInt = Integer.parseInt(newS, 10) >= 0;
	            } catch (NumberFormatException nfex) {
	            	isInt = false;
	            }
	            e.doit = isInt;
			}
		});
		txtLength.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				if (!txtLength.getText().isEmpty())
					currentMovie.setLength(Integer.parseInt(txtLength.getText(), 10));
				else
					currentMovie.setLength(0);
			}
		});
		// https://stackoverflow.com/q/11522774
		txtLength.addPaintListener(editablePaintListener);
		new TextRedrawListener(txtLength);
	}
	
	private static List<DetailMovieCompositePlugin> plugins = new ArrayList<DetailMovieCompositePlugin>();
	
	public static void AddPlugin(DetailMovieCompositePlugin plugin) {
		plugins.add(plugin);
	}
}
