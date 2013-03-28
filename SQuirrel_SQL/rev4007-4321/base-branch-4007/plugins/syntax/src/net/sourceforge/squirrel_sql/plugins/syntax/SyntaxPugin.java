package net.sourceforge.squirrel_sql.plugins.syntax;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.text.JTextComponent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginResources;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.preferences.INewSessionPropertiesPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.ISessionPropertiesPanel;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.FindAction;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.NetbeansSQLEditorPane;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.NetbeansSQLEntryPanel;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.ReplaceAction;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.SQLKit;
import net.sourceforge.squirrel_sql.plugins.syntax.netbeans.SQLSettingsInitializer;
import net.sourceforge.squirrel_sql.plugins.syntax.oster.OsterSQLEntryPanel;

import org.netbeans.editor.BaseKit;


public class SyntaxPugin extends DefaultSessionPlugin
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SyntaxPugin.class);

   static interface i18n
   {
      
      String TO_UPPER_CASE =
         s_stringMgr.getString("SyntaxPlugin.touppercase");
      
      String TO_LOWER_CASE =
         s_stringMgr.getString("SyntaxPlugin.tolowercase");
      
      String FIND = s_stringMgr.getString("SyntaxPlugin.find");
      
      String REPLACE = s_stringMgr.getString("SyntaxPlugin.replace");
      
      String AUTO_CORR = s_stringMgr.getString("SyntaxPlugin.autocorr");
      
      String DUP_LINE = s_stringMgr.getString("SyntaxPlugin.duplicateline");
      
      String COMMENT = s_stringMgr.getString("SyntaxPlugin.comment");
      
      String UNCOMMENT = s_stringMgr.getString("SyntaxPlugin.uncomment");

   }

   
	private static final ILogger s_log = LoggerController.createLogger(SyntaxPugin.class);

	
	private SyntaxPreferences _newSessionPrefs;

	
	private File _userSettingsFolder;

	
	private SQLEntryPanelFactoryProxy _sqlEntryFactoryProxy;

	
	private Map<IIdentifier, SessionPreferencesListener> _prefListeners = 
	    new HashMap<IIdentifier, SessionPreferencesListener>();

	
	private SyntaxPluginResources _resources;
	private AutoCorrectProviderImpl _autoCorrectProvider;

	private interface IMenuResourceKeys
	{
		String MENU = "syntax";
	}


	
	public String getInternalName()
	{
		return "syntax";
	}

	
	public String getDescriptiveName()
	{
		return "Syntax Highlighting Plugin";
	}

	
	public String getVersion()
	{
		return "1.0";
	}

	
	public String getAuthor()
	{
		return "Gerd Wagner, Colin Bell";
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

		_resources = new SyntaxPluginResources(this);

		
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
		final ISQLEntryPanelFactory originalFactory = app.getSQLEntryPanelFactory();
		

		_sqlEntryFactoryProxy = new SQLEntryPanelFactoryProxy(this, originalFactory);

		app.setSQLEntryPanelFactory(_sqlEntryFactoryProxy);

		_autoCorrectProvider = new AutoCorrectProviderImpl(_userSettingsFolder);

		createMenu();
	}

	private void createMenu()
	{
		IApplication app = getApplication();
		ActionCollection coll = app.getActionCollection();

		JMenu menu = _resources.createMenu(IMenuResourceKeys.MENU);
		app.addToMenu(IApplication.IMenuIDs.SESSION_MENU, menu);

		Action act = new ConfigureAutoCorrectAction(app, _resources, this);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new FindAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new ReplaceAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new DuplicateLineAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new CommentAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

		act = new UncommentAction(getApplication(), _resources);
		coll.add(act);
		_resources.addToMenu(act, menu);

	}


	
	public void unload()
	{
		savePrefs();
		super.unload();
	}

	
	public void sessionCreated(ISession session)
	{
		SyntaxPreferences prefs = null;

		try
		{
			prefs = (SyntaxPreferences)_newSessionPrefs.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError("CloneNotSupportedException for SyntaxPreferences");
		}

		session.putPluginObject(this, IConstants.ISessionKeys.PREFS, prefs);

		SessionPreferencesListener lis = 
		    new SessionPreferencesListener(this, session);
		prefs.addPropertyChangeListener(lis);
		_prefListeners.put(session.getIdentifier(), lis);
	}


	public PluginSessionCallback sessionStarted(final ISession session)
	{
		PluginSessionCallback ret = new PluginSessionCallback()
		{
			public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame, ISession sess)
			{
				initSqlInternalFrame(sqlInternalFrame);
			}

			public void objectTreeInternalFrameOpened(ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess)
			{
			}
		};

      initSessionSheet(session);

		return ret;
	}

   private void initSessionSheet(ISession session)
   {
      ActionCollection coll = getApplication().getActionCollection();
      session.addSeparatorToToolbar();
      session.addToToolbar(coll.get(FindAction.class));
      session.addToToolbar(coll.get(ReplaceAction.class));
      session.addToToolbar(coll.get(ConfigureAutoCorrectAction.class));

      SessionInternalFrame sif = session.getSessionInternalFrame();

      ISQLPanelAPI sqlPanelAPI = sif.getSQLPanelAPI();
      ISQLEntryPanel sep = sqlPanelAPI.getSQLEntryPanel();
      JComponent septc = sep.getTextComponent();

      new ToolsPopupHandler(this).initToolsPopup(sif, coll);

      JMenuItem mnuComment = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(CommentAction.class));
      JMenuItem mnuUncomment = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(UncommentAction.class));
      _resources.configureMenuItem(coll.get(CommentAction.class), mnuComment);
      _resources.configureMenuItem(coll.get(UncommentAction.class), mnuUncomment);
   }

   private void initSqlInternalFrame(SQLInternalFrame sqlInternalFrame)
	{
		ActionCollection coll = getApplication().getActionCollection();
		FindAction findAction = ((FindAction) coll.get(FindAction.class));
		ReplaceAction replaceAction = (ReplaceAction) coll.get(ReplaceAction.class);

		sqlInternalFrame.addSeparatorToToolbar();
		sqlInternalFrame.addToToolbar(findAction);
		sqlInternalFrame.addToToolbar(replaceAction);
		sqlInternalFrame.addToToolbar(coll.get(ConfigureAutoCorrectAction.class));

      new ToolsPopupHandler(this).initToolsPopup(sqlInternalFrame, coll);

		ISQLPanelAPI sqlPanelAPI = sqlInternalFrame.getSQLPanelAPI();

		JMenuItem mnuComment = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(CommentAction.class));
		JMenuItem mnuUncomment = sqlPanelAPI.addToSQLEntryAreaMenu(coll.get(UncommentAction.class));
		_resources.configureMenuItem(coll.get(CommentAction.class), mnuComment);
		_resources.configureMenuItem(coll.get(UncommentAction.class), mnuUncomment);

	}



   
	public void sessionEnding(ISession session)
	{
		super.sessionEnding(session);

		session.removePluginObject(this, IConstants.ISessionKeys.PREFS);
		_prefListeners.remove(session.getIdentifier());
		_sqlEntryFactoryProxy.sessionEnding(session);
	}

	
	public INewSessionPropertiesPanel[] getNewSessionPropertiesPanels()
	{
		return new INewSessionPropertiesPanel[]
		{
			new SyntaxPreferencesPanel(_newSessionPrefs, _resources)
		};
	}

	
	public ISessionPropertiesPanel[] getSessionPropertiesPanels(ISession session)
	{
		SyntaxPreferences sessionPrefs = (SyntaxPreferences)session.getPluginObject(this,
											IConstants.ISessionKeys.PREFS);

		return new ISessionPropertiesPanel[]
		{
			new SyntaxPreferencesPanel(sessionPrefs, _resources)
		};
	}

	SyntaxPluginResources getResources()
	{
		return _resources;
	}

	ISQLEntryPanelFactory getSQLEntryAreaFactory()
	{
		return _sqlEntryFactoryProxy;
	}

	
	private void loadPrefs()
	{
		try
		{
			final XMLBeanReader doc = new XMLBeanReader();
			final File file = new File(_userSettingsFolder,
					IConstants.USER_PREFS_FILE_NAME);
			doc.load(file, getClass().getClassLoader());

			Iterator<?> it = doc.iterator();

			if (it.hasNext())
			{
				_newSessionPrefs = (SyntaxPreferences)it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			
		}
		catch (Exception ex)
		{
			final String msg = "Error occured reading from preferences file: " +
				IConstants.USER_PREFS_FILE_NAME;
			s_log.error(msg, ex);
		}

		if (_newSessionPrefs == null)
		{
			_newSessionPrefs = new SyntaxPreferences();
		}
	}

	
	private void savePrefs()
	{
		try
		{
			final XMLBeanWriter wtr = new XMLBeanWriter(_newSessionPrefs);
			wtr.save(new File(_userSettingsFolder, IConstants.USER_PREFS_FILE_NAME));
		}
		catch (Exception ex)
		{
			final String msg = "Error occured writing to preferences file: " +
								IConstants.USER_PREFS_FILE_NAME;
			s_log.error(msg, ex);
		}
	}


	public Object getExternalService()
	{
		return getAutoCorrectProviderImpl();
	}


	public AutoCorrectProviderImpl getAutoCorrectProviderImpl()
	{
		return _autoCorrectProvider;
	}

	private static final class SessionPreferencesListener
		implements PropertyChangeListener
	{
		private SyntaxPugin _plugin;
		private ISession _session;

		SessionPreferencesListener(SyntaxPugin plugin, ISession session)
		{
			super();
			_plugin = plugin;
			_session = session;
		}

		public void propertyChange(PropertyChangeEvent evt)
		{
			String propName = evt.getPropertyName();

			if(   false == SyntaxPreferences.IPropertyNames.USE_NETBEANS_CONTROL.equals(propName)
				&& false == SyntaxPreferences.IPropertyNames.USE_OSTER_CONTROL.equals(propName) )
			{

				
				
				Object pluginObject = _session.getPluginObject(_plugin, IConstants.ISessionKeys.SQL_ENTRY_CONTROL);

				if(pluginObject instanceof NetbeansSQLEntryPanel)
				{
					((NetbeansSQLEntryPanel)pluginObject).updateFromPreferences();
				}

				if(pluginObject instanceof OsterSQLEntryPanel)
				{
					((OsterSQLEntryPanel)pluginObject).updateFromPreferences();
				}
			}
			else
			{
				

				String msg =
					
					s_stringMgr.getString("syntax.switchingNotSupported");

				JOptionPane.showMessageDialog(_session.getApplication().getMainFrame(), msg);

				throw new SyntaxPrefChangeNotSupportedException();

			}

		}
	}
}
