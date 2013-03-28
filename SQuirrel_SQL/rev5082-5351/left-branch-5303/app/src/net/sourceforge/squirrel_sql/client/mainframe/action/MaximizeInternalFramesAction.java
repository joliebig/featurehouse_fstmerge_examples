package net.sourceforge.squirrel_sql.client.mainframe.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.MaximizeInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.client.gui.mainframe.IHasJDesktopPane;
import net.sourceforge.squirrel_sql.client.gui.mainframe.WidgetUtils;
import net.sourceforge.squirrel_sql.client.mainframe.action.CascadeInternalFramesAction;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IDesktopContainer;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.IWidget;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DesktopStyle;
import net.sourceforge.squirrel_sql.client.IApplication;


public class MaximizeInternalFramesAction
	extends BaseAction
	implements IHasJDesktopPane
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CascadeInternalFramesAction.class);

	
	private IDesktopContainer _desktop;
   private IApplication _app;

   
	public MaximizeInternalFramesAction(IApplication app)
	{
      super(s_stringMgr.getString("MaximizeInternalFramesAction.title"));
      _app = app;
   }

   
	public void setDesktopContainer(IDesktopContainer value)
	{
		_desktop = value;
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		if (_desktop != null  && _app.getDesktopStyle().isInternalFrameStyle())
		{
			MaximizeInternalFramePositioner pos =
				new MaximizeInternalFramePositioner();
			IWidget[] widgets = WidgetUtils.getOpenNonToolWindows(_desktop.getAllWidgets());
			for (int i = widgets.length - 1; i >= 0; --i)
			{
				pos.positionInternalFrame(widgets[i].getInternalFrame());
			}
		}
	}
}
