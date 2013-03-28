package net.sourceforge.squirrel_sql.plugins.mysql.action;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;


public class DropDatabaseCommand implements ICommand
{
	
	private ISession _session;

	
	private final MysqlPlugin _plugin;

	
	private final IDatabaseObjectInfo[] _dbs;

	
	public DropDatabaseCommand(ISession session, MysqlPlugin plugin,
									IDatabaseObjectInfo[] dbs)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("IClientSession == null");
		}
		if (dbs == null)
		{
			throw new IllegalArgumentException("Databases array is null");
		}

		_session = session;
		_plugin = plugin;
		_dbs = dbs;
	}

	public void execute()
	{
		if (_dbs.length > 0)
		{
			final String sqlSep = 
                _session.getQueryTokenizer().getSQLStatementSeparator();
			final StringBuffer buf = new StringBuffer();
			for (int i = 0; i < _dbs.length; i++)
			{
				final IDatabaseObjectInfo ti = _dbs[i];
				buf.append("drop database ")
					.append(ti.getQualifiedName())
					.append(" ")
					.append(sqlSep)
					.append('\n');
			}
         SQLExecuterTask executer = new SQLExecuterTask(_session, buf.toString(), new DefaultSQLExecuterHandler(_session));
         executer.run();
		}
	}
}
