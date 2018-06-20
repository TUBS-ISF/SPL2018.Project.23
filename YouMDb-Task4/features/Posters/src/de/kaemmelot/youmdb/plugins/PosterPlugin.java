package de.kaemmelot.youmdb.plugins;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jfree.experimental.swt.SWTUtils;

import de.kaemmelot.youmdb.Database;
import de.kaemmelot.youmdb.Plugin;
import de.kaemmelot.youmdb.YouMDb;
import de.kaemmelot.youmdb.gui.DetailMovieComposite;
import de.kaemmelot.youmdb.gui.DetailMovieCompositeAnchor;
import de.kaemmelot.youmdb.gui.DetailMovieCompositePlugin;
import de.kaemmelot.youmdb.gui.ExtendedListItem;
import de.kaemmelot.youmdb.gui.ExtendedListItemAnchor;
import de.kaemmelot.youmdb.gui.ExtendedListItemPlugin;
import de.kaemmelot.youmdb.models.PosterAttribute;

public class PosterPlugin implements Plugin, DetailMovieCompositePlugin, ExtendedListItemPlugin {
	private CLabel lblImage;

	private org.eclipse.swt.graphics.Image image;
	private final org.eclipse.swt.graphics.Image noImage = SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png");
	
	public static final int IMAGE_HEIGHT = 90;
	public static final int IMAGE_WIDTH = 62;
	
	public DetailMovieCompositeAnchor getDetailMovieCompositeAnchor() {
		return DetailMovieCompositeAnchor.Left;
	}

	public int getDetailMovieCompositeWidth() {
		return 1;
	}

	public int getDetailMovieCompositeHeight() {
		return -1;
	}

	public int addDetailMovieCompositeContent(int othersWidth, int othersHeight, Composite parent, final DetailMovieComposite _this) {
		lblImage = new CLabel(parent, SWT.BORDER | SWT.SHADOW_OUT);
		GridData gd_lblImage = new GridData(SWT.LEFT, SWT.CENTER, false, false, othersWidth, othersHeight);
		lblImage.setLayoutData(gd_lblImage);
		lblImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblImage.setTopMargin(2);
		lblImage.setBottomMargin(2);
		lblImage.setRightMargin(2);
		lblImage.setLeftMargin(2);
		lblImage.setText(null);
		lblImage.setImage(noImage);
		lblImage.setSize(new Point(IMAGE_WIDTH, IMAGE_HEIGHT));
		lblImage.setCursor(SWTResourceManager.getCursor(SWT.CURSOR_ARROW));
		lblImage.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				if (!_this.getEditable())
					return;
				
				FileDialog fd = new FileDialog(_this.getShell(), SWT.OPEN);
				fd.setText("Select poster");
				fd.setFilterExtensions(new String[] {"*.jpg", "*.jpeg", "*.png", "*.gif"});
				String selected = fd.open();
				if (selected != null) { // null == cancel
					try {
						BufferedImage origImg = ImageIO.read(new File(selected).toURI().toURL());
						// just to be sure it's in the correct format
						BufferedImage convImg = new BufferedImage(origImg.getWidth(), origImg.getHeight(), BufferedImage.TYPE_INT_RGB);
						convImg.createGraphics().drawImage(origImg, 0, 0, null);

						PosterAttribute pa = _this.getMovie().getAttribute(PosterAttribute.class);
						if (pa == null)
							pa = _this.getMovie().setAttribute(PosterAttribute.class, new PosterAttribute());
						pa.setImage(convImg);
						
						movieChanged(_this);
					} catch (IOException ioe) {
						ioe.printStackTrace();
					}
				}
			}
		});
		// TODO user: delete image?
		
		return othersHeight;
	}
	
	public void editableChanged(DetailMovieComposite _this) {
		lblImage.setCursor(SWTResourceManager.getCursor(_this.getEditable() ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW));
	}
	
	public void movieChanged(DetailMovieComposite _this) {
		if (image != null) // release old image
			image.dispose();

		image = null;
		org.eclipse.swt.graphics.Image newImg = noImage;
		PosterAttribute pa = _this.getMovie() != null ? _this.getMovie().getAttribute(PosterAttribute.class) : null;
		if (pa != null && pa.getImage() != null) {
			newImg = image = new org.eclipse.swt.graphics.Image(_this.getFont().getDevice(),
					SWTUtils.convertAWTImageToSWT(pa.getImage()
					.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH)));
		}
		
		lblImage.setImage(newImg);
	}

	public ExtendedListItemAnchor getExtendedListItemAnchor() {
		return ExtendedListItemAnchor.Left;
	}

	public int getExtendedListItemWidth() {
		return 1;
	}

	public int getExtendedListItemHeight() {
		return 1;
	}

	private Map<ExtendedListItem, CLabel> extendedListItemImages = new HashMap<ExtendedListItem, CLabel>();
	
	public void addExtendedListItemContent(int height, Composite parent, ExtendedListItem _this) {
		CLabel lblImage = new CLabel(parent, SWT.BORDER | SWT.SHADOW_OUT);
		GridData lblImageGd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
		lblImage.setLayoutData(lblImageGd);
		lblImage.setTopMargin(2);
		lblImage.setRightMargin(2);
		lblImage.setLeftMargin(2);
		lblImage.setBottomMargin(2);
		lblImage.setSize(new Point(IMAGE_WIDTH, IMAGE_HEIGHT));
		lblImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblImage.setText(null);
		lblImage.addListener(SWT.MouseDown, _this.getPassThroughListener());
		extendedListItemImages.put(_this, lblImage);
	}

	public void movieChanged(ExtendedListItem _this) {
		if (!extendedListItemImages.containsKey(_this))
			throw new IllegalStateException();
		
		CLabel lblImage = extendedListItemImages.get(_this);
		org.eclipse.swt.graphics.Image img = lblImage.getImage();
		if (_this.getMovie() != null) {
			PosterAttribute pa = _this.getMovie().getAttribute(PosterAttribute.class);
			if (pa != null && pa.getImage() != null) {
				lblImage.setImage(new org.eclipse.swt.graphics.Image(_this.getFont().getDevice(),
						SWTUtils.convertAWTImageToSWT(pa.getImage()
								.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH))));
			} else
				lblImage.setImage(SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png"));
		} else
			lblImage.dispose();
		if (img != null)
			img.dispose();
	}
	
	public void registerPlugin(String[] args) {
		Database.registerClass(PosterAttribute.class);
		DetailMovieComposite.AddPlugin(this);
		ExtendedListItem.AddPlugin(this);
	}

	public void registerOverviewTabs(YouMDb window) { }
}
