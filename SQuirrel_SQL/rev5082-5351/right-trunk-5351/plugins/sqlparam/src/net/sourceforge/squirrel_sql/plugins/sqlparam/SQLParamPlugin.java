package net.sourceforge.squirrel_sql.plugins.sqlparam;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLExecutionListener;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;



public class SQLParamPlugin extends DefaultSessionPlugin
{
	private final static ILogger log = LoggerController.createLogger(SQLParamPlugin.class);

	private Resources resources;

	Map<String, String> cache;
	
    
    HashMap<ISQLPanelAPI, ISQLExecutionListener> panelListenerMap = 
        new HashMap<ISQLPanelAPI, ISQLExecutionListener>();
    
	
	public String getInternalName()
	{
		return "sqlparam";
	}

	
	public String getDescriptiveName()
	{
		return "SQL Parametrisation";
	}

	
	public String getVersion()
	{
		return "1.0.1";
	}


	
	@Override
	public String getContributors()
	{
		return "";
	}

	
	public String getAuthor()
	{
		return "Thorsten Mürell";
	}

	
	@Override
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	
	@Override
	public String getHelpFileName()
	{
		return "readme.html";
	}

	
	@Override
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	
	@Override
	public void initialize() throws PluginException
	{
		resources = new Resources(this);
	}

	
	@Override
	public void unload()
	{
		for (Map.Entry<ISQLPanelAPI, ISQLExecutionListener> entry :  panelListenerMap.entrySet()) {
			ISQLPanelAPI api = entry.getKey();
			ISQLExecutionListener listener = entry.getValue();
			removeSQLExecutionListener(api, listener);
		}
		panelListenerMap.clear();
	}
	
	
	@Override
	public boolean allowsSessionStartedInBackground()
	{
		return true;
	}

	
	@Override
	public void sessionCreated(ISession session)
	{
		cache = new Hashtable<String, String>();
	}
	
	
	public Map<String, String> getCache() {
		return cache;
	}
	
	
	public PluginSessionCallback sessionStarted(final ISession session)
	{
		log.info("Initializing plugin");
		ISQLPanelAPI sqlPaneAPI = session.getSessionSheet().getSQLPaneAPI();

		initSQLParam(sqlPaneAPI, session);

		PluginSessionCallback ret = new PluginSessionCallback()
		{
			public void sqlInternalFrameOpened(final SQLInternalFrame sqlInternalFrame, final ISession sess)
			{
				initSQLParam(sqlInternalFrame.getSQLPanelAPI(), sess);
			}

			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
			{
				
			}
			
		};

		return ret;
	}
	
	
	@Override
	public void sessionEnding(ISession session) {
		ISQLPanelAPI sqlPaneAPI = session.getSessionSheet().getSQLPaneAPI();
		ISQLExecutionListener listener = panelListenerMap.remove(sqlPaneAPI);
		removeSQLExecutionListener(sqlPaneAPI, listener);
	}

	private void removeSQLExecutionListener(ISQLPanelAPI api, ISQLExecutionListener listener)
	{
		if (api != null && listener != null) {
			api.removeSQLExecutionListener(listener);
		}
	}	
	
	private void initSQLParam(final ISQLPanelAPI sqlPaneAPI, final ISession session)
	{
		final SQLParamPlugin plugin = this;
		
		GUIUtils.processOnSwingEventThread(new Runnable()
		{
			public void run()
			{
				log.info("Adding SQL execution listener.");
                ISQLExecutionListener listener = 
                    new SQLParamExecutionListener(plugin, session);
				sqlPaneAPI.addSQLExecutionListener(listener);
                panelListenerMap.put(sqlPaneAPI, listener);
			}

		});
	}

	
	 public PluginResources getResources()
	 {
		 return resources;
	 }

}
