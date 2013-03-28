package net.sourceforge.squirrel_sql.plugins.dbdiff.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbdiff.commands.CompareCommand;


public class CompareAction extends SquirrelAction
                                     implements ISessionAction {

	
	private final SessionInfoProvider sessionInfoProv;

    
    private IApplication app = null;
    
    
    private final static ILogger log = 
                         LoggerController.createLogger(CompareAction.class);    
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(CompareAction.class);    
    
    
    public CompareAction(IApplication app, 
                         Resources rsrc, 
                         SessionInfoProvider prov) {
        super(app, rsrc);
        this.app = app;
        sessionInfoProv = prov;
    }

    
    public void actionPerformed(ActionEvent evt) {
        ISession destSession = sessionInfoProv.getDiffDestSession();
        IObjectTreeAPI api = destSession.getObjectTreeAPIOfActiveSessionWindow();
        if (api == null) {
            return;
        }
        IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
        sessionInfoProv.setDestSelectedDatabaseObjects(dbObjs);

        if (sessionInfoProv.getDiffSourceSession() == null) {
            return;
        }        
        if (!sourceDestSchemasDiffer()) {
            
            
            
            return;
        }
        new CompareCommand(sessionInfoProv).execute();
    }

	
    public void setSession(ISession session) {
        sessionInfoProv.setDestDiffSession(session);        
    }
        
    
    private boolean sourceDestSchemasDiffer() {
        
        
        
        
        
        
        return true;
    }    
}
