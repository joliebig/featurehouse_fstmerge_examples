package net.sourceforge.squirrel_sql.client.session.event;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;

public class ResultTabEvent
{
	private ISession _session;
	private IResultTab _tab;

	public ResultTabEvent(ISession session, IResultTab tab)
		throws IllegalArgumentException
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		if (tab == null)
		{
			throw new IllegalArgumentException("Null ResultTab passed");
		}
		_session = session;
		_tab = tab;
	}

	public ISession getSession()
	{
		return _session;
	}

	public IResultTab getResultTab()
	{
		return _tab;
	}
}
