package net.sourceforge.squirrel_sql.client.plugin;

public class SessionPluginInfo extends PluginInfo
{
	public SessionPluginInfo(PluginInfo pi) throws IllegalArgumentException
	{
		super(getPassedPluginClassName(pi));
		assignFrom(pi);
	}

	public ISessionPlugin getSessionPlugin()
	{
		return (ISessionPlugin) getPlugin();
	}

	void setPlugin(IPlugin value) throws IllegalArgumentException
	{
		if (value == null)
		{
			throw new IllegalArgumentException("Null IPlugin passed");
		}
		if (!(value instanceof ISessionPlugin))
		{
			throw new IllegalArgumentException("Plugin not an ISessionPlugin");
		}
		super.setPlugin(value);
	}

	private static String getPassedPluginClassName(PluginInfo pi)
		throws IllegalArgumentException
	{
		if (pi == null)
		{
			throw new IllegalArgumentException("Null PluginInfo passed");
		}
		return pi.getPluginClassName();
	}
}
