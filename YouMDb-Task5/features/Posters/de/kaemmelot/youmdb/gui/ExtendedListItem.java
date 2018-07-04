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
import de.kaemmelot.youmdb.models.PosterAttribute;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class ExtendedListItem extends Composite {
	private CLabel lblImage;
	
	public static final int IMAGE_HEIGHT = 90;
	public static final int IMAGE_WIDTH = 62;
	
	private int getLeftWidth() {
		return 1 + original();
	}
	
	private int getLeftHeight() {
		return 2 + original();
	}
	
	private boolean next_posters = false;
	private int addNextLeft(Composite content, int width, int maxHeight) {
		if (!next_posters) {
			lblImage = new CLabel(content, SWT.BORDER | SWT.SHADOW_OUT);
			GridData lblImageGd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
			lblImage.setLayoutData(lblImageGd);
			lblImage.setTopMargin(2);
			lblImage.setRightMargin(2);
			lblImage.setLeftMargin(2);
			lblImage.setBottomMargin(2);
			lblImage.setSize(new Point(IMAGE_WIDTH, IMAGE_HEIGHT));
			lblImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			lblImage.setText(null);
			lblImage.addListener(SWT.MouseDown, this.passThroughListener);
			
			next_posters = true;
			return 2;
		} else
			return original(content, width, maxHeight);
	}
	

	protected void movieChanged() {
		original();
		
		org.eclipse.swt.graphics.Image img = lblImage.getImage();
		if (this.movie != null) {
			PosterAttribute pa = this.movie.getAttribute(PosterAttribute.class);
			if (pa != null && pa.getImage() != null) {
				lblImage.setImage(new org.eclipse.swt.graphics.Image(this.getFont().getDevice(),
						SWTUtils.convertAWTImageToSWT(pa.getImage()
								.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH))));
			} else
				lblImage.setImage(SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png"));
		} else
			lblImage.dispose();
		if (img != null)
			img.dispose();
	}
}
