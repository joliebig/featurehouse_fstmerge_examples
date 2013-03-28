package net.sourceforge.squirrel_sql.plugins.example;

import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;


public class ExamplePlugin extends DefaultSessionPlugin
{
	private PluginResources _resources;

	
	public String getInternalName()
	{
		return "example";
	}

	
	public String getDescriptiveName()
	{
		return "Example Plugin";
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
		return "";
	}

	
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return new IGlobalPreferencesPanel[0];
	}

	
	public synchronized void initialize() throws PluginException
	{
		_resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.example.example", this);
	}


	
	public PluginSessionCallback sessionStarted(ISession session)
	{
		try
		{
         String driverName = session.getSQLConnection().getConnection().getMetaData().getDriverName();
         if(false == driverName.toUpperCase().startsWith("IBM DB2 JDBC"))
         {
            
            
            return null;
         }

         
         IObjectTreeAPI otApi = session.getSessionInternalFrame().getObjectTreeAPI();
         otApi.addToPopup(DatabaseObjectType.VIEW, new ScriptDB2ViewAction(getApplication(), _resources, session));
         otApi.addToPopup(DatabaseObjectType.PROCEDURE, new ScriptDB2ProcedureAction(getApplication(), _resources, session));

         return new PluginSessionCallbackAdaptor(this);
		}
		catch(Exception e)
		{
         throw new RuntimeException(e);
		}
	}

}
