package net.sourceforge.squirrel_sql.plugins.mysql.action;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class RenameTableCommand
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RenameTableCommand.class);

	
	private final ISession _session;

	
	private final IPlugin _plugin;

	
	private final ITableInfo _ti;

	
	private final String _newTableName;

	
	RenameTableCommand(ISession session, IPlugin plugin, ITableInfo ti,
						String newTableName)
	{
		super();

		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (plugin == null)
		{
			throw new IllegalArgumentException("IPlugin == null");
		}
		if (ti == null)
		{
			throw new IllegalArgumentException("ITableInfo == null");
		}
		if (newTableName == null || newTableName.length() == 0)
		{
			throw new IllegalArgumentException("New table name empty");
		}

		_session = session;
		_plugin = plugin;
		_ti = ti;
		_newTableName = newTableName;
	}

	public void execute()
	{
		String cmd = "rename table " + _ti.getQualifiedName() + " to " + _newTableName;
      SQLExecuterTask executer = new SQLExecuterTask(_session, cmd, new DefaultSQLExecuterHandler(_session));
      executer.run();
      _session.getSchemaInfo().reloadAllTables();
	}
}
