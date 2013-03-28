package net.sourceforge.squirrel_sql.fw.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MaximizeInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class MaximizeInternalFramesAction
	extends BaseAction
	implements IHasJDesktopPane
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CascadeInternalFramesAction.class);

	
	private JDesktopPane _desktop;

	
	public MaximizeInternalFramesAction()
	{
		this(null);
	}

	
	public MaximizeInternalFramesAction(JDesktopPane desktop)
	{
		super(s_stringMgr.getString("MaximizeInternalFramesAction.title"));
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
			MaximizeInternalFramePositioner pos =
				new MaximizeInternalFramePositioner();
			JInternalFrame[] children =
				GUIUtils.getOpenNonToolWindows(_desktop.getAllFrames());
			for (int i = children.length - 1; i >= 0; --i)
			{
				pos.positionInternalFrame(children[i]);
			}
		}
	}
}