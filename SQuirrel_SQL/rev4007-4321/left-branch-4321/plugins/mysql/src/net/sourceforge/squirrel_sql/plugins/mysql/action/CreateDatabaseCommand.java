package net.sourceforge.squirrel_sql.plugins.mysql.action;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;

public class CreateDatabaseCommand implements ICommand
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(CreateDatabaseCommand.class);


	
	private ISession _session;

	
	private final MysqlPlugin _plugin;

	
	public CreateDatabaseCommand(ISession session, MysqlPlugin plugin)
	{
		super();
		_session = session;
		_plugin = plugin;
	}

	public void execute()
	{
		
		String dbName = JOptionPane.showInputDialog(s_stringMgr.getString("mysql.enterDbName"));
		if (dbName != null)
		{
			final StringBuffer buf = new StringBuffer();
			buf.append("create database ").append(dbName);
			_session.getSessionInternalFrame().getSQLPanelAPI().executeSQL(buf.toString());
			IObjectTreeAPI api = _session.getSessionInternalFrame().getObjectTreeAPI();
			api.refreshTree();
		}

	}
}
