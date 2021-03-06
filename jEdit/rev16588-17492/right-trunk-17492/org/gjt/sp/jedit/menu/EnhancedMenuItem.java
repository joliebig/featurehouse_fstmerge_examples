

package org.gjt.sp.jedit.menu;


import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import org.gjt.sp.jedit.*;
import org.gjt.sp.jedit.gui.StatusBar;



public class EnhancedMenuItem extends JMenuItem
{
	
	
	public EnhancedMenuItem(String label, String action, ActionContext context)
	{
		this.action = action;
		this.shortcut = GUIUtilities.getShortcutLabel(action);
		String toolTip = jEdit.getProperty(action+ ".tooltip");
		if (toolTip != null) {
			setToolTipText(toolTip);
		}

		if(OperatingSystem.hasScreenMenuBar() && shortcut != null)
		{
			setText(label + " (" + shortcut + ")");
			shortcut = null;
		}
		else
			setText(label);

		if(action != null)
		{
			setEnabled(true);
			addActionListener(new EditAction.Wrapper(context,action));
			addMouseListener(new MouseHandler());
		}
		else
			setEnabled(false);
	} 

	
	public Dimension getPreferredSize()
	{
		Dimension d = super.getPreferredSize();

		if(shortcut != null)
		{
			d.width += (getFontMetrics(acceleratorFont)
				.stringWidth(shortcut) + 15);
		}
		return d;
	} 

	
	public void paint(Graphics g)
	{
		super.paint(g);

		if(shortcut != null)
		{
			g.setFont(acceleratorFont);
			g.setColor(getModel().isArmed() ?
				acceleratorSelectionForeground :
				acceleratorForeground);
			FontMetrics fm = g.getFontMetrics();
			Insets insets = getInsets();
			g.drawString(shortcut,getWidth() - (fm.stringWidth(
				shortcut) + insets.right + insets.left + 5),
				getFont().getSize() + (insets.top - 
				(OperatingSystem.isMacOSLF() ? 0 : 1))
				);
		}
	} 

	
	static Font acceleratorFont;
	static Color acceleratorForeground;
	static Color acceleratorSelectionForeground;
	

	

	
	private String shortcut;
	private String action;
	

	
	static
	{
		String shortcutFont;
		if (OperatingSystem.isMacOSLF())
		{
			shortcutFont = "Lucida Grande";
		}
		else
		{
			shortcutFont = "Monospaced";
		}
		
		acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");
		if(acceleratorFont == null)
		{
			acceleratorFont = new Font(shortcutFont,Font.PLAIN,12);
		}
		acceleratorForeground = UIManager
			.getColor("MenuItem.acceleratorForeground");
		if(acceleratorForeground == null)
		{
			acceleratorForeground = Color.black;
		}

		acceleratorSelectionForeground = UIManager
			.getColor("MenuItem.acceleratorSelectionForeground");
		if(acceleratorSelectionForeground == null)
		{
			acceleratorSelectionForeground = Color.black;
		}
	} 

	

	
	class MouseHandler extends MouseAdapter
	{
		boolean msgSet = false;
		private String msg;

		public void mouseReleased(MouseEvent evt)
		{
			cleanupStatusBar(evt);
		}

		public void mouseEntered(MouseEvent evt)
		{
			msg = jEdit.getProperty(action + ".mouse-over");
			if(msg != null)
			{
				GUIUtilities.getView((Component)evt.getSource())
					.getStatus().setMessage(msg);
				msgSet = true;
			}
		}

		public void mouseExited(MouseEvent evt)
		{
			cleanupStatusBar(evt);
		}

		private void cleanupStatusBar(MouseEvent evt)
		{
			if(msgSet)
			{
				StatusBar statusBar = GUIUtilities.getView((Component) evt.getSource())
					.getStatus();
				if (msg == statusBar.getMessage())
				{
					statusBar.setMessage(null);
				}
				msgSet = false;
				msg = null;
			}
		}
	} 
}
