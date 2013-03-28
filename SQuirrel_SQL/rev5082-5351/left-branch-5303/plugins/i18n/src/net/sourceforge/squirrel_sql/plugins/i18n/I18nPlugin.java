package net.sourceforge.squirrel_sql.plugins.i18n;

import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISession;


public class I18nPlugin extends DefaultSessionPlugin
{
	private PluginResources _resources;
   private I18nPanelController _i18nPanelController;

   
   public String getInternalName()
   {
      return "i18n";
   }

	
	public String getDescriptiveName()
	{
		return "I18n Plugin";
	}

	
	public String getVersion()
	{
		return "1.0";
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
		return "Rob Manning";
	}

	
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
      if(null == _i18nPanelController)
      {
         _i18nPanelController = new I18nPanelController(_resources);
      }


      return
               new IGlobalPreferencesPanel[]
               {
                 _i18nPanelController
               };
   }

	
	public synchronized void initialize() throws PluginException
	{
		_resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.i18n.i18n", this);
	}


	
	public PluginSessionCallback sessionStarted(ISession session)
	{
		return new PluginSessionCallbackAdaptor(this);
	}

}
