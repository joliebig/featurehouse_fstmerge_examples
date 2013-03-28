package net.sourceforge.squirrel_sql.plugins.laf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JTabbedPane;

import com.jgoodies.looks.Options;

import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLException;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactoryAdapter;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactoryComponentCreatedEvent;
import net.sourceforge.squirrel_sql.client.plugin.DefaultPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.util.IdentifierFactory;

public class LAFPlugin extends DefaultPlugin
{
	
	private final static ILogger
			s_log = LoggerController.createLogger(LAFPlugin.class);

	
	static final String OLD_USER_PREFS_FILE_NAME = "LAFPrefs.xml";

	
	static final String USER_PREFS_FILE_NAME = "LAFPreferences.xml";

	
	private LAFPluginResources _resources;

	
	private LAFPreferences _lafPrefs;

	
	private LAFRegister _lafRegister;

	
	private File _lafFolder;

	
	private File _userSettingsFolder;

	
	private File _userExtraLAFFolder;

	
	private final XMLObjectCache<LAFPreferences> _settingsCache = new XMLObjectCache<LAFPreferences>();

	
	public String getInternalName()
	{
		return "laf";
	}

	
	public String getDescriptiveName()
	{
		return "Look & Feel Plugin";
	}

	
	public String getVersion()
	{
		return "1.1";
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
		return "readme.html";
	}

	
	public String getLicenceFileName()
	{
		return "licences.html";
	}

	
	public synchronized void load(IApplication app) throws PluginException
	{
		super.load(app);

		
		_resources = new LAFPluginResources(this);

		
		
		File pluginAppFolder = null;
		try
		{
			pluginAppFolder = getPluginAppSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		
		_lafFolder = new File(pluginAppFolder, "lafs");
		if (!_lafFolder.exists())
		{
			_lafFolder.mkdir();
		}

		
		try
		{
			_userSettingsFolder = getPluginUserSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		
		_userExtraLAFFolder = new File(_userSettingsFolder, ILAFConstants.USER_EXTRA_LAFS_FOLDER); 

		
		createEmptyRequiredUserFiles();

		
		loadPrefs();

		



		
		_lafRegister = new LAFRegister(app, this);

		
		UIFactory.getInstance().addListener(new UIFactoryListener());

		
		_lafRegister.updateStatusBarFont();
	}

	
	public void unload()
	{
		try
		{
			savePrefs(new File(_userSettingsFolder, USER_PREFS_FILE_NAME));
		}
		catch (IOException ex)
		{
			s_log.error("Error occured writing to preferences file: "
							+ USER_PREFS_FILE_NAME,
						ex);
		}
		catch (XMLException ex)
		{
			s_log.error("Error occured writing to preferences file: "
							+ USER_PREFS_FILE_NAME,
						ex);
		}
		super.unload();
	}

	
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return new IGlobalPreferencesPanel[]
			{
				new LAFPreferencesTab(this, _lafRegister),
				new LAFFontsTab(this, _lafRegister),
			};
	}

	
	File getLookAndFeelFolder()
	{
		return _lafFolder;
	}

	
	File getUsersExtraLAFFolder()
	{
		return _userExtraLAFFolder;
	}

	
	LAFPreferences getLAFPreferences()
	{
		return _lafPrefs;
	}

	
	PluginResources getResources()
	{
		return _resources;
	}

	XMLObjectCache<LAFPreferences> getSettingsCache()
	{
		return _settingsCache;
	}

	
	private void loadPrefs()
	{
		final File oldPrefsFile = new File(_userSettingsFolder, OLD_USER_PREFS_FILE_NAME);
		final File newPrefsFile = new File(_userSettingsFolder, USER_PREFS_FILE_NAME);
		final boolean oldExists = oldPrefsFile.exists();
		final boolean newExists = newPrefsFile.exists();

		try
		{
			if (oldExists)
			{
				loadOldPrefs(oldPrefsFile);
				try
				{
					_settingsCache.add(_lafPrefs);
				}
				catch (DuplicateObjectException ex)
				{
					s_log.error("LAFPreferences object already in cache", ex);
				}
				savePrefs(newPrefsFile);
				if (!oldPrefsFile.delete())
				{
					s_log.error("Unable to delete old LAF preferences file");
				}
				
			}
			else if (newExists)
			{
				loadNewPrefs(newPrefsFile);
			}
		}
		catch (IOException ex)
		{
			s_log.error("Error occured in preferences file", ex);
		}
		catch (XMLException ex)
		{
			s_log.error("Error occured in preferences file", ex);
		}

		
		if (_lafPrefs == null)
		{
			_lafPrefs = new LAFPreferences(IdentifierFactory.getInstance().createIdentifier());
         _lafPrefs.setLookAndFeelClassName(MetalLookAndFeelController.METAL_LAF_CLASS_NAME);
			try
			{
				_settingsCache.add(_lafPrefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("LAFPreferences object already in cache", ex);
			}
		}
	}

	
	private void loadOldPrefs(File oldPrefsFile) throws XMLException
	{
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(oldPrefsFile, getClass().getClassLoader());
			Iterator<?> it = doc.iterator();
			if (it.hasNext())
			{
				_lafPrefs = (LAFPreferences) it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			
		}
	}

	
	private void loadNewPrefs(File newPrefsFile) throws XMLException
	{
		try
		{
			try
			{
				_settingsCache.load(newPrefsFile.getPath(), getClass().getClassLoader());
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("Cache should have been empty", ex);
			}
			Iterator<LAFPreferences> it = 
                _settingsCache.getAllForClass(LAFPreferences.class);
			if (it.hasNext())
			{
				_lafPrefs = it.next();
			}
			else
			{
				s_log.error("LAFPreferences object not loaded");
			}
		}
		catch (FileNotFoundException ignore)
		{
			
		}
	}

	
	private void savePrefs(File prefsFile)
		throws IOException, XMLException
	{
		_settingsCache.save(prefsFile.getPath());
	}

	private void createEmptyRequiredUserFiles()
	{
		_userExtraLAFFolder.mkdirs();

		File file = new File(_userExtraLAFFolder, ILAFConstants.USER_EXTRA_LAFS_PROPS_FILE);
		try
		{
			file.createNewFile();
		}
		catch (IOException ex)
		{
			s_log.error("Error creating file " + file.getAbsolutePath(), ex);
		}
	}

	private static class UIFactoryListener extends UIFactoryAdapter
	{
		
		public void tabbedPaneCreated(UIFactoryComponentCreatedEvent evt)
		{
			final JTabbedPane pnl = (JTabbedPane)evt.getComponent();
			pnl.putClientProperty(Options.NO_CONTENT_BORDER_KEY, Boolean.TRUE);
		}
	}
}
