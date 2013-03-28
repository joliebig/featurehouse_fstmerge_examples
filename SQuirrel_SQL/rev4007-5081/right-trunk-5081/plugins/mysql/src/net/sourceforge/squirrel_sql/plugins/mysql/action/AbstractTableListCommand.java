package net.sourceforge.squirrel_sql.plugins.mysql.action;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

abstract class AbstractTableListCommand implements ICommand
{
	
	private ISession _session;

	
	private final MysqlPlugin _plugin;

	
	public AbstractTableListCommand(ISession session, MysqlPlugin plugin)
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
		final IObjectTreeAPI api = _session.getSessionInternalFrame().getObjectTreeAPI();
		final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

		
		StringBuffer tableList = new StringBuffer(512);
		for (int i = 0; i < dbObjs.length; ++i)
		{
			tableList.append(dbObjs[i].getQualifiedName()).append(",");
		}
		if (tableList.length() > 0)
		{
			tableList.setLength(tableList.length() - 1); 
		}

		
		final StringBuffer cmd = new StringBuffer(512);
		cmd.append(getMySQLCommand())
			.append(' ')
			.append(tableList);
		final String cmdStr = checkSQL(cmd.toString());
		if (cmdStr != null && cmdStr.length() > 0)
		{
			_session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(cmdStr, true);
			_session.getSessionInternalFrame().getSQLPanelAPI().executeCurrentSQL();
			_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		}
	}

	
	protected String checkSQL(String sql)
	{
		return sql;
	}

	
	protected abstract String getMySQLCommand();
}
