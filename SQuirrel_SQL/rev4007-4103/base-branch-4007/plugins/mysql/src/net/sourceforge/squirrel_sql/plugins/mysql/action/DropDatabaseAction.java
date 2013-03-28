package net.sourceforge.squirrel_sql.plugins.mysql.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;

public class DropDatabaseAction	extends SquirrelAction
								implements ISessionAction
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DropDatabaseAction.class);


	
	
	private static final String TITLE = s_stringMgr.getString("mysql.droppingDBs");

	
	
	private static final String MSG = s_stringMgr.getString("mysql.sureDropping");

	
	private ISession _session;

	
	private final MysqlPlugin _plugin;

	public DropDatabaseAction(IApplication app, Resources rsrc, MysqlPlugin plugin)
	{
		super(app, rsrc);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			IPlugin plugin = _session.getApplication().getDummyAppPlugin();
			IObjectTreeAPI treeAPI = _session.getSessionInternalFrame().getObjectTreeAPI();
			IDatabaseObjectInfo[] dbs = treeAPI.getSelectedDatabaseObjects();
			ObjectTreeNode[] nodes = treeAPI.getSelectedNodes();
			if (dbs.length > 0)
			{
				if (Dialogs.showYesNo(_session.getSessionSheet(), MSG, TITLE))
				{
					try
					{
						new DropDatabaseCommand(_session, _plugin, dbs).execute();
						treeAPI.removeNodes(nodes);
					}
					catch (Throwable th)
					{
						_session.showErrorMessage(th);
					}
				}
			}
		}
	}

	
	public void setSession(ISession session)
	{
		_session = session;
	}
}
