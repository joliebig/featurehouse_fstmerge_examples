package net.sourceforge.squirrel_sql.plugins.userscript;



import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.userscript.kernel.UserScriptAdmin;

import javax.swing.*;
import java.net.URLClassLoader;
import java.util.Hashtable;


public class UserScriptPlugin extends DefaultSessionPlugin
{

   private interface IMenuResourceKeys
   {
      String USER_SCRIPT = "userscript";
   }

   
   @SuppressWarnings("unused")
   private static ILogger s_log = LoggerController.createLogger(UserScriptPlugin.class);

   private PluginResources _resources;

   private Hashtable<IIdentifier, UserScriptAdmin> _userScriptAdminsBySessionId = 
       new Hashtable<IIdentifier, UserScriptAdmin>();
   private URLClassLoader m_userScriptClassLoader;

   
   public String getInternalName()
   {
      return "userscript";
   }

   
   public String getDescriptiveName()
   {
      return "User Scripts Plugin";
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


   
   public synchronized void initialize() throws PluginException
   {
      super.initialize();
      IApplication app = getApplication();

      _resources =
         new PluginResources(
            "net.sourceforge.squirrel_sql.plugins.userscript.userscript",
            this);



      ActionCollection coll = app.getActionCollection();
      coll.add(new UserScriptAction(app, _resources, this));
      coll.add(new UserScriptSQLAction(app, _resources, this));

      createMenu();
   }

   
   public void unload()
   {
      super.unload();
   }

   public boolean allowsSessionStartedInBackground()
   {
      return true;
   }

   
   public PluginSessionCallback sessionStarted(final ISession session)
   {
      

      GUIUtils.processOnSwingEventThread(new Runnable() {
          public void run() {
              ActionCollection coll = getApplication().getActionCollection();
              IObjectTreeAPI api = 
                  session.getSessionInternalFrame().getObjectTreeAPI();

              api.addToPopup(DatabaseObjectType.TABLE, coll.get(UserScriptAction.class));
              api.addToPopup(DatabaseObjectType.PROCEDURE, coll.get(UserScriptAction.class));
              api.addToPopup(DatabaseObjectType.SESSION, coll.get(UserScriptAction.class));              
          }
      });


      UserScriptAdmin adm = new UserScriptAdmin(this, session);
      _userScriptAdminsBySessionId.put(session.getIdentifier(), adm);

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


   private void createMenu()
   {
      IApplication app = getApplication();
      ActionCollection coll = app.getActionCollection();

      JMenu menu = _resources.createMenu(IMenuResourceKeys.USER_SCRIPT);

      _resources.addToMenu(coll.get(UserScriptSQLAction.class), menu);

      app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
   }

   public UserScriptAdmin getUserScriptAdmin(ISession session)
   {
      return _userScriptAdminsBySessionId.get(session.getIdentifier());
   }

   public URLClassLoader getUserScriptClassLoader()
   {
      return m_userScriptClassLoader;
   }

   public void setUserScriptClassLoader(URLClassLoader urlClassLoader)
   {
      m_userScriptClassLoader = urlClassLoader;
   }

}
