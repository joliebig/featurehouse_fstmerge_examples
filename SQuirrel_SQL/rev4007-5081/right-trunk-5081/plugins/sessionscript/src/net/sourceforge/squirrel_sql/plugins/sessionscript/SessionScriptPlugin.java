package net.sourceforge.squirrel_sql.plugins.sessionscript;


import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class SessionScriptPlugin extends DefaultSessionPlugin
{
	public static final String BUNDLE_BASE_NAME = "net.sourceforge.squirrel_sql.plugins.sessionscript.sessionscript";

	
	@SuppressWarnings("unused")
	private static ILogger s_log = LoggerController.createLogger(SessionScriptPlugin.class);

	
	@SuppressWarnings("unused")
	private File _pluginAppFolder;

	
	@SuppressWarnings("unused")
	private File _userSettingsFolder;

	
	private AliasScriptCache _cache;

	private PluginResources _resources;

	
	public String getInternalName()
	{
		return "sessionscript";
	}

	
	public String getDescriptiveName()
	{
		return "Session Scripts Plugin";
	}

	
	public String getVersion()
	{
		return "0.14";
	}

	
	public String getAuthor()
	{
		return "Colin Bell";
	}

	
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	
	public String getHelpFileName()
	{
		return "readme.txt";
	}

	
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	
	public synchronized void initialize() throws PluginException
	{
		super.initialize();
		IApplication app = getApplication();

		
		
		try
		{
			_pluginAppFolder = getPluginAppSettingsFolder();
		} catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		
		try
		{
			_userSettingsFolder = getPluginUserSettingsFolder();
		} catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		_resources = new SessionScriptResources(BUNDLE_BASE_NAME, this);

		ActionCollection coll = app.getActionCollection();
		ViewSessionScriptsAction action = new ViewSessionScriptsAction(app, _resources, this);
		coll.add(action);
		app.addToMenu(IApplication.IMenuIDs.PLUGINS_MENU, action);

		try
		{
			_cache = new AliasScriptCache(this);
		} catch (IOException ex)
		{
			throw new PluginException(ex);
		}
		_cache.load();

	}

	
	public void unload()
	{
		if (_cache != null)
		{
			_cache.save();
		}
		super.unload();
	}

	public PluginSessionCallback sessionStarted(final ISession session)
	{
		boolean rc = false;

		AliasScript script = _cache.get(session.getAlias());
		if (script != null)
		{
			final String sql = script.getSQL();
			if (sql != null && sql.length() > 0)
			{
				rc = true;
				final ISQLPanelAPI api = session.getSessionInternalFrame().getSQLPanelAPI();
				GUIUtils.processOnSwingEventThread(new Runnable()
				{
					public void run()
					{
						api.setEntireSQLScript(sql);
						session.getApplication().getThreadPool().addTask(new Runnable()
						{
							public void run()
							{
								api.executeCurrentSQL();
							}
						});
					}
				});
			}
		}

		if (false == rc)
		{
			return null;
		}
		return new PluginSessionCallbackAdaptor(this);
	}

	
	AliasScriptCache getScriptsCache()
	{
		return _cache;
	}
}
