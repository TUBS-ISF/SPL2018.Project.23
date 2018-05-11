package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.wb.swt.SWTResourceManager;

import de.kaemmelot.youmdb.FeatureConfiguration;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class ExtendedListItem extends Composite {
	private final static int IMAGE_HEIGHT = 90;
	private final static int IMAGE_WIDTH = 62;
	
	private org.eclipse.swt.graphics.Image itemImage = null;
	
	public ExtendedListItem(ExtendedList parentList, Image image, String title, String desc) {
		super(parentList.GetContentComposite(), SWT.NONE);
		final Composite parent = parentList.GetContentComposite();
		
		setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		GridLayout gridLayout;
		if (FeatureConfiguration.GetInstance().UsePosters())
			gridLayout = new GridLayout(2, false);
		else
			gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 1;
		setLayout(gridLayout);
		
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				if (getSize().x > parent.getSize().x)
					setSize(parent.getSize().x, getSize().y);
			}
		});
		
		if (FeatureConfiguration.GetInstance().UsePosters()) {
			org.eclipse.swt.graphics.Image img;
			if (image != null) {
				// TODO
				// BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
				BufferedImage bufImg = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
				bufImg.createGraphics().drawImage(image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH), 0, 0, null);
				// see https://stackoverflow.com/a/24245496
				assert bufImg.getColorModel().getPixelSize() == 24;
				ImageData data = new ImageData(bufImg.getWidth(),bufImg.getHeight(), 24,
						new PaletteData(0x0000FF, 0x00FF00, 0xFF0000), 4,
						((DataBufferByte) bufImg.getData().getDataBuffer()).getData());
				itemImage = img = new org.eclipse.swt.graphics.Image(parent.getFont().getDevice(), data);
			} else
				img = SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png");
			
			CLabel lblImage = new CLabel(this, SWT.BORDER | SWT.SHADOW_OUT);
			GridData lblImageGd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
			lblImageGd.heightHint = IMAGE_HEIGHT;
			lblImageGd.widthHint = IMAGE_WIDTH;
			lblImage.setLayoutData(lblImageGd);
			lblImage.setTopMargin(2);
			lblImage.setRightMargin(2);
			lblImage.setLeftMargin(2);
			lblImage.setBottomMargin(2);
			lblImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
			lblImage.setImage(img);
			lblImage.setText(null);
		}
		
		Label lblTitle = new Label(this, SWT.NONE);
		lblTitle.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblTitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblTitle.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblTitle.setText(title);
		
		Label lblDescription = new Label(this, SWT.WRAP);
		lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblDescription.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		lblDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblDescription.setText(desc);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (itemImage != null)
			itemImage.dispose();
	}

}
