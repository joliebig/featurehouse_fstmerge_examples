package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class CloseAllSQLResultWindowsAction extends SquirrelAction
{
   
	public CloseAllSQLResultWindowsAction(IApplication app)
	{
		super(app);
	}

	
	public void actionPerformed(ActionEvent evt)
	{
		IApplication app = getApplication();
		CursorChanger cursorChg = new CursorChanger(app.getMainFrame());
		cursorChg.show();
		try
		{
         ISession activeSession = getApplication().getSessionManager().getActiveSession();
         if(   activeSession.getActiveSessionWindow() instanceof SessionInternalFrame
            || activeSession.getActiveSessionWindow() instanceof SQLInternalFrame)
         {
            
            
			   new CloseAllSQLResultWindowsCommand(activeSession.getSQLPanelAPIOfActiveSessionWindow()).execute();
         }
		}
		finally
		{
			cursorChg.restore();
		}
	}
}
