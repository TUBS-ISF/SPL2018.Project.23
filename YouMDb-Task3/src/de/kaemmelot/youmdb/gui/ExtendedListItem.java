package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import java.awt.Image;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.experimental.swt.SWTUtils;

import de.kaemmelot.youmdb.models.Movie;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class ExtendedListItem extends Composite {
	
	private org.eclipse.swt.graphics.Image itemImage = null;
	private final Movie movie;
	private final Label lblName;
	private final Label lblDescription;
	//#if Posters
	private final CLabel lblImage;
	//#endif
	
	public ExtendedListItem(ExtendedList parentList, Movie movie) {
		super(parentList.getContentComposite(), SWT.NONE);
		final Composite parent = parentList.getContentComposite();
		this.movie = movie;
		
		setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		GridLayout gridLayout;
		//#if Posters
		gridLayout = new GridLayout(2, false);
		//#else
//@		gridLayout = new GridLayout();
		//#endif
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
		
		final ExtendedListItem t = this;
		final Listener passThroughListener = new Listener() {
			public void handleEvent(Event event) {
				t.notifyListeners(event.type, event);
			}
		};
		
		//#if Posters
		lblImage = new CLabel(this, SWT.BORDER | SWT.SHADOW_OUT);
		GridData lblImageGd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
		// This sometimes causes problems with showing the image
		//lblImageGd.heightHint = IMAGE_HEIGHT;
		//lblImageGd.widthHint = IMAGE_WIDTH;
		lblImage.setLayoutData(lblImageGd);
		lblImage.setTopMargin(2);
		lblImage.setRightMargin(2);
		lblImage.setLeftMargin(2);
		lblImage.setBottomMargin(2);
		lblImage.setSize(new Point(YoumdbWindow.IMAGE_WIDTH, YoumdbWindow.IMAGE_HEIGHT));
		lblImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblImage.setText(null);
		lblImage.addListener(SWT.MouseDown, passThroughListener);
		//#endif
		
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
		
		refresh();
	}
	
	public Movie getMovie() {
		return movie;
	}
	
	public void refresh() {
		org.eclipse.swt.graphics.Image img;
		//#if Posters
		if (itemImage != null)
			itemImage.dispose();
		if (movie.getImage() != null) {
			itemImage = img = new org.eclipse.swt.graphics.Image(getFont().getDevice(),
					SWTUtils.convertAWTImageToSWT(movie.getImage()
							.getScaledInstance(YoumdbWindow.IMAGE_WIDTH, YoumdbWindow.IMAGE_HEIGHT, Image.SCALE_SMOOTH)));
		} else {
			itemImage = null;
			img = SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png");
		}
		lblImage.setImage(img);
		//#endif
		
		lblName.setText(movie.getName());
		lblDescription.setText(movie.getDescription());
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (itemImage != null)
			itemImage.dispose();
	}

}
