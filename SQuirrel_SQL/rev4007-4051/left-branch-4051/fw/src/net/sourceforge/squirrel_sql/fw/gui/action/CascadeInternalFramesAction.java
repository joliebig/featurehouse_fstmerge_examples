package net.sourceforge.squirrel_sql.fw.gui.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.gui.CascadeInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class CascadeInternalFramesAction
	extends BaseAction
	implements IHasJDesktopPane
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CascadeInternalFramesAction.class);

	
	private JDesktopPane _desktop;

	
	public CascadeInternalFramesAction()
	{
		this(null);
	}

	
	public CascadeInternalFramesAction(JDesktopPane desktop)
	{
		super(s_stringMgr.getString("CascadeInternalFramesAction.title"));
		setJDesktopPane(desktop);
	}

	
	public void setJDesktopPane(JDesktopPane value)
	{
		_desktop = value;
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		if (_desktop != null)
		{
			Dimension cs = null; 
			CascadeInternalFramePositioner pos =
				new CascadeInternalFramePositioner();
			JInternalFrame[] children =
				GUIUtils.getOpenNonToolWindows(_desktop.getAllFrames());
			for (int i = children.length - 1; i >= 0; --i)
			{
				JInternalFrame child = children[i];
				if (cs == null && child.getParent() != null)
				{
					cs = child.getParent().getSize();
					
					
					cs.setSize(
						(int) (cs.width * 0.8d),
						(int) (cs.height * 0.8d));
				}
				if (cs != null)
				{
					child.setSize(cs);
					pos.positionInternalFrame(child);
				}
			}
		}
	}
}
