package net.sf.jabref.gui.components;

import java.awt.*;

public class JPanelXBoxPreferredSize extends JPanelXBox {
	public JPanelXBoxPreferredSize() {
		
	}
	public JPanelXBoxPreferredSize(Component c) {
		add(c);
	}
	public Dimension getMaximumSize() {
		return getPreferredSize();
	}
}

