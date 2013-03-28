
package net.sourceforge.squirrel_sql.plugins.smarttools.comp;

import java.awt.Cursor;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.border.Border;


public class STButton extends JButton {
	private static final long serialVersionUID = -1504866017961154906L;

	public STButton() {
		super();
		initButton(false);
	}

	public STButton(Action a) {
		super(a);
		initButton(false);
	}

	public STButton(Icon icon) {
		super(icon);
		initButton(false);
	}

	public STButton(String text, Icon icon) {
		super(text, icon);
		initButton(false);
	}

	public STButton(String text) {
		super(text);
		initButton(false);
	}

	public STButton(boolean withdefaultBorder) {
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
