package de.kaemmelot.youmdb.plugins;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.Database;
import de.kaemmelot.youmdb.Plugin;
import de.kaemmelot.youmdb.YouMDb;
import de.kaemmelot.youmdb.gui.DetailMovieComposite;
import de.kaemmelot.youmdb.gui.DetailMovieCompositeAnchor;
import de.kaemmelot.youmdb.gui.DetailMovieCompositePlugin;
import de.kaemmelot.youmdb.gui.TextRedrawListener;
import de.kaemmelot.youmdb.models.RatingAttribute;

public class RatingPlugin implements Plugin, DetailMovieCompositePlugin {
	private Text txtRating;
	
	public DetailMovieCompositeAnchor getDetailMovieCompositeAnchor() {
		return DetailMovieCompositeAnchor.Center;
	}

	public int getDetailMovieCompositeWidth() {
		return 2;
	}

	public int getDetailMovieCompositeHeight() {
		return 1;
	}

	public int addDetailMovieCompositeContent(int othersWidth, int othersHeight, Composite parent, final DetailMovieComposite _this) {
		for (int i = 0; i < othersWidth - 2; i++)
			_this.addPlaceholder(parent);
		
		Label lblRating = new Label(parent, SWT.NONE);
		lblRating.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblRating.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false, 1, 1));
		lblRating.setText("Rating (out of 10):");
		
		txtRating = new Text(parent, SWT.NONE);
		txtRating.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		GridData gd_txtRating = new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1);
		gd_txtRating.widthHint = 25;
		txtRating.setLayoutData(gd_txtRating);
		txtRating.setEditable(_this.getEditable());
		txtRating.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
	            String newS = txtRating.getText().substring(0, e.start) + e.text + txtRating.getText().substring(e.end);
	            boolean isValidInt = true;
	            try {
	            	Integer i = Integer.parseInt(newS, 10);
	            	isValidInt = i >= 0 && i <= 10;
	            } catch (NumberFormatException nfex) {
	            	isValidInt = false;
	            }
	            e.doit = isValidInt;
			}
		});
		txtRating.addListener(SWT.FocusOut, new Listener() {
			public void handleEvent(Event event) {
				RatingAttribute ra = _this.getMovie().getAttribute(RatingAttribute.class);
				if (ra == null)
					ra = _this.getMovie().setAttribute(RatingAttribute.class, new RatingAttribute());
				if (!txtRating.getText().isEmpty())
					ra.setRating(Integer.parseInt(txtRating.getText(), 10));
				else if ((txtRating.getText().isEmpty() || Integer.parseInt(txtRating.getText(), 10) == 0))
					ra.setRating(null);
			}
		});
		// https://stackoverflow.com/q/11522774
		txtRating.addPaintListener(_this.getEditablePaintListener());
		new TextRedrawListener(txtRating);

		return 1;
	}
	
	public void editableChanged(DetailMovieComposite _this) {
		txtRating.setEditable(_this.getEditable());
	}
	
	public void movieChanged(DetailMovieComposite _this) {
		if (_this.getMovie() != null) {
			RatingAttribute ra = _this.getMovie().getAttribute(RatingAttribute.class);
			if (ra == null)
				ra = _this.getMovie().setAttribute(RatingAttribute.class, new RatingAttribute());
			txtRating.setText(ra.getRating() != null ? ra.getRating().toString() : "0");
		} else
			txtRating.setText("");
	}
	
	public void registerPlugin(String[] args) {
		Database.registerClass(RatingAttribute.class);
		DetailMovieComposite.AddPlugin(this);
	}

	public void registerOverviewTabs(YouMDb window) { }
}
