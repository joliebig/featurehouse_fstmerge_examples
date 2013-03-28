package net.sourceforge.squirrel_sql.plugins.postgres.actions;


import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import java.awt.event.ActionEvent;

public abstract class AbstractObjectTreeAction extends SquirrelAction implements IObjectTreeAction {
    
    protected IObjectTreeAPI _tree;


    public AbstractObjectTreeAction(IApplication app, Resources rsrc) {
        super(app, rsrc);
    }


    public void actionPerformed(ActionEvent evt) {
        if (_tree != null) {
            IDatabaseObjectInfo[] infos = _tree.getSelectedDatabaseObjects();
            if (infos.length > 1 && !isMultipleObjectAction()) {
                _tree.getSession().showMessage(getErrorMessage());
            } else {
                try {
                    getCommand(infos).execute();
                } catch (Exception e) {
                    _tree.getSession().showMessage(e);
                }
            }
        }
    }


    protected abstract ICommand getCommand(IDatabaseObjectInfo[] info);


    protected abstract boolean isMultipleObjectAction();


    protected abstract String getErrorMessage();


    
    public void setObjectTree(IObjectTreeAPI tree) {
        _tree = tree;
        setEnabled(_tree != null);
    }
}
