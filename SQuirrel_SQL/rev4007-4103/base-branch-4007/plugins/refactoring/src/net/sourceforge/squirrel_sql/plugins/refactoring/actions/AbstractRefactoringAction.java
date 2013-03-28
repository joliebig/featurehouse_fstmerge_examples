package net.sourceforge.squirrel_sql.plugins.refactoring.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;

public abstract class AbstractRefactoringAction extends SquirrelAction
                                                implements ISessionAction {

	
    protected ISession _session;
    
    
    protected IObjectTreeAPI _tree;
    
        
    public AbstractRefactoringAction(IApplication app, 
                                     Resources rsrc) 
    {
        super(app, rsrc); 
    }

    public void actionPerformed(ActionEvent evt) {
        if (_session != null) {
            IObjectTreeAPI api = 
                _session.getObjectTreeAPIOfActiveSessionWindow();
            IDatabaseObjectInfo[] infos = api.getSelectedDatabaseObjects();
            if (infos.length > 1 && !isMultipleObjectAction()) {
                _session.showMessage(getErrorMessage());
            } else {
                try {
                    getCommand(infos).execute();
                } catch (Exception e) {
                    _session.showMessage(e);
                }            
            }
        }
    }

    protected abstract ICommand getCommand(IDatabaseObjectInfo[] info);
    
    protected abstract boolean isMultipleObjectAction();
    
    protected abstract String getErrorMessage();

    public void setObjectTree(IObjectTreeAPI tree)
    {
       if(null != tree)
       {
          _session = tree.getSession();
       }
       else
       {
          _session = null;
       }
       _tree = tree;
       setEnabled(null != _session);
    }    
    
    
    
	
    public void setSession(ISession session) {
        _session = session;
    }
}