package net.sourceforge.squirrel_sql.client;



import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.util.*;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.PopupFactory;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.client.gui.FileViewerFactory;
import net.sourceforge.squirrel_sql.client.gui.SplashScreen;
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.gui.db.DataCache;
import net.sourceforge.squirrel_sql.client.gui.laf.AllBluesBoldMetalTheme;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.mainframe.action.ConnectToStartupAliasesCommand;
import net.sourceforge.squirrel_sql.client.mainframe.action.ViewHelpCommand;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginLoadInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.PreferenceType;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanelFactory;
import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistory;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLHistoryItem;
import net.sourceforge.squirrel_sql.client.session.properties.EditWhereCols;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfoCacheSerializer;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.datasetviewer.CellImportExportInfoSaver;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DTProperties;
import net.sourceforge.squirrel_sql.fw.gui.CursorChanger;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.BareBonesBrowserLaunch;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.ProxyHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.TaskThreadPool;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanReader;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

class Application implements IApplication
{
	
	private static ILogger s_log;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(Application.class);

	private SquirrelPreferences _prefs;
	private SQLDriverManager _driverMgr;
	private DataCache _cache;
	private ActionCollection _actions;

	


	
	private IPluginManager _pluginManager;

	private final DummyAppPlugin _dummyPlugin = new DummyAppPlugin();

	private SquirrelResources _resources;

	
	private final TaskThreadPool _threadPool = new TaskThreadPool();

	
	private SessionManager _sessionManager;

	
	private WindowManager _windowManager;

	private LoggerController _loggerFactory;

	
	private ISQLEntryPanelFactory _sqlEntryFactory = new DefaultSQLEntryPanelFactory();

	
	private PrintStream _jdbcDebugOutputStream;

	
	private PrintWriter _jdbcDebugOutputWriter;

	
	private final FontInfoStore _fontInfoStore = new FontInfoStore();

	
	private SQLHistory _sqlHistory;

	
	private int _jdbcDebugType = SquirrelPreferences.IJdbcDebugTypes.NONE;

   
   private ApplicationFiles _appFiles = null;

   private EditWhereCols editWhereCols = new EditWhereCols();

   private List<ApplicationListener> _listeners = new ArrayList<ApplicationListener>();

   
	Application()
	{
		super();
	}

	
	public void startup()
	{
		LoggerController.registerLoggerFactory(new SquirrelLoggerFactory(true));
		s_log = LoggerController.createLogger(getClass());

		EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
		q.push(
			new EventQueue()
			{
				protected void dispatchEvent(AWTEvent event)
				{
					try
					{
						super.dispatchEvent(event);
					}
					catch (Throwable t)
					{
                        if (s_log.isDebugEnabled()) {
                            t.printStackTrace();
                        }
						s_log.error("Exception occured dispatching Event " + event, t);
					}
				}
			}
		);

		final ApplicationArguments args = ApplicationArguments.getInstance();

		
		setupLookAndFeel(args);

        editWhereCols.setApplication(this);
        


		_resources = new SquirrelResources("net.sourceforge.squirrel_sql.client.resources.squirrel");
        _prefs = SquirrelPreferences.load();
        Locale.setDefault(constructPreferredLocale(_prefs));
        preferencesHaveChanged(null);
        _prefs.addPropertyChangeListener(
            new PropertyChangeListener()
            {
                public void propertyChange(PropertyChangeEvent evt)
                {
                    preferencesHaveChanged(evt);
                }
            });
        
		SplashScreen splash = null;
		if (args.getShowSplashScreen())
		{
			splash = new SplashScreen(_resources, 15, _prefs);
		}

		try
		{
			CursorChanger chg = null;
			if (splash != null)
			{
				chg = new CursorChanger(splash);
				chg.show();
			}
			try
			{
				executeStartupTasks(splash, args);
			}
			finally
			{
				if (chg != null)
				{
					chg.restore();
				}
			}
		}
		finally
		{
			if (splash != null)
			{
				splash.dispose();
			}
		}
	}

    
	
	public boolean shutdown()
	{
		s_log.info(s_stringMgr.getString("Application.shutdown",
									Calendar.getInstance().getTime()));

		saveApplicationState();

        if (!closeAllSessions()) {
            return false;
        }
        _pluginManager.unloadPlugins();
        
        closeAllViewers();
        
        closeOutputStreams();
        
        SchemaInfoCacheSerializer.waitTillStoringIsDone();

        String msg = s_stringMgr.getString("Application.shutdowncomplete",
										   Calendar.getInstance().getTime());
		s_log.info(msg);
		LoggerController.shutdown();

		return true;
	}

    
    public void saveApplicationState() {

      _prefs.setFirstRun(false);


      for (ApplicationListener l : _listeners.toArray(new ApplicationListener[0]))
      {
         l.saveApplicationState();
      }

      saveDrivers();

		saveAliases();

		
		saveSQLHistory();

		
		saveCellImportExportInfo();

		
		saveEditWhereColsInfo();

		
		saveDataTypePreferences();

       _prefs.save();
    }


    
    private Locale constructPreferredLocale(SquirrelPreferences prefs) {
   	 String langCountryPair = prefs.getPreferredLocale();
   	 if (StringUtilities.isEmpty(langCountryPair)) {
   		 langCountryPair = "en_US";
   	 }
   	 String[] parts = langCountryPair.split("_");
   	 if (parts.length == 2) {
   		 return new Locale(parts[0], parts[1]);
   	 } 
   	 return new Locale(parts[0]); 
    }
    
    
    private void closeOutputStreams() {
        if (_jdbcDebugOutputStream != null)
        {
            _jdbcDebugOutputStream.close();
            _jdbcDebugOutputStream = null;
        }

        if (_jdbcDebugOutputWriter != null)
        {
            _jdbcDebugOutputWriter.close();
            _jdbcDebugOutputWriter = null;
        }
    }

    
    private void saveAliases() {
        try
		{
			final File file = _appFiles.getDatabaseAliasesFile();
			_cache.saveAliases(file);
		}
		catch (Throwable th)
		{
			String thMsg = th.getMessage();
			if (thMsg == null)
			{
				thMsg = "";
			}
			String msg = s_stringMgr.getString("Application.error.aliassave",
												th.getMessage());
			showErrorDialog(msg, th);
			s_log.error(msg, th);
		}
    }

    
    private void saveDrivers() {
        try
		{
			final File file = _appFiles.getDatabaseDriversFile();
			_cache.saveDrivers(file);
		}
		catch (Throwable th)
		{
			String msg = s_stringMgr.getString("Application.error.driversave",
												th.getMessage());
			showErrorDialog(msg, th);
			s_log.error(msg, th);
		}
    }

    
    private void closeAllViewers() {
        try {
            FileViewerFactory.getInstance().closeAllViewers();
        } catch (Throwable t){
            
            s_log.error(s_stringMgr.getString("Application.error.closeFileViewers"), t);
        }
    }

	
	private boolean closeAllSessions() {
	    boolean result = false;
	    try
	    {
	        if (!_sessionManager.closeAllSessions())
	        {
	            s_log.info(s_stringMgr.getString("Application.shutdownCancelled",
	                    Calendar.getInstance().getTime()));
	        } else {
	            result = true;
	        }
	    }
	    catch (Throwable t)
	    {
	        String msg =
	            s_stringMgr.getString("Application.error.closeAllSessions",
	                    t.getMessage());
	        s_log.error(msg, t);
	    }
	    return result;
	}

	public IPluginManager getPluginManager()
	{
		return _pluginManager;
	}

	
	public WindowManager getWindowManager()
	{
		return _windowManager;
	}

	public ActionCollection getActionCollection()
	{
		return _actions;
	}

	public SQLDriverManager getSQLDriverManager()
	{
		return _driverMgr;
	}

	public DataCache getDataCache()
	{
		return _cache;
	}

	public IPlugin getDummyAppPlugin()
	{
		return _dummyPlugin;
	}

	public SquirrelResources getResources()
	{
		return _resources;
	}

	public IMessageHandler getMessageHandler()
	{
		return getMainFrame().getMessagePanel();
	}

	public SquirrelPreferences getSquirrelPreferences()
	{
		return _prefs;
	}

	public MainFrame getMainFrame()
	{

		return _windowManager.getMainFrame();
	}

	
	public SessionManager getSessionManager()
	{
		return _sessionManager;
	}

	
	public void showErrorDialog(String msg)
	{
		new ErrorDialog(getMainFrame(), msg).setVisible(true);
	}

	
	public void showErrorDialog(Throwable th)
	{
		new ErrorDialog(getMainFrame(), th).setVisible(true);
	}

	
	public void showErrorDialog(String msg, Throwable th)
	{
		new ErrorDialog(getMainFrame(), msg, th).setVisible(true);
	}

	
	public FontInfoStore getFontInfoStore()
	{
		return _fontInfoStore;
	}

	
	public TaskThreadPool getThreadPool()
	{
		return _threadPool;
	}

	public LoggerController getLoggerFactory()
	{
		return _loggerFactory;
	}

	
	public ISQLEntryPanelFactory getSQLEntryPanelFactory()
	{
		return _sqlEntryFactory;
	}

	
	public void setSQLEntryPanelFactory(ISQLEntryPanelFactory factory)
	{
		_sqlEntryFactory = factory != null ? factory : new DefaultSQLEntryPanelFactory();
	}

	
	public SQLHistory getSQLHistory()
	{
		return _sqlHistory;
	}

	public synchronized void addToMenu(int menuId, JMenu menu)
	{
		final MainFrame mf = getMainFrame();
		if (mf != null)
		{
			mf.addToMenu(menuId, menu);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.menuadding"));
		}
	}

	public synchronized void addToMenu(int menuId, Action action)
	{
		final MainFrame mf = getMainFrame();
		if (mf != null)
		{
			mf.addToMenu(menuId, action);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.menuadding"));
		}
	}

	
	public void addToStatusBar(JComponent comp)
	{
		final MainFrame mf = getMainFrame();
		if (mf != null)
		{
			mf.addToStatusBar(comp);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.compadding"));
		}
	}

	
	public void removeFromStatusBar(JComponent comp)
	{
		final MainFrame mf = getMainFrame();
		if (mf != null)
		{
			mf.removeFromStatusBar(comp);
		}
		else
		{
			throw new IllegalStateException(s_stringMgr.getString("Application.error.compremoving"));
		}
	}

    
    public void openURL(String url) {
        BareBonesBrowserLaunch.openURL(url);
    }
    
	
	private void executeStartupTasks(SplashScreen splash, ApplicationArguments args)
	{
		if (args == null)
		{
			throw new IllegalArgumentException("ApplicationArguments == null");
		}

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createSessionManager"));


		_sessionManager = new SessionManager(this);

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingprefs"));
		

		final boolean loadPlugins = args.getLoadPlugins();
		if (loadPlugins)
		{
			indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingplugins"));
		}
		else
		{
			indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.notloadingplugins"));
		}

      UIFactory.initialize(_prefs, this);
      _pluginManager = new PluginManager(this);
		if (args.getLoadPlugins())
		{
            if (null != splash && _prefs.getShowPluginFilesInSplashScreen())
				{
                ClassLoaderListener listener = splash.getClassLoaderListener();
                _pluginManager.setClassLoaderListener(listener);
            }
			_pluginManager.loadPlugins();
		}

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingactions"));
		_actions = new ActionCollection(this);

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadinguseracc"));
		_actions.loadActionKeys(_prefs.getActionKeys());

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createjdbcmgr"));
		_driverMgr = new SQLDriverManager();

		
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadingjdbc"));
		_appFiles = new ApplicationFiles();

      String errMsg = FileTransformer.transform(_appFiles);
      if(null != errMsg)
      {
         System.err.println(errMsg);
         JOptionPane.showMessageDialog(null, errMsg, "SQuirreL failed to start", JOptionPane.ERROR_MESSAGE);
         System.exit(-1);
      }

      _cache = new DataCache(_driverMgr, 
                             _appFiles.getDatabaseDriversFile(),
                             _appFiles.getDatabaseAliasesFile(),
                             _resources.getDefaultDriversUrl(),
                             this);

		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.createWindowManager"));
		_windowManager = new WindowManager(this, args.getUserInterfaceDebugEnabled());



		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.uifactoryinit"));



		String initializingPlugins = 
            s_stringMgr.getString("Application.splash.initializingplugins");
        String notloadingplugins =
            s_stringMgr.getString("Application.splash.notloadingplugins");
        String task = (loadPlugins ? initializingPlugins : notloadingplugins);
		indicateNewStartupTask(splash, task);
		if (loadPlugins)
		{
			_pluginManager.initializePlugins();
			for (Iterator<PluginLoadInfo> it = 
                _pluginManager.getPluginLoadInfoIterator(); it.hasNext();)
			{
				PluginLoadInfo pli = it.next();
				long created = pli.getCreationTime();
				long load = pli.getLoadTime();
				long init = pli.getInitializeTime();
                Object[] params = new Object[] { pli.getInternalName(),
                                                 Long.valueOf(created),
                                                 Long.valueOf(load),
                                                 Long.valueOf(init),
                                                 Long.valueOf(created + load + init)
                };
                String pluginLoadMsg = 
                    s_stringMgr.getString("Application.splash.loadplugintime",
                                          params);
				s_log.info(pluginLoadMsg);
			}
		}

        
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadsqlhistory"));
		loadSQLHistory();

        
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadcellselections"));
		loadCellImportExportInfo();

        
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loadeditselections"));
		loadEditWhereColsInfo();

        
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.loaddatatypeprops"));
		loadDTProperties();

        
		indicateNewStartupTask(splash, s_stringMgr.getString("Application.splash.showmainwindow"));
		_windowManager.moveToFront(_windowManager.getMainFrame());
      _threadPool.setParentForMessages(_windowManager.getMainFrame());





		new ConnectToStartupAliasesCommand(this).execute();

		if (_prefs.isFirstRun())
		{
			try
			{
				new ViewHelpCommand(this).execute();
			}
			catch (BaseException ex)
			{
                
				s_log.error(s_stringMgr.getString("Application.error.showhelpwindow"), ex);
			}
		}
	}

	
	private void indicateNewStartupTask(SplashScreen splash,
										String taskDescription)
	{
		if (splash != null)
		{
			splash.indicateNewTask(taskDescription);
		}
	}

	private void preferencesHaveChanged(PropertyChangeEvent evt)
	{
		final String propName = evt != null ? evt.getPropertyName() : null;

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.SHOW_TOOLTIPS))
		{
			ToolTipManager.sharedInstance().setEnabled(_prefs.getShowToolTips());
		}

		if (propName == null || propName.equals(
					SquirrelPreferences.IPropertyNames.JDBC_DEBUG_TYPE))
		{
			setupJDBCLogging();
		}

		if (propName == null || propName.equals(SquirrelPreferences.IPropertyNames.LOGIN_TIMEOUT))
		{
			DriverManager.setLoginTimeout(_prefs.getLoginTimeout());
		}

		if (propName == null || propName == SquirrelPreferences.IPropertyNames.PROXY)
		{
			new ProxyHandler().apply(_prefs.getProxySettings());
		}
	}

	
    @SuppressWarnings("unchecked")
	private void loadSQLHistory()
	{
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getUserSQLHistoryFile());
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				_sqlHistory = (SQLHistory)it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			
		}
		catch (Exception ex)
		{
            
			s_log.error(s_stringMgr.getString("Application.error.loadsqlhistory"), ex);
		}
		finally
		{
			if (_sqlHistory == null)
			{
				_sqlHistory = new SQLHistory();
			}
		}
	}

	
	private void saveSQLHistory()
	{
		
		try
		{
         if(_prefs.getSessionProperties().getLimitSQLEntryHistorySize());
         {
            SQLHistoryItem[] data = _sqlHistory.getData();

            int maxSize = _prefs.getSessionProperties().getSQLEntryHistorySize();
            if(data.length > maxSize)
            {
               SQLHistoryItem[] reducedData = new SQLHistoryItem[maxSize];
               System.arraycopy(data, data.length - maxSize, reducedData, 0, maxSize);
               _sqlHistory.setData(reducedData);
            }
         }
         

			XMLBeanWriter wtr = new XMLBeanWriter(_sqlHistory);
			wtr.save(new ApplicationFiles().getUserSQLHistoryFile());
		}
		catch (Exception ex)
		{
            
			s_log.error(s_stringMgr.getString("Application.error.savesqlhistory"), ex);
		}
	}


	
    @SuppressWarnings("unchecked")
	private void loadCellImportExportInfo()
	{
		CellImportExportInfoSaver saverInstance = null;
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getCellImportExportSelectionsFile());
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				saverInstance = (CellImportExportInfoSaver)it.next();
			}
		}
		catch (FileNotFoundException ignore)
		{
			
		}
		catch (Exception ex)
		{
            
			s_log.error(s_stringMgr.getString("Application.error.loadcellselections"), ex);
		}
		finally
		{
			
			
			CellImportExportInfoSaver.setInstance(saverInstance);
		}
	}

	
	private void saveCellImportExportInfo()
	{
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(CellImportExportInfoSaver.getInstance());
			wtr.save(new ApplicationFiles().getCellImportExportSelectionsFile());
		}
		catch (Exception ex)
		{
            
			s_log.error(s_stringMgr.getString("Application.error.writecellselections"), ex);
		}
	}

	
    @SuppressWarnings("all")
	private void loadEditWhereColsInfo()
	{
		
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getEditWhereColsFile());
			Iterator it = doc.iterator();
			if (it.hasNext())
			{
				editWhereCols = (EditWhereCols)it.next();
                editWhereCols.setApplication(this);
			}
		}
		catch (FileNotFoundException ignore)
		{
			
		}
		catch (Exception ex)
		{
            
			s_log.error(s_stringMgr.getString("Application.error.loadcolsinfo"), ex);
		}
		finally
		{
			
		}
	}

	
	private void saveEditWhereColsInfo()
	{
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(editWhereCols);
			wtr.save(new ApplicationFiles().getEditWhereColsFile());
		}
		catch (Exception ex)
		{
		    
			s_log.error(s_stringMgr.getString("Application.error.savecolsinfo"), ex);
		}
	}

	
    @SuppressWarnings("all")
	private void loadDTProperties()
	{
		DTProperties saverInstance = null;
		try
		{
			XMLBeanReader doc = new XMLBeanReader();
			doc.load(new ApplicationFiles().getDTPropertiesFile());
			Iterator<Object> it = doc.iterator();
			if (it.hasNext())
			{
				saverInstance = (DTProperties)it.next();
				DTProperties x = saverInstance;
			}
		}
		catch (FileNotFoundException ignore)
		{
			
		}
		catch (Exception ex)
		{
            
			s_log.error(s_stringMgr.getString("Application.error.loaddatatypeprops"), ex);
		}
		finally
		{
			
		}
	}

	
	private void saveDataTypePreferences()
	{
		try
		{
			XMLBeanWriter wtr = new XMLBeanWriter(new DTProperties());
			wtr.save(new ApplicationFiles().getDTPropertiesFile());
		}
		catch (Exception ex)
		{
            
			s_log.error(s_stringMgr.getString("Application.error.savedatatypeprops"), ex);
		}
	}

    
    public void savePreferences(PreferenceType preferenceType) {
        if (!_prefs.getSavePreferencesImmediately()) {
            return;
        }
        switch (preferenceType) {
            case ALIAS_DEFINITIONS:
                saveAliases();
                break;
            case DRIVER_DEFINITIONS:
                saveDrivers();
                break;
            case DATATYPE_PREFERENCES:
                saveDataTypePreferences();
                break;
            case CELLIMPORTEXPORT_PREFERENCES:
                saveCellImportExportInfo();
                break;
            case SQLHISTORY:
                saveSQLHistory();
                break;
            case EDITWHERECOL_PREFERENCES:
                saveEditWhereColsInfo();
                break;
            default:
                s_log.error("Unknown preference type: "+preferenceType);
        }
    }

   public void addApplicationListener(ApplicationListener l)
   {
      _listeners.add(l);
   }

   public void removeApplicationListener(ApplicationListener l)
   {
      _listeners.remove(l);
   }

   
	private void setupLookAndFeel(ApplicationArguments args)
	{
	    
	    String userSpecifiedOverride = System.getProperty("swing.defaultlaf");
	    if (userSpecifiedOverride != null 
	            && !"".equals(userSpecifiedOverride)) 
	    {
	        return;
	    }
	    
		String lafClassName = args.useNativeLAF()
					? UIManager.getSystemLookAndFeelClassName()
					: MetalLookAndFeel.class.getName();

		if (!args.useDefaultMetalTheme())
		{
			MetalLookAndFeel.setCurrentTheme(new AllBluesBoldMetalTheme());
		}

		try
		{
            
            
            
            
            
            
            PopupFactory.setSharedInstance(new PopupFactory());
            
			UIManager.setLookAndFeel(lafClassName);
		}
		catch (Exception ex)
		{
            
			s_log.error(s_stringMgr.getString("Application.error.setlaf"), ex);
		}
	}

    @SuppressWarnings("deprecation")
	private void setupJDBCLogging()
	{
		
		if (_jdbcDebugType != _prefs.getJdbcDebugType())
		{
			final ApplicationFiles appFiles = new ApplicationFiles();
			final File outFile = appFiles.getJDBCDebugLogFile();

			
			DriverManager.setLogStream(null);
			if (_jdbcDebugOutputStream != null)
			{
				_jdbcDebugOutputStream.close();
				_jdbcDebugOutputStream = null;
			}
			DriverManager.setLogWriter(null);
			if (_jdbcDebugOutputWriter != null)
			{
				_jdbcDebugOutputWriter.close();
				_jdbcDebugOutputWriter = null;
			}

			if (_prefs.isJdbcDebugToStream())
			{
				try
				{
                    
					s_log.debug(s_stringMgr.getString("Application.info.setjdbcdebuglog"));
					_jdbcDebugOutputStream = new PrintStream(new FileOutputStream(outFile));
					DriverManager.setLogStream(_jdbcDebugOutputStream);
                    
					s_log.debug(s_stringMgr.getString("Application.info.setjdbcdebuglogsuccess"));
				}
				catch (IOException ex)
				{
					final String msg = s_stringMgr.getString("Application.error.jdbcstream");
					s_log.error(msg, ex);
					showErrorDialog(msg, ex);
					DriverManager.setLogStream(System.out);
				}
			}

			if (_prefs.isJdbcDebugToWriter())
			{
				try
				{
                    
					s_log.debug(s_stringMgr.getString("Application.info.jdbcwriter"));
					_jdbcDebugOutputWriter = new PrintWriter(new FileWriter(outFile));
					DriverManager.setLogWriter(_jdbcDebugOutputWriter);
                    
					s_log.debug(s_stringMgr.getString("Application.info.jdbcwritersuccess"));
				}
				catch (IOException ex)
				{
					final String msg = s_stringMgr.getString("Application.error.jdbcwriter");
					s_log.error(msg, ex);
					showErrorDialog(msg, ex);
					DriverManager.setLogWriter(new PrintWriter(System.out));
				}
			}

			_jdbcDebugType = _prefs.getJdbcDebugType();
		}
	}
}
