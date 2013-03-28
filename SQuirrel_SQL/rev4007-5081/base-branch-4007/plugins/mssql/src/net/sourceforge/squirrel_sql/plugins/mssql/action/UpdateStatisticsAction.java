package net.sourceforge.squirrel_sql.plugins.mssql.action;



import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class UpdateStatisticsAction extends SquirrelAction implements ISessionAction {
    private static final long serialVersionUID = 1L;

    transient private ISession _session;
	transient private final MssqlPlugin _plugin;
    
	public UpdateStatisticsAction(IApplication app, Resources rsrc, MssqlPlugin plugin) {
		super(app, rsrc);
        _plugin = plugin;
	}

	public void actionPerformed(ActionEvent evt) {
		if (_session != null)
    		new UpdateStatisticsCommand(_session, _plugin).execute();
	}

	public void setSession(ISession session) {
		_session = session;
	}
}
