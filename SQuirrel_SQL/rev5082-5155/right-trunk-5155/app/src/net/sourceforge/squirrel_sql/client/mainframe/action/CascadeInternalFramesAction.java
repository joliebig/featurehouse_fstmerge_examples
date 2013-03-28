package net.sourceforge.squirrel_sql.client.mainframe.action;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.mainframe.IHasJDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.mainframe.WidgetUtils;
import net.sourceforge.squirrel_sql.fw.gui.CascadeInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


public class CascadeInternalFramesAction
	extends BaseAction
	implements IHasJDesktopPane
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CascadeInternalFramesAction.class);

	
	private IDesktopContainer _desktop;
   private IApplication _app;

   
	public CascadeInternalFramesAction(IApplication app)
	{
      _app = app;
   }

   
	public void setDesktopContainer(IDesktopContainer value)
	{
		_desktop = value;
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		if (_desktop != null && _app.getDesktopStyle().isInternalFrameStyle())
		{
			Dimension cs = null; 
			CascadeInternalFramePositioner pos =
				new CascadeInternalFramePositioner();
			IWidget[] widgets =
				WidgetUtils.getOpenNonToolWindows(_desktop.getAllWidgets());
			for (int i = widgets.length - 1; i >= 0; --i)
			{
				JInternalFrame child = widgets[i].getInternalFrame();

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
