package net.sourceforge.squirrel_sql.client.session.event;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.ISQLResultExecuter;

public class SQLResultExecuterTabEvent
{
	private ISession _session;
	private ISQLResultExecuter _tab;

	public SQLResultExecuterTabEvent(ISession session, ISQLResultExecuter tab)
			throws IllegalArgumentException
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ExecuterTab passed");
		}
		_session = session;
		_tab = tab;
	}

	public ISession getSession()
	{
		return _session;
	}

	public ISQLResultExecuter getExecuter()
	{
		return _tab;
	}
}