package de.kaemmelot.youmdb.gui;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.kaemmelot.youmdb.models.ActorRolePair;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class ActorRoleDialog extends Dialog {
	private Text actor;
	private Text role;
	
	protected Shell shell;

	public ActorRoleDialog(Shell parent) {
		super(parent, SWT.NONE);
		setText("Actor - Role");
	}

	public ActorRolePair open(int order) {
		return open(new ActorRolePair("", "", order));
	}
	
	public ActorRolePair open(ActorRolePair arp) {
		createContents(arp);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return arp;
	}

	private void createContents(final ActorRolePair arp) {
		shell = new Shell(getParent(), SWT.BORDER | SWT.TITLE | SWT.ON_TOP | SWT.FOCUSED); // TODO how to keep focused?
		shell.setMinimumSize(new Point(450, 300));
		shell.setSize(450, 300);
		shell.setText(getText());
		GridLayout gl_shell = new GridLayout(3, false);
		gl_shell.verticalSpacing = 0;
		gl_shell.marginWidth = 0;
		gl_shell.marginHeight = 0;
		gl_shell.horizontalSpacing = 0;
		shell.setLayout(gl_shell);
		
		Label actorLbl = new Label(shell, SWT.NONE);
		actorLbl.setText("Actor:");
		actorLbl.setAlignment(SWT.RIGHT);
		
		actor = new Text(shell, SWT.BORDER);
		actor.setText(arp.getActor());
		actor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label roleLbl = new Label(shell, SWT.NONE);
		roleLbl.setText("Role:");
		roleLbl.setAlignment(SWT.RIGHT);
		
		role = new Text(shell, SWT.BORDER);
		role.setText(arp.getRole());
		role.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Button btnAbort = new Button(shell, SWT.NONE);
		btnAbort.setText("Abort");
		btnAbort.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				ActorRoleDialog.this.shell.close();
			}
			
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		btnOk.setText("OK");
		btnOk.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}
			public void mouseDown(MouseEvent e) {
				arp.setActor(actor.getText());
				arp.setRole(role.getText());
				ActorRoleDialog.this.shell.close();
			}
			public void mouseDoubleClick(MouseEvent e) {
			}
		});
		
		new Label(shell, SWT.NONE); // placeholder
	}
}
