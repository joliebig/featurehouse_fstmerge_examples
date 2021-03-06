
package net.sf.jabref;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import com.jgoodies.uif_lite.panel.SimpleInternalFrame;

public abstract class SidePaneComponent extends SimpleInternalFrame {

	protected JButton close = new JButton(GUIGlobals.getImage("close"));

	protected boolean visible = false;

	protected SidePaneManager manager;

	protected BasePanel panel = null;

	public SidePaneComponent(SidePaneManager manager, URL icon, String title) {
		super(new ImageIcon(icon), title);
		this.manager = manager;
		setSelected(true);
		JToolBar tlb = new JToolBar();
		close.setMargin(new Insets(0, 0, 0, 0));
		
		close.setBorder(null);
		tlb.add(close);
		close.addActionListener(new CloseButtonListener());
		setToolBar(tlb);
		

		
		
		
		
	}

	public void hideAway() {
		manager.hideComponent(this);
	}

	
	void setVisibility(boolean vis) {
		visible = vis;
	}

	
	boolean hasVisibility() {
		return visible;
	}

	public void setActiveBasePanel(BasePanel panel) {
		this.panel = panel;
	}

	public BasePanel getActiveBasePanel() {
		return panel;
	}

	
	public void componentClosing() {

	}

	
	public void componentOpening() {

	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	class CloseButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			hideAway();
		}
	}
}
