package net.sourceforge.squirrel_sql.plugins.refactoring.actions;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.ModifyColumnCommand;

public class ModifyColumnAction extends AbstractRefactoringAction
                                     implements ISessionAction {

    private static final long serialVersionUID = 1L;

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ModifyColumnAction.class);
    
    private static interface i18n {
        
        String columnPart = 
            s_stringMgr.getString("AddColumnAction.addColumnPart");
        
        
        String singleObjectMessage = 
            s_stringMgr.getString("Shared.singleObjectMessage", columnPart); 
    }
    
    public ModifyColumnAction(IApplication app, 
                           Resources rsrc) 
    {
        super(app, rsrc); 
    }

    protected ICommand getCommand(IDatabaseObjectInfo[] info) {
        return new ModifyColumnCommand(_session, info);
    }
    
    protected String getErrorMessage() {
        return i18n.singleObjectMessage;
    }

    @Override
    protected boolean isMultipleObjectAction() {
        
        return false;
    }

}