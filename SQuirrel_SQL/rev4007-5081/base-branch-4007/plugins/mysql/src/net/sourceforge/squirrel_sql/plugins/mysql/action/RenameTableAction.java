package net.sourceforge.squirrel_sql.plugins.mysql.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;

import net.sourceforge.squirrel_sql.plugins.mysql.MysqlPlugin;

public class RenameTableAction extends SquirrelAction
								implements ISessionAction
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RenameTableAction.class);

	
	private final static ILogger s_log =
		LoggerController.createLogger(RenameTableAction.class);

	
	private ISession _session;

	
	private final MysqlPlugin _plugin;

	public RenameTableAction(IApplication app, Resources rsrc,MysqlPlugin plugin)
	{
		super(app, rsrc);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt)
	{
		if (_session != null)
		{
			final IObjectTreeAPI treeAPI = _session.getSessionInternalFrame().getObjectTreeAPI();
			final IDatabaseObjectInfo[] tables = treeAPI.getSelectedDatabaseObjects();
			if (tables.length == 1)
			{
				final ITableInfo ti = (ITableInfo)tables[0];
				final String msg = s_stringMgr.getString("RenameTableAction.newnameprompt", ti.getQualifiedName());
				final String title = s_stringMgr.getString("RenameTableAction.rename");
				final String newTableName = JOptionPane.showInputDialog(null, msg, title, JOptionPane.QUESTION_MESSAGE);
				if (newTableName != null && newTableName.length() > 0)
				{
					try
					{
						new RenameTableCommand(_session, _plugin, ti, newTableName).execute();
					}
					catch (Throwable th)
					{
						_session.showErrorMessage(th);
						s_log.error("Error occured renaming table", th);
					}
				}
			}
			else
			{
				
				_session.getApplication().showErrorDialog(s_stringMgr.getString("mysql.selectSingleTable"));
			}
		}
	}

	
	public void setSession(ISession session)
	{
		_session = session;
	}
}
