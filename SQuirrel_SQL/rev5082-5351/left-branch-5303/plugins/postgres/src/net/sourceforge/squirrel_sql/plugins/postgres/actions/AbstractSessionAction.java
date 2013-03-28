package net.sourceforge.squirrel_sql.plugins.postgres.actions;


import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import java.awt.event.ActionEvent;

public abstract class AbstractSessionAction extends SquirrelAction implements ISessionAction {
    
    protected ISession _session;


    public AbstractSessionAction(IApplication app, Resources rsrc) {
        super(app, rsrc);
    }


    public void actionPerformed(ActionEvent evt) {
        if (_session != null) {
            try {
                getCommand().execute();
            } catch (Exception e) {
                _session.showMessage(e);
            }
        }
    }


    protected abstract ICommand getCommand();


    
    public void setSession(ISession session) {
        _session = session;
        setEnabled(_session != null);
    }
}
