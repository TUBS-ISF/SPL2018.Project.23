package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;

public interface ExtendedListItemPlugin {
	ExtendedListItemAnchor getExtendedListItemAnchor();
	
	int getExtendedListItemWidth();
	
	int getExtendedListItemHeight();
	
	void addExtendedListItemContent(int height, Composite parent, ExtendedListItem _this);
	
	void movieChanged(ExtendedListItem _this);
}
