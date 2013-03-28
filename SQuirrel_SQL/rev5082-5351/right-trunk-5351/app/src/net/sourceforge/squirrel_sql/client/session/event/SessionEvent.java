package net.sourceforge.squirrel_sql.client.session.event;

import java.util.EventObject;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class SessionEvent extends EventObject
{
	
	private ISession _session;

	
	public SessionEvent(ISession source)
	{
		super(checkParams(source));
		_session = source;
	}

	
	public ISession getSession()
	{
		return _session;
	}

	private static ISession checkParams(ISession source)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		return source;
	}
}
