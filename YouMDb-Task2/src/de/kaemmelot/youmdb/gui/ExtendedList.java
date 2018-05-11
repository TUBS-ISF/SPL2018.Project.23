package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

public class ExtendedList extends ScrolledComposite {
	private final Composite contentComposite;

	public ExtendedList(Composite parent) {
		super(parent, SWT.V_SCROLL);
		setExpandHorizontal(true);
		setExpandVertical(true);
		setLayout(new GridLayout());
		setAlwaysShowScrollBars(true);
		
		contentComposite = new Composite(this, SWT.NO_FOCUS);
		contentComposite.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		GridLayout gl_contentComposite = new GridLayout();
		gl_contentComposite.marginRight = 2;
		gl_contentComposite.marginLeft = 2;
		gl_contentComposite.marginHeight = 0;
		gl_contentComposite.marginWidth = 0;
		gl_contentComposite.verticalSpacing = 2;
		gl_contentComposite.horizontalSpacing = 2;
		contentComposite.setLayout(gl_contentComposite);
		contentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		setContent(contentComposite);
		
		contentComposite.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event event) {
				if (contentComposite.getSize().x > getSize().x)
					contentComposite.setSize(getSize().x, contentComposite.getSize().y);
			}
		});
	}
	
	public void ClearItems() {
		for (Control child : contentComposite.getChildren())
			child.dispose();
	}
	
	public Composite GetContentComposite() {
		return contentComposite;
	}
	
	public void Refresh() {
		setMinSize(getParent().getSize()); // TODO BUGFIX for not being painted
		setMinSize(null);
		setMinHeight(contentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	}
}
