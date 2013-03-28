package net.sourceforge.squirrel_sql.plugins.mssql.action;



import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class ShowStatisticsAction extends SquirrelAction implements ISessionAction {

    private static final long serialVersionUID = 1L;
    transient private ISession _session;
	transient private final MssqlPlugin _plugin;
    
    private final ITableInfo _tableInfo;
    private final String _indexName;

	public ShowStatisticsAction(IApplication app, Resources rsrc, MssqlPlugin plugin, ITableInfo tableInfo, String indexName) {
		super(app, rsrc);
		
        putValue(javax.swing.Action.NAME,indexName);
        _plugin = plugin;
        _tableInfo = tableInfo;
        _indexName = indexName;
	}

	public void actionPerformed(ActionEvent evt) {
		if (_session != null)
    		new ShowStatisticsCommand(_session, _plugin, _tableInfo, _indexName).execute();
	}

	public void setSession(ISession session) {
		_session = session;
	}
}
