package net.sourceforge.squirrel_sql.plugins.mssql.action;



import java.awt.event.ActionEvent;
import java.util.ArrayList;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class ScriptProcedureExecAction extends SquirrelAction implements ISessionAction {
	private ISession _session;
	private final MssqlPlugin _plugin;

	public ScriptProcedureExecAction(IApplication app, Resources rsrc, MssqlPlugin plugin) {
		super(app, rsrc);
		_plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt) {
		if (_session != null) {
			IPlugin plugin = _session.getApplication().getDummyAppPlugin();
			IObjectTreeAPI treeAPI = _session.getSessionInternalFrame().getObjectTreeAPI();
			final IDatabaseObjectInfo[] dbObjs = treeAPI.getSelectedDatabaseObjects();
            
			if (dbObjs.length > 0) {
                try {
                    new ScriptProcedureExecCommand(_session, _plugin, dbObjs).execute();
                }
                catch (Throwable th) {
                    _session.showErrorMessage(th);
                }
            }
                
		}
	}

	public void setSession(ISession session) {
		_session = session;
	}
}
