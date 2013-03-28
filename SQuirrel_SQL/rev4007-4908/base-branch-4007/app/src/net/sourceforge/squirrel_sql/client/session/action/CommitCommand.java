package net.sourceforge.squirrel_sql.client.session.action;

import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class CommitCommand implements ICommand
{
	
	private ISession _session;

	
	public CommitCommand(ISession session)
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
		_session.commit();
	}
}
