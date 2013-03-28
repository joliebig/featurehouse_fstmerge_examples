package org.firebirdsql.squirrel;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

final class FirebirdResources extends PluginResources
{
	interface IMenuResourceKeys
	{

		String FIREBIRD = "firebird";
	}

	FirebirdResources(String rsrcBundleBaseName, IPlugin plugin)
	{
		super(rsrcBundleBaseName, plugin);
	}
}
