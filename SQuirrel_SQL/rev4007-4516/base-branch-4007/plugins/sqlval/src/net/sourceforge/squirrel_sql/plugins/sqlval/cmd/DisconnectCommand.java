package net.sourceforge.squirrel_sql.plugins.sqlval.cmd;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.session.ISession;

import net.sourceforge.squirrel_sql.plugins.sqlval.WebServicePreferences;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSession;
import net.sourceforge.squirrel_sql.plugins.sqlval.WebServiceSessionProperties;

public class DisconnectCommand implements ICommand
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DisconnectCommand.class);


	private final ISession _session;
	private final WebServicePreferences _prefs;
	private final WebServiceSessionProperties _sessionProps;

	public DisconnectCommand(ISession session, WebServicePreferences prefs,
				WebServiceSessionProperties sessionProps)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		if (sessionProps == null)
		{
			throw new IllegalArgumentException("WebServiceSessionProperties == null");
		}

		_session = session;
		_prefs = prefs;
		_sessionProps = sessionProps;
	}

	
	public void execute() throws BaseException
	{
		try
		{
			final WebServiceSession wss = _sessionProps.getWebServiceSession();
			if (wss.isOpen())
			{
				wss.close();
				
				_session.showMessage(s_stringMgr.getString("sqlval.disconnected"));
			}
		}
		catch (Throwable th)
		{
			throw new BaseException(th);
		}
	}
}

