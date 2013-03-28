package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.gui.action.SelectInternalFrameAction;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.AliasesListInternalFrame;

import java.awt.event.ActionEvent;


public class ViewAliasesAction extends SelectInternalFrameAction
{
   private AliasesListInternalFrame m_window;

   
	public ViewAliasesAction(IApplication app, AliasesListInternalFrame window)
	{
		super(window);
		if (window == null)
		{
			throw new IllegalArgumentException("null AliasesToolWindow passed");
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
