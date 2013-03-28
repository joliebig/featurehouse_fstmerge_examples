package net.sourceforge.squirrel_sql.plugins.dbcopy.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.commands.CopyTableCommand;

public class CopyTableAction extends SquirrelAction
                                     implements ISessionAction {

	
    private ISession _session;

	
	private final DBCopyPlugin _plugin;

    public CopyTableAction(IApplication app, 
                           Resources rsrc,
                           DBCopyPlugin plugin) 
    {
        super(app, rsrc);
        _plugin = plugin;
    }

    public void actionPerformed(ActionEvent evt) {
        if (_session != null) {
            new CopyTableCommand(_session, _plugin).execute();
        }
    }

	
    public void setSession(ISession session) {
        _session = session;
    }
}