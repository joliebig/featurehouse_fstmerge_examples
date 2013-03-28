package net.sourceforge.squirrel_sql.client.plugin;


public class PluginInfo
{
	
	public interface IPropertyNames
	{
		String AUTHOR = "author";
		String CONTRIBUTORS = "contributors";
		String DESCRIPTIVE_NAME = "descriptiveName";
		String INTERNAL_NAME = "internalName";
		String IS_LOADED = "isLoaded";
		String PLUGIN_CLASS_NAME = "pluginClassName";
		String VERSION = "version";
		String WEB_SITE = "webSite";
	}

	private String _pluginClassName;
	private IPlugin _plugin;
	private boolean _loaded;

	
	public PluginInfo()
	{
		super();
	}

	PluginInfo(String pluginClassName) throws IllegalArgumentException
	{
		super();
		if (pluginClassName == null)
		{
			throw new IllegalArgumentException("pluginClassName == null");
		}

		_pluginClassName = pluginClassName;
	}

	public void assignFrom(PluginInfo pi) throws IllegalArgumentException
	{
		if (pi == null)
		{
			throw new IllegalArgumentException("PluginInfo == null");
		}

		if (this != pi)
		{
			setPlugin(pi.getPlugin());
			setLoaded(pi.isLoaded());
		}
	}

	public String getPluginClassName()
	{
		return _pluginClassName;
	}

	public boolean isLoaded()
	{
		return _loaded;
	}

	
	public String getInternalName()
	{
		return _plugin.getInternalName();
	}

	
	public String getDescriptiveName()
	{
		return _plugin.getDescriptiveName();
	}

	
	public String getAuthor()
	{
		return _plugin.getAuthor();
	}

	
	public String getContributors()
	{
		return _plugin.getContributors();
	}

	
	public String getWebSite()
	{
		return _plugin.getWebSite();
	}

	
	public String getVersion()
	{
		return _plugin.getVersion();
	}

	
	public String getHelpFileName()
	{
		return _plugin.getHelpFileName();
	}

	
	public String getChangeLogFileName()
	{
		return _plugin.getChangeLogFileName();
	}

	
	public String getLicenceFileName()
	{
		return _plugin.getLicenceFileName();
	}

	
	public IPlugin getPlugin()
	{
		return _plugin;
	}

	void setPlugin(IPlugin value) throws IllegalArgumentException
	{
		if (value == null)
		{
			throw new IllegalArgumentException("Null IPlugin passed");
		}
		_plugin = value;
	}

	void setLoaded(boolean value)
	{
		_loaded = value;
	}
}
