package net.sourceforge.squirrel_sql.plugins.dbcopy.actions;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.DBCopyPlugin;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbcopy.commands.PasteTableCommand;


public class PasteTableAction extends SquirrelAction
                                     implements ISessionAction {

	
	private final SessionInfoProvider sessionInfoProv;

    
    private IApplication app = null;
    
    
    private final static ILogger log = 
                         LoggerController.createLogger(PasteTableAction.class);    
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(PasteTableAction.class);    
    
    
    public PasteTableAction(IApplication app, Resources rsrc,
    									DBCopyPlugin plugin) {
        super(app, rsrc);
        this.app = app;
        sessionInfoProv = plugin;
    }

    
    public void actionPerformed(ActionEvent evt) {
        ISession destSession = sessionInfoProv.getCopyDestSession();
        IObjectTreeAPI api = 
            destSession.getObjectTreeAPIOfActiveSessionWindow();
        if (api == null) {
            return;
        }
        IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
        if (dbObjs.length > 1) {
            sessionInfoProv.setDestSelectedDatabaseObject(null);
            
            
            String msg =
            	s_stringMgr.getString("PasteTableAction.error.multischemapaste");
            app.showErrorDialog(msg);
            		            
            return;
        } else {
        	
        	
        	if (DatabaseObjectType.TABLE_TYPE_DBO.equals(dbObjs[0].getDatabaseObjectType())) {
        		IDatabaseObjectInfo tableLabelInfo = dbObjs[0];
        		ISQLConnection destCon = destSession.getSQLConnection();
        		SQLDatabaseMetaData md = null;
        		if (destCon != null) {
        			md = destCon.getSQLMetaData();
        		}
        		IDatabaseObjectInfo schema = 
        			new DatabaseObjectInfo(null, 
        								   tableLabelInfo.getSchemaName(),
        								   tableLabelInfo.getSchemaName(),
        								   DatabaseObjectType.SCHEMA,
        								   md);
        		sessionInfoProv.setDestSelectedDatabaseObject(schema);
        	} else {
        		sessionInfoProv.setDestSelectedDatabaseObject(dbObjs[0]);
        	}
            
        }
        
        try {
            IDatabaseObjectInfo info
                            = sessionInfoProv.getDestSelectedDatabaseObject();
            if (info == null || destSession == null) {
                return;
            }
            if (!checkSession(destSession, info)) {
                return;
            }
        } catch (UserCancelledOperationException e) {
            return;
        }
        if (sessionInfoProv.getCopySourceSession() == null) {
            return;
        }        
        if (!sourceDestSchemasDiffer()) {
            
            
            
            return;
        }
        new PasteTableCommand(sessionInfoProv).execute();
    }

	
    public void setSession(ISession session) {
        sessionInfoProv.setDestCopySession(session);        
    }
    
    
    private boolean checkSession(ISession session, IDatabaseObjectInfo dbObj) 
        throws UserCancelledOperationException 
    {
        if (session == null || dbObj == null) {
            return true;
        }
        String typeName = dbObj.getDatabaseObjectType().getName();
        
        log.debug("PasteTableAction.checkSession: dbObj type="+typeName+
                  " name="+dbObj.getSimpleName());

        HibernateDialect d = 
            DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                      session.getApplication().getMainFrame(), 
                                      session.getMetaData());
        if (!d.canPasteTo(dbObj)) {
            
            
            
            String errmsg = 
                s_stringMgr.getString("PasteTableAction.error.destdbobj",
                                      new Object[] { typeName });
            app.showErrorDialog(errmsg);
            return false;
        }
        return true;
    }
    
    
    private boolean sourceDestSchemasDiffer() {
        
        
        
        
        
        
        return true;
    }    
}
