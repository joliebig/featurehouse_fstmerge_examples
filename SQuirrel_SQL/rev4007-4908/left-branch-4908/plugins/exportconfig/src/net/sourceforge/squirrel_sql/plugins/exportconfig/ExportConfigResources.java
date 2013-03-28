package net.sourceforge.squirrel_sql.plugins.exportconfig;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

final class ExportConfigResources extends PluginResources
{
	interface IMenuResourceKeys
	{
		String EXPORT = "export";
	}

	ExportConfigResources(String rsrcBundleBaseName, IPlugin plugin)
	{
		super(rsrcBundleBaseName, plugin);
	}
}
