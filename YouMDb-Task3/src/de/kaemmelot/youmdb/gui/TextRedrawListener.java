package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.widgets.Text;

public class TextRedrawListener
		implements ModifyListener, DragDetectListener, FocusListener, MouseListener, MouseMoveListener {

	private boolean mouseDown = false;
	
	public TextRedrawListener(Text txt) {
		txt.addModifyListener(this);
		txt.addDragDetectListener(this);
		txt.addFocusListener(this);
		txt.addMouseListener(this);
		txt.addMouseMoveListener(this);
	}
	
	public void mouseMove(MouseEvent e) {
		if (mouseDown)
			((Text) e.widget).redraw();
	}

	public void mouseDoubleClick(MouseEvent e) {
	}

	public void mouseDown(MouseEvent e) {
		if ((e.button & 1) == 1) {
			mouseDown = true;
			((Text) e.widget).redraw();
		}
	}

	public void mouseUp(MouseEvent e) {
		if ((e.button & 1) == 1) {
			mouseDown = false;
			((Text) e.widget).redraw();
		}
	}

	public void focusGained(FocusEvent e) {
		((Text) e.widget).redraw();
	}

	public void focusLost(FocusEvent e) {
		((Text) e.widget).redraw();
	}

	public void dragDetected(DragDetectEvent e) {
		((Text) e.widget).redraw();
	}

	public void modifyText(ModifyEvent e) {
		((Text) e.widget).redraw();
	}

}
