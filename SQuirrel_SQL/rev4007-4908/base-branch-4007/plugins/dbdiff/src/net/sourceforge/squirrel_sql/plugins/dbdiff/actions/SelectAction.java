package net.sourceforge.squirrel_sql.plugins.dbdiff.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.dbdiff.DBDiffPlugin;
import net.sourceforge.squirrel_sql.plugins.dbdiff.commands.SelectCommand;

public class SelectAction extends SquirrelAction
                                     implements ISessionAction {

	
    private ISession _session;

	
	private final DBDiffPlugin _plugin;

    public SelectAction(IApplication app, 
                              Resources rsrc,
                              DBDiffPlugin plugin) 
    {
        super(app, rsrc);
        _plugin = plugin;
    }

    public void actionPerformed(ActionEvent evt) {
        if (_session != null) {
            new SelectCommand(_session, _plugin).execute();
        }
    }

	
    public void setSession(ISession session) {
        _session = session;
    }
}