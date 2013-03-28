
package net.sourceforge.squirrel_sql.plugins.sqlreplace;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class SQLReplacePlugin extends DefaultSessionPlugin {

	
	Map<String, String> cache;

	
    HashMap<ISQLPanelAPI, ISQLExecutionListener> panelListenerMap = 
        new HashMap<ISQLPanelAPI, ISQLExecutionListener>();

    
	private final static ILogger log = 
	       LoggerController.createLogger(SQLReplacePlugin.class);  

	   private static String RESOURCE_PATH =
	      "net.sourceforge.squirrel_sql.plugins.sqlreplace.sqlreplace";

	   private static ILogger logger =
	      LoggerController.createLogger(SQLReplacePlugin.class);

	   
	   private File pluginAppFolder;


	   private PluginResources resources;


	   private ReplacementManager replacementManager; 

	
	public String getAuthor() {
		return "Dieter Engelhardt";
	}

	
	public String getDescriptiveName() {
		return "SQLReplace Plugin";
	}

	
	public String getInternalName() {
		return "sqlreplace";
	}

	
	public String getVersion() {
		return "0.0.1";
	}

	   
	   public synchronized void initialize() throws PluginException
	   {
	      super.initialize();

	      IApplication app = getApplication();

	      
	      
	      try
	      {
	         pluginAppFolder = getPluginAppSettingsFolder();
	      }
	      catch (IOException ex)
	      {
	         throw new PluginException(ex);
	      }

	      
	      resources = new SQLReplaceResources(RESOURCE_PATH, this);
	      replacementManager = new ReplacementManager(this);
	      
	      try
	      {
	    	  replacementManager.load();
	      }
	      catch (IOException e)
	      {
	         if (!(e instanceof FileNotFoundException))
	         {
	            logger.error("Problem loading replacementManager", e);
	         }
	      }
	
		   }

	   
	   ReplacementManager getReplacementManager() {
		   return replacementManager;
	   }

	  
	   protected String getResourceString(String name)
	   {
	      return resources.getString(name);
	   }

	  
	   public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	   {
	      return new IGlobalPreferencesPanel[]{
	         new SQLReplacePreferencesController(this)
	      };
	   }
		
		public PluginSessionCallback sessionStarted(ISession session) {
			try
			{
				ISQLPanelAPI sqlPaneAPI = session.getSessionSheet().getSQLPaneAPI();

				initSQLReplace(sqlPaneAPI, session);


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
			catch(Exception e)
			{
	         throw new RuntimeException(e);
			}
		}

		
		
		public void unload()
		{
		   for (ISQLPanelAPI api : panelListenerMap.keySet()) {
		        api.removeSQLExecutionListener(panelListenerMap.get(api));
	       }
		}

		public boolean allowsSessionStartedInBackground()
		{
			return true;
		}

		
		@Override
		public void sessionCreated(ISession session)
		{
			try{
				replacementManager.load();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}


		
		private void initSQLReplace(final ISQLPanelAPI sqlPaneAPI, final ISession session)
		{
			final SQLReplacePlugin plugin = this;
			
			GUIUtils.processOnSwingEventThread(new Runnable()
			{
				public void run()
				{
					log.info("Adding SQL execution listener.");
	                ISQLExecutionListener listener = 
	                    new SQLReplaceExecutionListener(plugin, session);
					sqlPaneAPI.addSQLExecutionListener(listener);
	                panelListenerMap.put(sqlPaneAPI, listener);
				}

			});
		}

		
		@Override
		public void sessionEnding(ISession session) {
			ISQLPanelAPI sqlPaneAPI = session.getSessionSheet().getSQLPaneAPI();
			ISQLExecutionListener listener = panelListenerMap.remove(sqlPaneAPI);
			sqlPaneAPI.removeSQLExecutionListener(listener);
		}

}
