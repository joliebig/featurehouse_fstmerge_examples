package net.sourceforge.squirrel_sql.plugins.mysql.action;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

abstract class AbstractMultipleSQLCommand implements ICommand
{
	
	private ISession _session;

	
	private final MysqlPlugin _plugin;

	
	public AbstractMultipleSQLCommand(ISession session, MysqlPlugin plugin)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("MysqlPlugin == null");
		}

		_session = session;
		_plugin = plugin;
	}

	
	public void execute()
	{
		final StringBuffer buf = new StringBuffer(2048);
		final String sep = " " + _session.getQueryTokenizer().getSQLStatementSeparator();

		final IObjectTreeAPI api = _session.getSessionInternalFrame().getObjectTreeAPI();
		final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

		for (int i = 0; i < dbObjs.length; ++i)
		{
			final String cmd = getMySQLCommand(dbObjs[i]);
			if (cmd != null && cmd.length() > 0)
			{
				buf.append(cmd).append(" ").append(sep).append('\n');
			}
		}

		
		if (buf.length() > 0)
		{
			_session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(buf.toString(), true);
			_session.getSessionInternalFrame().getSQLPanelAPI().executeCurrentSQL();
			_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		}
	}

	
	protected abstract String getMySQLCommand(IDatabaseObjectInfo dbObj);
}
