package net.sourceforge.squirrel_sql.plugins.favs;

import java.io.IOException;

import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

public class SavedQueriesPlugin extends DefaultPlugin {
	private PluginResources _resources;
	private FoldersCache _cache;

	private interface IMenuResourceKeys {
		String QUERIES = "queries";
	}

	
	public String getInternalName() {
		return "favs";
	}

	
	public String getDescriptiveName() {
		return "Saved Queries Plugin";
	}

	
	public String getVersion() {
		return "0.1";
	}

	
	public String getAuthor() {
		return "Udi Ipalawatte";
	}

	
	@Override
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	
	@Override
	public String getHelpFileName()
	{
		return "readme.txt";
	}

	
	@Override
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	
	public void initialize() throws PluginException {
		super.initialize();
		IApplication app = getApplication();
		try {
			_cache = new FoldersCache(app, getPluginUserSettingsFolder());
		} catch (IOException ex) {
			throw new PluginException(ex);
		}
		_cache.load();

		_resources = new PluginResources("net.sourceforge.squirrel_sql.plugins.favs.saved_queries", this);

		ActionCollection coll = app.getActionCollection();

		coll.add(new DeleteSavedQueriesFolderAction(app, _resources));
		coll.add(new NewSavedQueriesFolderAction(app, _resources));
		coll.add(new OrganizeSavedQueriesAction(app, _resources, _cache));
		coll.add(new RenameSavedQueriesFolderAction(app, _resources));

		createMenu();
	}

	
	public void unload() {
		_cache.save();
		super.unload();
	}

	private void createMenu() {		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = _resources.createMenu(IMenuResourceKeys.QUERIES);
		_resources.addToMenu(coll.get(OrganizeSavedQueriesAction.class), menu);
		menu.addSeparator();

		app.addToMenu(IApplication.IMenuIDs.PLUGINS_MENU, menu);
	}
}


