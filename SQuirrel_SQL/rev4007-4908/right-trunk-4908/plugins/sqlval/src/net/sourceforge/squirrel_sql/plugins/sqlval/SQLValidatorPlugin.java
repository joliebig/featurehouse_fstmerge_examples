package net.sourceforge.squirrel_sql.plugins.sqlval;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JMenu;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallbackAdaptor;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.ISQLPanelListener;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SQLPanelEvent;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.sqlval.action.ConnectAction;
import net.sourceforge.squirrel_sql.plugins.sqlval.action.DisconnectAction;
import net.sourceforge.squirrel_sql.plugins.sqlval.action.ValidateSQLAction;

public class SQLValidatorPlugin extends DefaultSessionPlugin
{
	
	private static final ILogger s_log =
		LoggerController.createLogger(SQLValidatorPlugin.class);

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SQLValidatorPlugin.class);    
        
	private interface IMenuResourceKeys
	{
		String SQLVAL = "sqlval";
	}

	
	private static final String USER_PREFS_FILE_NAME = "prefs.xml";

	private static final String PREFS_KEY = "sessionprefs";

	
	private WebServicePreferences _prefs;

	
	private File _userSettingsFolder;

	
	private PluginResources _resources;

	
	private ISQLPanelListener _lis = new SQLPanelListener();

	
	public String getInternalName()
	{
		return "sqlval";
	}

	
	public String getDescriptiveName()
	{
        
		return s_stringMgr.getString("sqlval.descriptivename");
	}

	
	public String getVersion()
	{
		return "0.13";
	}

	
	public String getAuthor()
	{
		return "Colin Bell";
	}

	
	public String getContributors()
	{
		return "Olof Edlund";
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

		_resources = new PluginResources(getClass().getName(), this);

		
		try
		{
			_userSettingsFolder = getPluginUserSettingsFolder();
		}
		catch (IOException ex)
		{
			throw new PluginException(ex);
		}

		
		loadPrefs();

		
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();
		coll.add(new ConnectAction(app, _resources, _prefs, this));
		coll.add(new DisconnectAction(app, _resources, _prefs, this));
		coll.add(new ValidateSQLAction(app, _resources, _prefs, this));
		createMenu();
	}

	
	public void unload()
	{
		savePrefs();
		super.unload();
	}

	
	public void sessionCreated(final ISession session)
	{
        super.sessionCreated(session);
        final WebServiceSessionProperties props = 
            new WebServiceSessionProperties(_prefs);
        session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                props.setSQLConnection(session.getSQLConnection());
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        session.putPluginObject(SQLValidatorPlugin.this, 
                                                PREFS_KEY, 
                                                props);
                    }
                });
            }
        });
    }

	
	public PluginSessionCallback sessionStarted(ISession session)
	{
      session.getSessionInternalFrame().getSQLPanelAPI().addSQLPanelListener(_lis);
      setupSQLEntryArea(session);

      return new PluginSessionCallbackAdaptor(this);
	}

	
	public void sessionEnding(ISession session)
	{
		session.getSessionInternalFrame().getSQLPanelAPI().removeSQLPanelListener(_lis);
        WebServiceSessionProperties wssp = getWebServiceSessionProperties(session);
        if (wssp != null) {
            WebServiceSession wss = wssp.getWebServiceSession();
            if (wss != null) {
                wss.close();
            }
        }
		session.removePluginObject(this, PREFS_KEY);
		super.sessionEnding(session);
	}

	
	public IGlobalPreferencesPanel[] getGlobalPreferencePanels()
	{
		return new IGlobalPreferencesPanel[]
		{
			new ValidatorGlobalPreferencesTab(_prefs),		};
	}

	PluginResources getResources()
	{
		return _resources;
	}

	public WebServiceSessionProperties getWebServiceSessionProperties(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		return (WebServiceSessionProperties)session.getPluginObject(this, PREFS_KEY);
	}

	private void setupSQLEntryArea(ISession session)
	{
		final ISQLPanelAPI api = session.getSessionInternalFrame().getSQLPanelAPI();
		final ActionCollection coll = getApplication().getActionCollection();
		api.addToSQLEntryAreaMenu(coll.get(ValidateSQLAction.class));
	}

	
	private void loadPrefs()
	{
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new File(_userSettingsFolder, USER_PREFS_FILE_NAME),
								getClass().getClassLoader());
			Iterator<?> it = doc.iterator();
			if (it.hasNext())
			{
				_prefs = (WebServicePreferences)it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			s_log.info(USER_PREFS_FILE_NAME + " not found - will be created");
		}
		catch (Exception ex)
		{
			s_log.error("Error occured reading from preferences file: "
					+ USER_PREFS_FILE_NAME, ex);
		}
		if (_prefs == null)
		{
			_prefs = new WebServicePreferences();
		}

		_prefs.setClientName(Version.getApplicationName() + "/" + getDescriptiveName());
		_prefs.setClientVersion(Version.getShortVersion() + "/" + getVersion());
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
			s_log.error("Error occured writing to preferences file: "
					+ USER_PREFS_FILE_NAME, ex);
		}
	}

	private void createMenu()
	{
		final IApplication app = getApplication();
		final ActionCollection coll = app.getActionCollection();

		final JMenu menu = _resources.createMenu(IMenuResourceKeys.SQLVAL);
		_resources.addToMenu(coll.get(ConnectAction.class), menu);
		_resources.addToMenu(coll.get(DisconnectAction.class), menu);
		_resources.addToMenu(coll.get(ValidateSQLAction.class), menu);

		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);
	}

	private class SQLPanelListener extends SQLPanelAdapter
	{
		public void sqlEntryAreaReplaced(SQLPanelEvent evt)
		{
			setupSQLEntryArea(evt.getSession());
		}
	}
}
