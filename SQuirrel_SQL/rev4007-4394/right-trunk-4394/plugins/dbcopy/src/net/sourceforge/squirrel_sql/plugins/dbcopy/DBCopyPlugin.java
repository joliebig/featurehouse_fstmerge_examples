package net.sourceforge.squirrel_sql.plugins.dbcopy;




import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.actions.CopyTableAction;
import net.sourceforge.squirrel_sql.plugins.dbcopy.actions.PasteTableAction;
import net.sourceforge.squirrel_sql.plugins.dbcopy.gui.DBCopyGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.plugins.dbcopy.prefs.PreferencesManager;


public class DBCopyPlugin extends DefaultSessionPlugin 
                          implements SessionInfoProvider {

    
    private final static ILogger s_log = LoggerController.createLogger(DBCopyPlugin.class);
    
    private PluginResources _resources;
    
    private ISession copySourceSession = null;
    
    private ISession copyDestSession = null;
    
    private IDatabaseObjectInfo[] selectedDatabaseObjects = null;   
    
    private IDatabaseObjectInfo selectedDestDatabaseObject = null;
    
    
    public PluginSessionCallback sessionStarted(final ISession session) {
        IObjectTreeAPI api = session.getObjectTreeAPIOfActiveSessionWindow();
        addMenuItemsToContextMenu(api);        
        return new DBCopyPluginSessionCallback(this);
    }
    
    public void sessionEnding(final ISession session) {
        if (session.equals(copySourceSession)) {
            copySourceSession = null;
            
            setPasteMenuEnabled(false);
        }
    }
    
    
    public String getInternalName() {
        return "dbcopy";
    }

    
    public String getDescriptiveName() {
        return "DBCopy Plugin";
    }

    
    public String getAuthor() {
        return "Rob Manning";
    }

    
    
    public String getContributors() {
        return "Dan Dragut";
    }

    
    public String getVersion() {
        return "1.14";
    }

    
    public String getHelpFileName()
    {
       return "readme.html";
    }    
    
    
    public String getChangeLogFileName()
    {
        return "changes.txt";
    }
    
    public void initialize() throws PluginException {
        super.initialize();
        
        if (s_log.isDebugEnabled()) {
            s_log.debug("Initializing DB Copy Plugin");
        }
        _resources =
            new DBCopyPluginResources(
                "net.sourceforge.squirrel_sql.plugins.dbcopy.dbcopy",
                this);
        PreferencesManager.initialize(this);
        
        IApplication app = getApplication();
        ActionCollection coll = app.getActionCollection();        
        coll.add(new CopyTableAction(app, _resources, this));
        coll.add(new PasteTableAction(app, _resources, this));
        
        setPasteMenuEnabled(false);
    }
    
    public void unload() {
        super.unload();
        copySourceSession = null;
        setPasteMenuEnabled(false);
        PreferencesManager.unload();
    }    
    
    public void setCopyMenuEnabled(boolean enabled) {
        final ActionCollection coll = getApplication().getActionCollection();
        CopyTableAction copyAction = 
            (CopyTableAction)coll.get(CopyTableAction.class);
        copyAction.setEnabled(enabled);        
    }

    public void setPasteMenuEnabled(final boolean enabled) {
        GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
                final ActionCollection coll = getApplication().getActionCollection();
                PasteTableAction pasteAction = 
                    (PasteTableAction)coll.get(PasteTableAction.class);
                pasteAction.setEnabled(enabled);                
            }
        });
    }
    
    
    public void setSelectedDatabaseObjects(IDatabaseObjectInfo[] dbObjArr) {
        if (dbObjArr != null) {
            selectedDatabaseObjects = dbObjArr;
            for (int i = 0; i < dbObjArr.length; i++) {
                if (s_log.isDebugEnabled()) {
                    s_log.debug(
                        "setSelectedDatabaseObjects: IDatabaseObjectInfo["+
                        i+"]="+dbObjArr[i]);
                }
            }
        }
    }

    
    public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
        DBCopyGlobalPreferencesTab tab = new DBCopyGlobalPreferencesTab();
        return new IGlobalPreferencesPanel[] { tab };
    }
    
    
    protected void addMenuItemsToContextMenu(final IObjectTreeAPI api) 
    {
        
        final ActionCollection coll = getApplication().getActionCollection();
        
        if (SwingUtilities.isEventDispatchThread()) {
            addToPopup(api, coll);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    addToPopup(api, coll);
                }
            });
        }
    }
    
    private void addToPopup(IObjectTreeAPI api, ActionCollection coll) {

        api.addToPopup(DatabaseObjectType.TABLE_TYPE_DBO,
                       coll.get(CopyTableAction.class));
    	
        api.addToPopup(DatabaseObjectType.TABLE_TYPE_DBO,
	                   coll.get(PasteTableAction.class));
        
    	
        api.addToPopup(DatabaseObjectType.TABLE, 
                       coll.get(CopyTableAction.class));

        api.addToPopup(DatabaseObjectType.TABLE,
		           coll.get(PasteTableAction.class));        
        
        
        api.addToPopup(DatabaseObjectType.SCHEMA, 
                       coll.get(PasteTableAction.class));
           
        
        api.addToPopup(DatabaseObjectType.CATALOG, 
                       coll.get(PasteTableAction.class));      
        
        api.addToPopup(DatabaseObjectType.SESSION, 
                       coll.get(PasteTableAction.class));
        
    }
        
    private class DBCopyPluginResources extends PluginResources {
        DBCopyPluginResources(String rsrcBundleBaseName, IPlugin plugin) {
            super(rsrcBundleBaseName, plugin);
        }
    }
    
    
    
    
    public ISession getCopySourceSession() {
        return copySourceSession;
    }
    
    
    public void setCopySourceSession(ISession session) {
        if (session != null) {
            copySourceSession = session;
        }
    }

    
    public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects() {
        return selectedDatabaseObjects;
    }

    
    public ISession getCopyDestSession() {
        return copyDestSession;
    }

    
    public void setDestCopySession(ISession session) {
        copyDestSession = session;
    }

    
    public IDatabaseObjectInfo getDestSelectedDatabaseObject() {
        return selectedDestDatabaseObject;
    }
    
    public void setDestSelectedDatabaseObject(IDatabaseObjectInfo info) {
        selectedDestDatabaseObject = info;
    }
}
