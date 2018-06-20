package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Composite;

public interface DetailMovieCompositePlugin {
	/**
	 * @return the anchor on the DetailMovieComposite
	 */
	DetailMovieCompositeAnchor getDetailMovieCompositeAnchor();
	
	/**
	 * @return width or -1 if dependent on others
	 */
	int getDetailMovieCompositeWidth();
	
	/**
	 * @return height or -1 if dependent on others
	 */
	int getDetailMovieCompositeHeight();
	
	/**
	 * @return the height used
	 */
	int addDetailMovieCompositeContent(int othersWidth, int othersHeight, Composite parent, DetailMovieComposite _this);
	
	void editableChanged(DetailMovieComposite _this);
	
	void movieChanged(DetailMovieComposite _this);
}
