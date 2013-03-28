package net.sourceforge.squirrel_sql.plugins.mysql;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

final class MysqlResources extends PluginResources
{
	interface IMenuResourceKeys
	{
		String CHECK_TABLE = "checktable";
		String MYSQL = "mysql";
	}

	MysqlResources(String rsrcBundleBaseName, IPlugin plugin)
	{
		super(rsrcBundleBaseName, plugin);
	}
}
