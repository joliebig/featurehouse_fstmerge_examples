package net.sourceforge.squirrel_sql.plugins.mssql;



import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

final class MssqlResources extends PluginResources {
	interface IMenuResourceKeys {
        String SHOW_STATISTICS = "show_statistics";
        String INDEXDEFRAG = "indexdefrag";
        String SHRINKDBFILE = "shrinkdbfile";
        String MSSQL = "mssql";
	}

	MssqlResources(String rsrcBundleBaseName, IPlugin plugin) {
		super(rsrcBundleBaseName, plugin);
	}
}
