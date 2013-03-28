package net.sourceforge.squirrel_sql.plugins.mysql.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

public class AlterTableAction extends SquirrelAction implements ISessionAction
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AlterTableAction.class);


	private ISession _session;

	
	private final MysqlPlugin _plugin;

	public AlterTableAction(IApplication app, Resources rsrc, MysqlPlugin plugin)
	{
		super(app, rsrc);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			IObjectTreeAPI treeAPI = _session.getSessionInternalFrame().getObjectTreeAPI();
			IDatabaseObjectInfo[] tables = treeAPI.getSelectedDatabaseObjects();
			if (tables.length == 1 && tables[0] instanceof ITableInfo)
			{
				try
				{

					new AlterTableCommand(_session, _plugin, (ITableInfo)tables[0]).execute();
				}
				catch (Throwable th)
				{
					_session.showErrorMessage(th);
				}
			}
			else
			{
				String msg = s_stringMgr.getString("AlterTableAction.error.wrongobjcount");
				_session.showErrorMessage(msg);
			}
		}
	}

	
	public void setSession(ISession session)
	{
		_session = session;
	}
}
