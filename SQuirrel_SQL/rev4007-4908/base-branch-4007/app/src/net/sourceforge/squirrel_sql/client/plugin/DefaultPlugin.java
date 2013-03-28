package net.sourceforge.squirrel_sql.client.plugin;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.gui.db.aliasproperties.IAliasPropertiesPanelController;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public abstract class DefaultPlugin implements IPlugin
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DefaultPlugin.class);

	
	protected IApplication _app;

	
	public void load(IApplication app) throws PluginException
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
	}

	
	public void initialize() throws PluginException
	{
		
	}

	
	public void unload() 
	{
		
		
	}

	
	public String getContributors()
	{
		return "";
	}

	
	public String getWebSite()
	{
		return Version.getWebSite();
	}

	
	public String getHelpFileName()
	{
		return null;
	}

	
	public String getChangeLogFileName()
	{
		return null;
	}

	
	public String getLicenceFileName()
	{
		return null;
	}

	
	public final IApplication getApplication()
	{
		return _app;
	}

	
	public synchronized File getPluginAppSettingsFolder()
		throws IllegalStateException, IOException
	{
		final String internalName = getInternalName();
		if (internalName == null || internalName.trim().length() == 0)
		{
			throw new IllegalStateException("IPlugin doesn't have a valid internal name");
		}
		final File pluginDir = new ApplicationFiles().getPluginsDirectory();
		final File file = new File(pluginDir, internalName);
		if (!file.exists())
		{
			file.mkdirs();
		}

		if (!file.isDirectory())
		{
			throw new IOException(s_stringMgr.getString("DefaultPlugin.error.cannotcreate", file.getAbsolutePath()));
		}

		return file;
	}

	
	public synchronized File getPluginUserSettingsFolder()
		throws IllegalStateException, IOException
	{
		final String internalName = getInternalName();
		if (internalName == null || internalName.trim().length() == 0)
		{
			throw new IllegalStateException("IPlugin doesn't have a valid internal name");
		}
		String name =
			new ApplicationFiles().getPluginsUserSettingsDirectory()
				+ File.separator
				+ internalName
				+ File.separator;
		File file = new File(name);
		if (!file.exists())
		{
			file.mkdirs();
		}

		if (!file.isDirectory())
		{
			throw new IOException(s_stringMgr.getString("DefaultPlugin.error.cannotcreate", name));
		}

		return file;
	}

	
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return null;
	}

   public IAliasPropertiesPanelController[] getAliasPropertiesPanelControllers(SQLAlias alias)
   {
      return null;
   }

   public void aliasCopied(SQLAlias source, SQLAlias target)
   {
   }

   public void aliasRemoved(SQLAlias alias)
   {
   }


   
	public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels()
	{
		return null;
	}

   
   public Object getExternalService()
   {
      return null;
   }
   

}
