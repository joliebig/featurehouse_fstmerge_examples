package net.sourceforge.squirrel_sql.client.plugin;

import net.sourceforge.squirrel_sql.fw.util.Resources;

public class PluginResources extends Resources
{
	public PluginResources(String rsrcBundleBaseName, IPlugin plugin)
	{
		super(rsrcBundleBaseName, getClassLoader(plugin));
	}

	private static ClassLoader getClassLoader(IPlugin plugin)
	{
		if (plugin == null)
		{
			throw new IllegalArgumentException("Null IPlugin passed");
		}
		return plugin.getClass().getClassLoader();
	}
}