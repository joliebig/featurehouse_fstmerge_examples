package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.gui.action.SelectInternalFrameAction;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.DriversListInternalFrame;

import java.awt.event.ActionEvent;


public class ViewDriversAction extends SelectInternalFrameAction
{
   private DriversListInternalFrame m_window;

   
	public ViewDriversAction(IApplication app, DriversListInternalFrame window)
	{
		super(window);
		if (app == null)
		{
			throw new IllegalArgumentException("null IApplication passed");
		}
		if (window == null)
		{
			throw new IllegalArgumentException("null DriversToolWindow passed");
		}

      m_window = window;
      app.getResources().setupAction(this, app.getSquirrelPreferences().getShowColoriconsInToolbar());
	}

   public void actionPerformed(ActionEvent evt)
   {
      super.actionPerformed(evt);

      if(null != m_window)
      {
         m_window.nowVisible(true);
      }
   }

}
