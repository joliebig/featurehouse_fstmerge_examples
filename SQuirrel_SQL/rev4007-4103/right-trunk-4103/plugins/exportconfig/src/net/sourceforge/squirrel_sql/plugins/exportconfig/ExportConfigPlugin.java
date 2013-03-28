package net.sourceforge.squirrel_sql.plugins.exportconfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;

import net.sourceforge.squirrel_sql.plugins.exportconfig.action.ExportAliasesAction;
import net.sourceforge.squirrel_sql.plugins.exportconfig.action.ExportConfigurationAction;
import net.sourceforge.squirrel_sql.plugins.exportconfig.action.ExportDriversAction;
import net.sourceforge.squirrel_sql.plugins.exportconfig.action.ExportSettingsAction;

public class ExportConfigPlugin extends DefaultPlugin
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportConfigPlugin.class);


	
	private final static ILogger s_log = LoggerController.createLogger(ExportConfigPlugin.class);

	
	private static final String USER_PREFS_FILE_NAME = "prefs.xml";

	
	private File _userSettingsFolder;

	
	private PluginResources _resources;

	
	private JMenu _exportMenu;

	
	private ExportConfigPreferences _prefs;

	
	public String getInternalName()
	{
		return "exportconfig";
	}

	
	public String getDescriptiveName()
	{
		return "Export Configuration Plugin";
	}

	
	public String getVersion()
	{
		return "0.10";
	}

	
	public String getAuthor()
	{
		return "Colin Bell";
	}

    
    public String getContributors()
    {
        return "Rob Manning";
    }

	
	public synchronized void load(IApplication app) throws PluginException
	{
		super.load(app);

		
		try
		{
			_userSettingsFolder = getPluginUserSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		_resources = new ExportConfigResources(getClass().getName(), this);
	}

	
	public String getChangeLogFileName()
	{
		return "changes.txt";
	}

	
	public String getHelpFileName()
	{
		return "readme.html";
	}

	
	public String getLicenceFileName()
	{
		return "licence.txt";
	}

	
	public synchronized void initialize() throws PluginException
	{
		super.initialize();

		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		coll.add(new ExportAliasesAction(app, _resources, this));
		coll.add(new ExportConfigurationAction(app, _resources, this));
		coll.add(new ExportDriversAction(app, _resources, this));
		coll.add(new ExportSettingsAction(app, _resources, this));

		
		loadPrefs();

		_exportMenu = createExportMenu();
		app.addToMenu(IApplication.IMenuIDs.PLUGINS_MENU, _exportMenu);
	}

	
	public void unload()
	{
		savePrefs();
		super.unload();
	}

	
	public ExportConfigPreferences getPreferences()
	{
		return _prefs;
	}

	
	private void loadPrefs()
	{
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new File(_userSettingsFolder, USER_PREFS_FILE_NAME),
								getClass().getClassLoader());
			final Iterator<?> it = doc.iterator();
			if (it.hasNext())
			{
				_prefs = (ExportConfigPreferences)it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			
			s_log.info(s_stringMgr.getString("exportconfig.fileWillBeCreated", USER_PREFS_FILE_NAME));
		}
		catch (Exception ex)
		{
			
			s_log.error(s_stringMgr.getString("exportconfig.errorCreatingFile", USER_PREFS_FILE_NAME), ex);
		}
		if (_prefs == null)
		{
			_prefs = new ExportConfigPreferences();
		}
	}

	
	private void savePrefs()
	{
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(_prefs);
			wtr.save(new File(_userSettingsFolder, USER_PREFS_FILE_NAME));
		}
		catch (Exception ex)
		{
			
			s_log.error(s_stringMgr.getString("exportconfig.errorWritingPrefs", USER_PREFS_FILE_NAME), ex);
		}
	}

	
	private JMenu createExportMenu()
	{
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final JMenu exportMenu = _resources.createMenu(ExportConfigResources.IMenuResourceKeys.EXPORT);
		_resources.addToMenu(coll.get(ExportConfigurationAction.class), exportMenu);
		_resources.addToMenu(coll.get(ExportAliasesAction.class), exportMenu);
		_resources.addToMenu(coll.get(ExportDriversAction.class), exportMenu);
		_resources.addToMenu(coll.get(ExportSettingsAction.class), exportMenu);

		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, exportMenu);

		return exportMenu;
	}
}
