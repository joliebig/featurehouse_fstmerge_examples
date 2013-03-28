package net.sourceforge.squirrel_sql.plugins.laf;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
public final class LAFPluginResources extends PluginResources
{
	LAFPluginResources(IPlugin plugin)
	{
		super(LAFPluginResources.class.getName(), plugin);
	}
	interface IKeys
	{
		String CLASSNAME = "classname";
		String JAR = "jar";
	}
}
