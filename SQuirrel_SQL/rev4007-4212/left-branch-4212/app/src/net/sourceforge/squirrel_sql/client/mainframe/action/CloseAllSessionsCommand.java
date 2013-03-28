package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class CloseAllSessionsCommand implements ICommand
{
	
	private final IApplication _app;

	
	public CloseAllSessionsCommand(IApplication app)
		throws IllegalArgumentException
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("IApplication == null");
		}

		_app = app;
	}

	
	public void execute()
	{
		_app.getSessionManager().closeAllSessions();
	}
}
