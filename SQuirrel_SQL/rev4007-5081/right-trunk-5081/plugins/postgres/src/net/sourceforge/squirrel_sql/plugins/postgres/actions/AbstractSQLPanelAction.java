package net.sourceforge.squirrel_sql.plugins.postgres.actions;


import net.sourceforge.squirrel_sql.client.session.action.ISQLPanelAction;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import java.awt.event.ActionEvent;

public abstract class AbstractSQLPanelAction extends SquirrelAction implements ISQLPanelAction {
    
    protected ISQLPanelAPI _panel;


    public AbstractSQLPanelAction(IApplication app, Resources rsrc) {
        super(app, rsrc);
    }


    public void actionPerformed(ActionEvent evt) {
        if (_panel != null) {
            try {
                getCommand().execute();
            } catch (Exception e) {
                _panel.getSession().showMessage(e);
            }
        }
    }


    protected abstract ICommand getCommand();


    
    public void setSQLPanel(ISQLPanelAPI panel) {
        _panel = panel;
        setEnabled(_panel != null);
    }
}
