package net.sourceforge.squirrel_sql.client.session.event;

import java.util.EventObject;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLPanel;

public class SQLPanelEvent extends EventObject
{
	
	private final ISession _session;

	
	public SQLPanelEvent(ISession session, SQLPanel source)
	{
		super(checkParams(session, source));
		_session = session;
	}

	
	public ISession getSession()
	{
		return _session;
	}

	
	public ISQLPanelAPI getSQLPanel()
	{
		return ((SQLPanel)getSource()).getSQLPanelAPI();
	}

	private static SQLPanel checkParams(ISession session, SQLPanel source)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (source == null)
		{
			throw new IllegalArgumentException("SQLPanel == null");
		}
		return source;
	}
}