package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

/**
 * @deprecated THIS IS JUST A DRAFT. Use {@link ExtendedListItem} instead.
 */
public class ExtendedListItemDraft extends Composite {
	public ExtendedListItemDraft(final Composite parent, int style) {
		super(parent, SWT.BORDER);
		setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 1;
		setLayout(gridLayout);
		
		addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				if (getSize().x > parent.getSize().x)
					setSize(parent.getSize().x, getSize().y);
			}
		});
		
		org.eclipse.swt.graphics.Image img = SWTResourceManager.getImage(ExtendedListItem.class, "/resources/noImage_small.png");
		
		CLabel lblImage = new CLabel(this, SWT.BORDER | SWT.SHADOW_OUT);
		GridData gd_lblImage = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 2);
		gd_lblImage.widthHint = 62;
		gd_lblImage.heightHint = 90;
		lblImage.setLayoutData(gd_lblImage);
		lblImage.setTopMargin(2);
		lblImage.setBottomMargin(2);
		lblImage.setRightMargin(2);
		lblImage.setLeftMargin(2);
		lblImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblImage.setImage(img);
		lblImage.setText(null);
		
		Label lblTitle = new Label(this, SWT.NONE);
		lblTitle.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblTitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblTitle.setFont(SWTResourceManager.getFont("Segoe UI", 10, SWT.BOLD));
		lblTitle.setText("Title");
		
		Label lblDescription = new Label(this, SWT.WRAP);
		lblDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblDescription.setFont(SWTResourceManager.getFont("Segoe UI", 8, SWT.NORMAL));
		lblDescription.setBackground(SWTResourceManager.getColor(SWT.COLOR_TRANSPARENT));
		lblDescription.setText("Description");
	}
}
