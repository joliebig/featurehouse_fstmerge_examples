package net.sourceforge.squirrel_sql.client.session;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.client.gui.db.SQLAlias;
import net.sourceforge.squirrel_sql.client.gui.session.BaseSessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SessionPanel;
import net.sourceforge.squirrel_sql.client.mainframe.action.OpenConnectionCommand;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IMainPanelTab;
import net.sourceforge.squirrel_sql.client.session.parser.IParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsProcessor;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.sql.IQueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.QueryTokenizer;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.SQLConnectionState;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.util.DefaultExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.NullMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


class Session implements ISession
{
   
   private static final ILogger s_log =
      LoggerController.createLogger(Session.class);

   
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(Session.class);

   
   private String _title = "";

   private SessionPanel _sessionSheet;

   
   private final IIdentifier _id;

   
   private IApplication _app;

   
   private SQLConnection _conn;

   
   private ISQLDriver _driver;

   
   private SQLAlias _alias;

   private final String _user;
   private final String _password;

   
   private SessionProperties _props;

   
   private final Map<String, Map<String, Object>> _pluginObjects = 
       new HashMap<String, Map<String, Object>>();

   private IMessageHandler _msgHandler = NullMessageHandler.getInstance();

   
   private final net.sourceforge.squirrel_sql.client.session.schemainfo.SchemaInfo _schemaInfo;

   
   private boolean _closed;

   private List<JComponent> _statusBarToBeAdded = 
       new ArrayList<JComponent>();

   private SQLConnectionListener _connLis = null;

   private BaseSessionInternalFrame _activeActiveSessionWindow;
   private SessionInternalFrame _sessionInternalFrame;
   private Hashtable<IIdentifier, ParserEventsProcessor> 
       _parserEventsProcessorsByEntryPanelIdentifier = 
       new Hashtable<IIdentifier, ParserEventsProcessor>();


   
   private boolean _finishedLoading = false;

   
   private boolean _pluginsFinishedLoading = false;

   
   private boolean customTokenizerInstalled = false;
   
   private IQueryTokenizer tokenizer = null;
   
   
   private DefaultExceptionFormatter formatter = new DefaultExceptionFormatter();
   
   
   public Session(IApplication app, ISQLDriver driver, SQLAlias alias,
                  SQLConnection conn, String user, String password,
                  IIdentifier sessionId)
   {
      super();
      _schemaInfo = new SchemaInfo(app);

      if (app == null)
      {
         throw new IllegalArgumentException("null IApplication passed");
      }
      if (driver == null)
      {
         throw new IllegalArgumentException("null ISQLDriver passed");
      }
      if (alias == null)
      {
         throw new IllegalArgumentException("null ISQLAlias passed");
      }
      if (conn == null)
      {
         throw new IllegalArgumentException("null SQLConnection passed");
      }
      if (sessionId == null)
      {
         throw new IllegalArgumentException("sessionId == null");
      }

      _app = app;
      _driver = driver;

      _alias = new SQLAlias();

      try
      {
         _alias.assignFrom(alias, true);
      }
      catch (ValidationException e)
      {
         throw new RuntimeException(e);
      }


      _conn = conn;
      _user = user;
      _password = password;
      _id = sessionId;

      setupTitle();

      _props = (SessionProperties)_app.getSquirrelPreferences().getSessionProperties().clone();

      _connLis = new SQLConnectionListener();
      _conn.addPropertyChangeListener(_connLis);

        checkDriverVersion();

      
      _app.getThreadPool().addTask(new Runnable()
      {
         public void run()
         {
            _schemaInfo.initialLoad(Session.this);
            _finishedLoading = true;
         }
      });

   }

   
   public void close() throws SQLException
   {
      if (!_closed)
      {
         s_log.debug("Closing session: " + _id);

         _conn.removePropertyChangeListener(_connLis);
         _connLis = null;


         ParserEventsProcessor[] procs =
            _parserEventsProcessorsByEntryPanelIdentifier.values().toArray(new ParserEventsProcessor[0]);


         for (int i = 0; i < procs.length; i++)
         {
            try
            {
               procs[i].endProcessing();
            }
            catch(Exception e)
            {
               s_log.info("Error stopping parser event processor", e);
            }
         }

         _schemaInfo.dispose();


         try
         {
            closeSQLConnection();
         }
         finally
         {
            
            
            _closed = true;

            if (_sessionSheet != null)
            {
               _sessionSheet.sessionHasClosed();
               _sessionSheet = null;
            }
         }
         s_log.debug("Successfully closed session: " + _id);
      }
   }

   
   public synchronized void commit()
   {
      try
      {
         getSQLConnection().commit();
         final String msg = s_stringMgr.getString("Session.commit");
         _msgHandler.showMessage(msg);
      }
      catch (Throwable ex)
      {
         _msgHandler.showErrorMessage(ex, formatter);
      }
   }

   
   public synchronized void rollback()
   {
      try
      {
         getSQLConnection().rollback();
         final String msg = s_stringMgr.getString("Session.rollback");
         _msgHandler.showMessage(msg);
      }
      catch (Exception ex)
      {
         _msgHandler.showErrorMessage(ex, formatter);
      }
   }

   
   public IIdentifier getIdentifier()
   {
      return _id;
   }

   
   public boolean isClosed()
   {
      return _closed;
   }

   
   public IApplication getApplication()
   {
      return _app;
   }

   
   public ISQLConnection getSQLConnection()
   {
        checkThread();
      return _conn;
   }

   
   public ISQLDriver getDriver()
   {
      return _driver;
   }

   
   public ISQLAliasExt getAlias()
   {
      return _alias;
   }

   public SessionProperties getProperties()
   {
      return _props;
   }

   
   public SchemaInfo getSchemaInfo()
   {
      return _schemaInfo;
   }

   public synchronized Object getPluginObject(IPlugin plugin, String key)
   {
      if (plugin == null)
      {
         throw new IllegalArgumentException("Null IPlugin passed");
      }
      if (key == null)
      {
         throw new IllegalArgumentException("Null key passed");
      }
      Map<String, Object> map = _pluginObjects.get(plugin.getInternalName());
      if (map == null)
      {
         map = new HashMap<String, Object>();
         _pluginObjects.put(plugin.getInternalName(), map);
      }
      return map.get(key);
   }

   
   public void addToToolbar(Action action)
   {
      _sessionSheet.addToToolbar(action);
   }

   public void addSeparatorToToolbar()
   {
      _sessionSheet.addSeparatorToToolbar();
   }


   public synchronized Object putPluginObject(IPlugin plugin, String key,
                                              Object value)
   {
      if (plugin == null)
      {
         throw new IllegalArgumentException("Null IPlugin passed");
      }
      if (key == null)
      {
         throw new IllegalArgumentException("Null key passed");
      }
      Map<String, Object> map = _pluginObjects.get(plugin.getInternalName());
      if (map == null)
      {
         map = new HashMap<String, Object>();
         _pluginObjects.put(plugin.getInternalName(), map);
      }
      return map.put(key, value);
   }

   public synchronized void removePluginObject(IPlugin plugin, String key)
   {
      if (plugin == null)
      {
         throw new IllegalArgumentException("Null IPlugin passed");
      }
      if (key == null)
      {
         throw new IllegalArgumentException("Null key passed");
      }
      Map<String, Object> map = _pluginObjects.get(plugin.getInternalName());
      if (map != null)
      {
         map.remove(key);
      }
   }

   public synchronized void closeSQLConnection() throws SQLException
   {
      if (_conn != null)
      {
         try
         {
            _conn.close();
         }
         finally
         {
            _conn = null;
         }
      }
   }

   
   public void reconnect()
   {
      SQLConnectionState connState = new SQLConnectionState();
      if (_conn != null)
      {
         try
         {
            connState.saveState(_conn, getProperties(), _msgHandler);
         }
         catch (SQLException ex)
         {
            s_log.error("Unexpected SQLException", ex);
         }
      }
      OpenConnectionCommand cmd = new OpenConnectionCommand(_app, _alias,
                                 _user, _password, connState.getConnectionProperties());
      try
      {
         closeSQLConnection();
         _app.getSessionManager().fireConnectionClosedForReconnect(this);
      }
      catch (SQLException ex)
      {
         final String msg = s_stringMgr.getString("Session.error.connclose");
         s_log.error(msg, ex);
         _msgHandler.showErrorMessage(msg);
         _msgHandler.showErrorMessage(ex, this.getExceptionFormatter());
      }
      try
      {
         cmd.execute();
         _conn = cmd.getSQLConnection();
         if (connState != null)
         {
            connState.restoreState(_conn, _msgHandler);
            getProperties().setAutoCommit(connState.getAutoCommit());
         }
         final String msg = s_stringMgr.getString("Session.reconn", _alias.getName());
         _msgHandler.showMessage(msg);
         _app.getSessionManager().fireReconnected(this);
      }
      catch (Throwable t)
      {
         final String msg = s_stringMgr.getString("Session.reconnError", _alias.getName());
         _msgHandler.showErrorMessage(msg +"\n" + t.toString());
         s_log.error(msg, t);
         _app.getSessionManager().fireReconnectFailed(this);
      }
   }

   public void setMessageHandler(IMessageHandler handler)
   {
      _msgHandler = handler != null ? handler : NullMessageHandler.getInstance();
   }

   public synchronized void setSessionSheet(SessionPanel child)
   {
      _sessionSheet = child;
      if (_sessionSheet != null)
      {
         final ListIterator<JComponent> it = _statusBarToBeAdded.listIterator();
         while (it.hasNext())
         {
            addToStatusBar(it.next());
            it.remove();
         }
      }
   }

   public synchronized void setSessionInternalFrame(SessionInternalFrame sif)
   {
      _sessionInternalFrame = sif;

      
      _activeActiveSessionWindow = sif;

      _sessionSheet = sif.getSessionPanel();
      final ListIterator<JComponent> it = _statusBarToBeAdded.listIterator();
      while (it.hasNext())
      {
         addToStatusBar(it.next());
         it.remove();
      }
   }

   public synchronized SessionInternalFrame getSessionInternalFrame()
   {
      return _sessionInternalFrame;
   }

   public synchronized SessionPanel getSessionSheet()
   {
      return _sessionSheet;
   }

   
   public void selectMainTab(int tabIndex)
   {
      _sessionSheet.selectMainTab(tabIndex);
   }

   public int getSelectedMainTabIndex()
   {
      return _sessionSheet.getSelectedMainTabIndex();
   }


   
   public int addMainTab(IMainPanelTab tab)
   {
      return _sessionSheet.addMainTab(tab);
   }

   
   public synchronized void addToStatusBar(JComponent comp)
   {
      if (_sessionSheet != null)
      {
         _sessionSheet.addToStatusBar(comp);
      }
      else
      {
         _statusBarToBeAdded.add(comp);
      }
   }

   
   public synchronized void removeFromStatusBar(JComponent comp)
   {
      if (_sessionSheet != null)
      {
         _sessionSheet.removeFromStatusBar(comp);
      }
      else
      {
         _statusBarToBeAdded.remove(comp);
      }
   }






   
   public String getTitle()
   {
      return _title;
   }


   public String toString()
   {
      return getTitle();
   }

   private void setupTitle()
   {
      String catalog = null;
      try
      {
         catalog = getSQLConnection().getCatalog();
      }
      catch (SQLException ex)
      {
         s_log.error("Error occured retrieving current catalog from Connection", ex);
      }
      if (catalog == null)
      {
         catalog = "";
      }
      else
      {
         catalog = "(" + catalog + ")";
      }

      String title = null;
      String user = _user != null ? _user : "";
      if (user.length() > 0)
      {
         String[] args = new String[3];
         args[0] = getAlias().getName();
         args[1] = catalog;
         args[2] = user;
         title = s_stringMgr.getString("Session.title1", args);
      }
      else
      {
         String[] args = new String[2];
         args[0] = getAlias().getName();
         args[1] = catalog;
         title = s_stringMgr.getString("Session.title0", args);
      }

      _title = _id + " - " + title;
   }


   
   public IParserEventsProcessor getParserEventsProcessor(IIdentifier entryPanelIdentifier)
   {
      ParserEventsProcessor pep = _parserEventsProcessorsByEntryPanelIdentifier.get(entryPanelIdentifier);

      if(null == pep)
      {
         pep = new ParserEventsProcessor(getSqlPanelApi(entryPanelIdentifier), this);
         _parserEventsProcessorsByEntryPanelIdentifier.put(entryPanelIdentifier, pep);
      }
      return pep;
   }

   private ISQLPanelAPI getSqlPanelApi(IIdentifier entryPanelIdentifier)
   {
      BaseSessionInternalFrame[] frames = getApplication().getWindowManager().getAllFramesOfSession(getIdentifier());

      for (int i = 0; i < frames.length; i++)
      {
         if(frames[i] instanceof SQLInternalFrame)
         {
            ISQLPanelAPI sqlPanelAPI = ((SQLInternalFrame)frames[i]).getSQLPanelAPI();
            IIdentifier id = sqlPanelAPI.getSQLEntryPanel().getIdentifier();

            if(id.equals(entryPanelIdentifier))
            {
               return sqlPanelAPI;
            }
         }

         if(frames[i] instanceof SessionInternalFrame)
         {
            ISQLPanelAPI sqlPanelAPI = ((SessionInternalFrame)frames[i]).getSQLPanelAPI();
            IIdentifier id = sqlPanelAPI.getSQLEntryPanel().getIdentifier();

            if(id.equals(entryPanelIdentifier))
            {
               return sqlPanelAPI;
            }
         }
      }

      throw new IllegalStateException("Session has no entry panel for ID=" + entryPanelIdentifier);
   }

   public void setActiveSessionWindow(BaseSessionInternalFrame activeActiveSessionWindow)
   {
      _activeActiveSessionWindow = activeActiveSessionWindow;
   }

   public BaseSessionInternalFrame getActiveSessionWindow()
   {
      return _activeActiveSessionWindow;
   }

   
   public ISQLPanelAPI getSQLPanelAPIOfActiveSessionWindow()
   {
      ISQLPanelAPI sqlPanelAPI;
      if(_activeActiveSessionWindow instanceof SessionInternalFrame)
      {
         sqlPanelAPI = ((SessionInternalFrame)_activeActiveSessionWindow).getSQLPanelAPI();
      }
      else if(_activeActiveSessionWindow instanceof SQLInternalFrame)
      {
         sqlPanelAPI = ((SQLInternalFrame)_activeActiveSessionWindow).getSQLPanelAPI();
      }
      else
      {
         throw new IllegalStateException("SQLPanelApi can only be provided for SessionInternalFrame or SQLInternalFrame");
      }

      return sqlPanelAPI;
   }

   
   public IObjectTreeAPI getObjectTreeAPIOfActiveSessionWindow()
   {
      IObjectTreeAPI objectTreeAPI;
      if(_activeActiveSessionWindow instanceof SessionInternalFrame)
      {
         objectTreeAPI = ((SessionInternalFrame)_activeActiveSessionWindow).getObjectTreeAPI();
      }
      else if(_activeActiveSessionWindow instanceof ObjectTreeInternalFrame)
      {
         objectTreeAPI = ((ObjectTreeInternalFrame)_activeActiveSessionWindow).getObjectTreeAPI();
      }
      else
      {
         throw new IllegalStateException("ObjectTreeApi can only be provided for SessionInternalFrame or ObjectTreeInternalFrame");
      }

      return objectTreeAPI;
   }

    
    private void checkDriverVersion() {
        if (!_app.getSquirrelPreferences().getWarnJreJdbcMismatch()) {
            return;
        }
        String javaVersion = System.getProperty("java.vm.version");
        boolean javaVersionIsAtLeast14 = true;
        if (javaVersion != null) {
            if (javaVersion.startsWith("1.1")
                    || javaVersion.startsWith("1.2")
                    || javaVersion.startsWith("1.3"))
            {
                javaVersionIsAtLeast14 = false;
            }
        }
        if (!javaVersionIsAtLeast14) {
            return;
        }
        
        boolean driverIs21Compliant = true;

        
        
        

        SQLDatabaseMetaData md = _conn.getSQLMetaData();
        try {
            md.supportsResultSetType(ResultSet.TYPE_FORWARD_ONLY);
        } catch (Throwable e) {
            driverIs21Compliant = false;
        }

        if (!driverIs21Compliant) {
            
            String msg =
                s_stringMgr.getString("Session.driverCompliance", _alias.getName());
            
            String title =
                s_stringMgr.getString("Session.driverComplianceTitle");
            showMessageDialog(msg, title, JOptionPane.WARNING_MESSAGE);
            s_log.info(msg);
            return;
        }
        boolean driverIs30Compliant = true;
        try {
            md.supportsSavepoints();
        } catch (Throwable e) {
            driverIs30Compliant = false;
        }

        if (!driverIs30Compliant) {
            
            String msg =
                s_stringMgr.getString("Session.driverCompliance3.0", _alias.getName());
            
            String title =
                s_stringMgr.getString("Session.driverComplianceTitle");
            showMessageDialog(msg, title, JOptionPane.WARNING_MESSAGE);
            s_log.info(msg);
        }
    }

    private void showMessageDialog(final String message,
                                   final String title,
                                   final int messageType)
    {
        final JFrame f = _app.getMainFrame();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(f,
                        message,
                        title,
                        messageType);
            }
        });
    }

    
    private void checkThread() {
        
    }

   private class SQLConnectionListener implements PropertyChangeListener
   {
      public void propertyChange(PropertyChangeEvent evt)
      {
         final String propName = evt.getPropertyName();
         if (propName == null || propName == ISQLConnection.IPropertyNames.CATALOG)
         {
            setupTitle();
         }
      }
   }


   protected void finalize() throws Throwable
   {





      _app.getSessionManager().fireSessionFinalized(_id);

   }

    
    public void setPluginsfinishedLoading(boolean pluginsFinishedLoading) {
        this._pluginsFinishedLoading = pluginsFinishedLoading;
    }

    
    public boolean isfinishedLoading() {
        return _finishedLoading && _pluginsFinishedLoading;
    }

    
    public boolean confirmClose()
    {
       if(getActiveSessionWindow() instanceof SQLInternalFrame || getActiveSessionWindow() instanceof SessionInternalFrame)
       {
          if (getSQLPanelAPIOfActiveSessionWindow().confirmClose())
          {
             return true;
          }
          else
          {
             return false;
          }
       }
       
       return true;

    }

        
    public IQueryTokenizer getQueryTokenizer() {
        if (tokenizer == null || !customTokenizerInstalled) {
            
            
            
            
            tokenizer = new QueryTokenizer(_props.getSQLStatementSeparator(),
                                           _props.getStartOfLineComment(),
                                           _props.getRemoveMultiLineComment());
        }
        return tokenizer;
    }

        
    public void setQueryTokenizer(IQueryTokenizer aTokenizer) {
        if (aTokenizer == null) {
            throw new IllegalArgumentException("aTokenizer arg cannot be null");
        }        
        if (customTokenizerInstalled) {
            String currentTokenizer = tokenizer.getClass().getName();
            String newTokenizer = tokenizer.getClass().getName();
            throw new IllegalStateException(
                "Only one custom query tokenizer can be installed.  " +
                "Current tokenizer is "+currentTokenizer+". New tokenizer is "+
                newTokenizer);
        }
        customTokenizerInstalled = true;
        tokenizer = aTokenizer;
    }

    
    public ISQLDatabaseMetaData getMetaData() {
        if (_conn != null) {
            return _conn.getSQLMetaData();
        } else {
            return null;
        }
    }

    
    public void setExceptionFormatter(ExceptionFormatter formatter) {
        this.formatter.setCustomExceptionFormatter(formatter);
    }

    
    public ExceptionFormatter getExceptionFormatter() {
        return this.formatter; 
    }
    
    
    public String formatException(Throwable th) {
        return this.formatter.format(th);
    }

    
    public void showErrorMessage(String msg) {
        _msgHandler.showErrorMessage(msg);
    }

    
    public void showErrorMessage(Throwable th) {
        _msgHandler.showErrorMessage(th, formatter);
    }

    
    public void showMessage(String msg) {
        _msgHandler.showMessage(msg);
    }

    
    public void showMessage(Throwable th) {
        _msgHandler.showMessage(th, formatter);
    }

    
    public void showWarningMessage(String msg) {
        _msgHandler.showWarningMessage(msg);
    }
    
}
