package net.sourceforge.squirrel_sql.plugins.dbdiff;




import javax.swing.JMenu;
import javax.swing.JMenuItem;
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
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.actions.CompareAction;
import net.sourceforge.squirrel_sql.plugins.dbdiff.actions.SelectAction;


public class DBDiffPlugin extends DefaultSessionPlugin 
                          implements SessionInfoProvider {

    
    private final static ILogger s_log = LoggerController.createLogger(DBDiffPlugin.class);
    
    private PluginResources _resources;
    
    private ISession diffSourceSession = null;
    
    private ISession diffDestSession = null;
    
    private IDatabaseObjectInfo[] selectedDatabaseObjects = null;   
    
    private IDatabaseObjectInfo[] selectedDestDatabaseObjects = null;
    
    
    public PluginSessionCallback sessionStarted(final ISession session) {
        addMenuItemsToContextMenu(session);    
        return new DBDiffPluginSessionCallback(this);
    }
        
    
    public String getInternalName() {
        return "dbdiff";
    }

    
    public String getDescriptiveName() {
        return "DBDiff Plugin";
    }

    
    public String getAuthor() {
        return "Rob Manning";
    }

    
    
    public String getContributors() {
        return "";
    }

    
    public String getVersion() {
        return "0.01";
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
            s_log.debug("Initializing DB Diff Plugin");
        }

        _resources =
            new DBDiffPluginResources(
                "net.sourceforge.squirrel_sql.plugins.dbdiff.dbdiff",
                this);
        

        IApplication app = getApplication();
        ActionCollection coll = app.getActionCollection();        
        coll.add(new SelectAction(app, _resources, this));
        coll.add(new CompareAction(app, _resources, this));
        
        
        
    }
    
    public void unload() {
        super.unload();
        
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
        
        return new IGlobalPreferencesPanel[0];
    }
    
    
    protected void addMenuItemsToContextMenu(ISession session) 
    {
        final IObjectTreeAPI api = 
            session.getObjectTreeAPIOfActiveSessionWindow();
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
    
    private class DBDiffPluginResources extends PluginResources {
        DBDiffPluginResources(String rsrcBundleBaseName, IPlugin plugin) {
            super(rsrcBundleBaseName, plugin);
        }
    }    
    
    private void addToPopup(IObjectTreeAPI api, ActionCollection coll) {
              
        
        JMenu dbdiffMenu = _resources.createMenu("dbdiff");
        
        JMenuItem selectItem = new JMenuItem(coll.get(SelectAction.class));
        JMenuItem compareItem = new JMenuItem(coll.get(CompareAction.class));
        dbdiffMenu.add(selectItem);
        dbdiffMenu.add(compareItem);
        
        
        api.addToPopup(DatabaseObjectType.CATALOG, dbdiffMenu);
        api.addToPopup(DatabaseObjectType.SCHEMA, dbdiffMenu);
        api.addToPopup(DatabaseObjectType.TABLE_TYPE_DBO, dbdiffMenu);
        api.addToPopup(DatabaseObjectType.TABLE, dbdiffMenu);

        
    }
        
    public void setCompareMenuEnabled(boolean enabled) {
        final ActionCollection coll = getApplication().getActionCollection();
        CompareAction compareAction = 
            (CompareAction)coll.get(CompareAction.class);
        compareAction.setEnabled(enabled);
    }
    
    
    
    
    
    public ISession getDiffSourceSession() {
        return diffSourceSession;
    }
    
    
    public void setDiffSourceSession(ISession session) {
        if (session != null) {
            diffSourceSession = session;
        }
    }

    
    public IDatabaseObjectInfo[] getSourceSelectedDatabaseObjects() {
        return selectedDatabaseObjects;
    }

    
    public ISession getDiffDestSession() {
        return diffDestSession;
    }

    
    public void setDestDiffSession(ISession session) {
        diffDestSession = session;
    }

    
    public IDatabaseObjectInfo[] getDestSelectedDatabaseObjects() {
        return selectedDestDatabaseObjects;
    }
    
    public void setDestSelectedDatabaseObjects(IDatabaseObjectInfo[] info) {
        selectedDestDatabaseObjects = info;
    }
}
