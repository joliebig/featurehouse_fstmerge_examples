package net.sourceforge.squirrel_sql.client.session.action;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ReconnectAction extends SquirrelAction
{
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ReconnectAction.class);

    
	private static interface i18n
	{
        
        
		String MSG = s_stringMgr.getString("ReconnectAction.confirmReconnect");
	}

	
	public ReconnectAction(IApplication app)
	{
		super(app);
	}

	
	public void actionPerformed(ActionEvent evt) {
		IApplication app = getApplication();
		if(Dialogs.showYesNo(app.getMainFrame(), i18n.MSG))
		{
         
         
         ISession activeSession = getApplication().getSessionManager().getActiveSession();
			activeSession.reconnect();
		}
	}
}
