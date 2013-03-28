package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.ISessionWidget;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;


public class CloseSessionWindowCommand implements ICommand
{
	
	private ISession _session;

	
	public CloseSessionWindowCommand(ISession session)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
	}

	
	public void execute()
	{
      ISessionWidget activeSessionWindow = _session.getActiveSessionWindow();
      if(activeSessionWindow instanceof SQLInternalFrame || activeSessionWindow instanceof ObjectTreeInternalFrame)
      {
         activeSessionWindow.closeFrame(true);
      }
      else
      {
   		_session.getApplication().getSessionManager().closeSession(_session);
      }
	}
}