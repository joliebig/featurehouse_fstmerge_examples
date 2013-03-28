
package net.sourceforge.squirrel_sql.plugins.firebirdmanager.gui.comp;

import java.awt.Cursor;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.Border;


public class FBButton extends JButton {
	private static final long serialVersionUID = -8734222381715897845L;

	public FBButton() {
		super();
		initButton(false);
	}

	public FBButton(Action a) {
		super(a);
		initButton(false);
	}

	public FBButton(Icon icon) {
		super(icon);
		initButton(false);
	}

	public FBButton(String text, Icon icon) {
		super(text, icon);
		initButton(false);
	}

	public FBButton(String text) {
		super(text);
		initButton(false);
	}

	public FBButton(boolean withdefaultBorder) {
		super();
		initButton(withdefaultBorder);
	}

	private void initButton(boolean withdefaultBorder) {
		if (!withdefaultBorder) {
			Border borderButton = BorderFactory.createEmptyBorder(5, 5, 5, 5);
			this.setBorder(borderButton);
		}
		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}
}
