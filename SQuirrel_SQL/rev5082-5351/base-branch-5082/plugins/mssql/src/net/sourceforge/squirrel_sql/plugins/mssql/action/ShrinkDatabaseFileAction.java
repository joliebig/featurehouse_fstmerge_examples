package net.sourceforge.squirrel_sql.plugins.mssql.action;



import java.awt.event.ActionEvent;

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
import net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile.DatabaseFile;

public class ShrinkDatabaseFileAction extends SquirrelAction implements ISessionAction {
	private ISession _session;
	private final MssqlPlugin _plugin;
    private String _catalogName;
    private DatabaseFile _databaseFile;

	public ShrinkDatabaseFileAction(IApplication app, Resources rsrc, MssqlPlugin plugin, String catalogName, DatabaseFile databaseFile) {
		super(app, rsrc);
        
        
        putValue(javax.swing.Action.NAME,databaseFile.getName() + " (" + databaseFile.getSize() + ")");
        
		_plugin = plugin;
        _catalogName = catalogName;
        _databaseFile = databaseFile;
	}

	public void actionPerformed(ActionEvent evt) {
		if (_session != null) {
			new ShrinkDatabaseFileCommand(_session, _plugin, _catalogName, _databaseFile.getName()).execute();
		}
	}

	public void setSession(ISession session) {
		_session = session;
	}
}
