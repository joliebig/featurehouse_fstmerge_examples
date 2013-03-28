package net.sourceforge.squirrel_sql.plugins.dbinfo;

import java.io.File;
import java.io.IOException;

import javax.swing.Action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;




public class DBInfoPlugin extends DefaultSessionPlugin {
    
    private PluginResources _resources;

    
    private File _dataFolder;

    
    public String getInternalName() {
        return "dbinfo";
    }

    
    public String getDescriptiveName() {
        return "Database Info Plugin";
    }

    
    public String getVersion() {
        return "0.1";
    }

    
    public String getAuthor() {
        return "??";
    }

    
    public synchronized void initialize() throws PluginException {
        super.initialize();

        _resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.dbinfo.dbinfo", this);

        createActions();

        
        try {
	        _dataFolder = new File(getPluginAppSettingsFolder(), "data");
        } catch (IOException ex) {
        	throw new PluginException(ex);
        }
        if (!_dataFolder.exists()) {
            _dataFolder.mkdir();
        }
    }

    
    public void unload() {
        super.unload();
    }

    private void createActions() {
        final IApplication app = getApplication();
        ActionCollection coll = app.getActionCollection();
        Action action = new ShowDBInfoFilesAction(app, _resources);
        coll.add(action);
        app.addToMenu(IApplication.IMenuIDs.PLUGINS_MENU, action);
    }

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   public PluginSessionCallback sessionStarted(ISession session)
   {
      PluginSessionCallback ret = new PluginSessionCallback()
      {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
         {
            
            
         }

         public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
         {
            
            
         }
      };
      return ret;
   }
}
