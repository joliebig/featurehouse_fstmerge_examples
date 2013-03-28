package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class CloseSessionCommand implements ICommand
{
	
	private ISession _session;

	
	public CloseSessionCommand(ISession session)
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
		_session.getApplication().getSessionManager().closeSession(_session);
	}
}
