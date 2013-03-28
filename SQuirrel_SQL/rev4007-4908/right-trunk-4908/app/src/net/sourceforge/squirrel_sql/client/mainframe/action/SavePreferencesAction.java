package net.sourceforge.squirrel_sql.client.mainframe.action;



import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.gui.DialogUtils;


public class SavePreferencesAction extends SquirrelAction {

    static final long serialVersionUID = 6961615570741567740L;

    
    public SavePreferencesAction(IApplication app) {
        super(app);
    }

    
    public void actionPerformed(ActionEvent evt) {
        IApplication app = getApplication();
        SavePreferencesCommand command = 
            new SavePreferencesCommand(app, getParentFrame(evt));
        command.setDialogUtils(new DialogUtils());
        command.execute();
    }
}
