package de.ixdb.squirrel_sql.plugins.cache;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.*;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.File;
import java.io.IOException;


public class CachePlugin extends DefaultSessionPlugin
{

   
   private static ILogger s_log = LoggerController.createLogger(CachePlugin.class);

   
   private File _pluginAppFolder;

   private PluginResources _resources;

   
   private File _userSettingsFolder;

   
   public String getInternalName()
   {
      return "cache";
   }

   
   public String getDescriptiveName()
   {
      return "Plugin for the Intersystems Cache DB";
   }

   
   public String getVersion()
   {
      return "0.01";
   }

   
   public String getAuthor()
   {
      return "Gerd Wagner";
   }

   
   public String getChangeLogFileName()
   {
      return "changes.txt";
   }

   
   public String getHelpFileName()
   {
      return "readme.txt";
   }

   
   public String getLicenceFileName()
   {
      return "licence.txt";
   }

   
   public String getContributors()
   {
      return "Andreas Schneider";
   }

   
   public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
   {
      return new IGlobalPreferencesPanel[0];
   }

   
   public synchronized void initialize() throws PluginException
   {
      super.initialize();
      IApplication app = getApplication();

      PluginManager pmgr = app.getPluginManager();

      
      
      try
      {
         _pluginAppFolder = getPluginAppSettingsFolder();
      }
      catch (IOException ex)
      {
         throw new PluginException(ex);
      }

      
      try
      {
         _userSettingsFolder = getPluginUserSettingsFolder();
      }
      catch (IOException ex)
      {
         throw new PluginException(ex);
      }

      _resources = new CachePluginResources("de.ixdb.squirrel_sql.plugins.cache.cache", this);

      
      ActionCollection coll = app.getActionCollection();
      coll.add(new ScriptViewAction(app, _resources, this));
      coll.add(new ScriptFunctionAction(app, _resources, this));
      coll.add(new ScriptCdlAction(app, _resources, this));
      coll.add(new ShowNamespacesAction(app, _resources, this));
      coll.add(new ShowQueryPlanAction(app, _resources, this));
      coll.add(new ShowProcessesAction(app, _resources, this));



   }

   
   public void unload()
   {
      super.unload();
   }

   
   public PluginSessionCallback sessionStarted(ISession session)
   {
      try
      {
         if(-1 != session.getSQLConnection().getConnection().getMetaData().getDriverName().toUpperCase().indexOf("CACHE"))
         {
            ActionCollection coll = getApplication().getActionCollection();
            IObjectTreeAPI otApi = session.getSessionInternalFrame().getObjectTreeAPI();
            otApi.addToPopup(DatabaseObjectType.VIEW, coll.get(ScriptViewAction.class));
            otApi.addToPopup(DatabaseObjectType.SESSION, coll.get(ShowNamespacesAction.class));
            otApi.addToPopup(DatabaseObjectType.SESSION, coll.get(ShowProcessesAction.class));
            otApi.addToPopup(DatabaseObjectType.PROCEDURE, coll.get(ScriptFunctionAction.class));
            otApi.addToPopup(DatabaseObjectType.PROCEDURE, coll.get(ScriptCdlAction.class));
            otApi.addToPopup(DatabaseObjectType.TABLE, coll.get(ScriptCdlAction.class));
            otApi.addToPopup(DatabaseObjectType.VIEW, coll.get(ScriptCdlAction.class));


            ISQLPanelAPI sqlApi = session.getSessionInternalFrame().getSQLPanelAPI();
            sqlApi.addToSQLEntryAreaMenu(coll.get(ShowQueryPlanAction.class));

            session.addSeparatorToToolbar();
            session.addToToolbar(coll.get(ShowNamespacesAction.class));
            session.addToToolbar(coll.get(ShowProcessesAction.class));
            session.addToToolbar(coll.get(ShowQueryPlanAction.class));
            session.getSessionInternalFrame().addToToolsPopUp("cachequeryplan", coll.get(ShowQueryPlanAction.class));


            return new PluginSessionCallback()
            {
               public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
               {
                  
               }

               public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
               {
                  
               }
            };
         }
         else
         {
            return null;
         }
      }
      catch(Exception e)
      {
         s_log.error("Could not get driver name", e);
         return null;
      }
   }

}
