package net.sourceforge.squirrel_sql.plugins.derby;



import java.net.MalformedURLException;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.session.ObjectTreeInternalFrame;
import net.sourceforge.squirrel_sql.client.gui.session.SQLInternalFrame;
import net.sourceforge.squirrel_sql.client.plugin.DefaultSessionPlugin;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginSessionCallback;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginGlobalPreferencesTab;
import net.sourceforge.squirrel_sql.client.plugin.gui.PluginQueryTokenizerPreferencesPanel;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.expanders.TableWithChildNodesExpander;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.DatabaseObjectInfoTab;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDriver;
import net.sourceforge.squirrel_sql.fw.sql.SQLDriverManager;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.derby.exp.DerbyTableTriggerExtractorImpl;
import net.sourceforge.squirrel_sql.plugins.derby.prefs.DerbyPluginPreferencesPanel;
import net.sourceforge.squirrel_sql.plugins.derby.prefs.DerbyPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.derby.tab.TriggerDetailsTab;
import net.sourceforge.squirrel_sql.plugins.derby.tab.TriggerSourceTab;
import net.sourceforge.squirrel_sql.plugins.derby.tab.ViewSourceTab;
import net.sourceforge.squirrel_sql.plugins.derby.tokenizer.DerbyQueryTokenizer;
import net.sourceforge.squirrel_sql.plugins.derby.types.DerbyClobDataTypeComponentFactory;


public class DerbyPlugin extends DefaultSessionPlugin {

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DerbyPlugin.class);

   
   private final static ILogger s_log = LoggerController.createLogger(DerbyPlugin.class);

   
   private IObjectTreeAPI _treeAPI;

   static interface i18n {
      
      String SHOW_VIEW_SOURCE = s_stringMgr.getString("DerbyPlugin.showViewSource");

      
      String SHOW_PROCEDURE_SOURCE = s_stringMgr.getString("DerbyPlugin.showProcedureSource");

      
      String PREFS_TITLE = s_stringMgr.getString("DerbyPlugin.prefsTitle");

      
      String PREFS_HINT = s_stringMgr.getString("DerbyPlugin.prefsHint");
   }

   
   private PluginQueryTokenizerPreferencesManager _prefsManager = null;

   
   public String getInternalName() {
      return "derby";
   }

   
   public String getDescriptiveName() {
      return "Derby Plugin";
   }

   
   public String getVersion() {
      return "0.12";
   }

   
   public String getAuthor() {
      return "Rob Manning";
   }

   
   public String getContributors() {
      return "Alex Pivovarov";
   }

   
   public String getChangeLogFileName() {
      return "changes.txt";
   }

   
   public String getHelpFileName() {
      return "readme.html";
   }

   
   public String getLicenceFileName() {
      return "licence.txt";
   }

   
   public synchronized void load(IApplication app) throws PluginException {
      super.load(app);
   }

   
   public IGlobalPreferencesPanel[] getGlobalPreferencePanels() {
      PluginQueryTokenizerPreferencesPanel _prefsPanel = new DerbyPluginPreferencesPanel(_prefsManager);

      PluginGlobalPreferencesTab tab = new PluginGlobalPreferencesTab(_prefsPanel);

      tab.setHint(i18n.PREFS_HINT);
      tab.setTitle(i18n.PREFS_TITLE);

      return new IGlobalPreferencesPanel[] { tab };
   }

   
   public synchronized void initialize() throws PluginException {
      super.initialize();
      
      
      _app.getSessionManager().addSessionListener(new SessionListener());

      

      _prefsManager = new PluginQueryTokenizerPreferencesManager();
      _prefsManager.initialize(this, new DerbyPreferenceBean());

      
      DerbyPreferenceBean prefBean = (DerbyPreferenceBean) _prefsManager.getPreferences();

      if (prefBean.isReadClobsFully()) {
         
         CellComponentFactory.registerDataTypeFactory(new DerbyClobDataTypeComponentFactory(),
                                                      2005,
                                                      "CLOB");
      }      
   }

   
   public void unload() {
      super.unload();
   }

   public boolean allowsSessionStartedInBackground() {
      return true;
   }

   
   public PluginSessionCallback sessionStarted(final ISession session) {
      if (!isPluginSession(session)) {
         return null;
      }
      if (s_log.isInfoEnabled()) {
         s_log.info("Installing Derby query tokenizer");
      }
      DerbyPreferenceBean prefBean = (DerbyPreferenceBean) _prefsManager.getPreferences();
      if (prefBean.isInstallCustomQueryTokenizer()) {
         DerbyQueryTokenizer tokenizer = new DerbyQueryTokenizer(prefBean.getStatementSeparator(),
                                                                 prefBean.getLineComment(),
                                                                 prefBean.isRemoveMultiLineComments());
         session.setQueryTokenizer(tokenizer);
      }
      
      GUIUtils.processOnSwingEventThread(new Runnable() {
         public void run() {
            updateTreeApi(session);
         }
      });

      return new PluginSessionCallback() {
         public void sqlInternalFrameOpened(SQLInternalFrame sqlInternalFrame,
               ISession sess) {
            
         }

         public void objectTreeInternalFrameOpened(
               ObjectTreeInternalFrame objectTreeInternalFrame, ISession sess) {
            
         }
      };

   }

   @Override
   protected boolean isPluginSession(ISession session) {
      return DialectFactory.isDerby(session.getMetaData());
   }

   private void updateTreeApi(ISession session) {

      _treeAPI = session.getSessionInternalFrame().getObjectTreeAPI();

      
      

      _treeAPI.addDetailTab(DatabaseObjectType.VIEW,
                            new ViewSourceTab(i18n.SHOW_VIEW_SOURCE));

      
      
      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER,
                            new DatabaseObjectInfoTab());
      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER_TYPE_DBO,
                            new DatabaseObjectInfoTab());
      
      

      
      
      
      TableWithChildNodesExpander trigExp = new TableWithChildNodesExpander();
      trigExp.setTableTriggerExtractor(new DerbyTableTriggerExtractorImpl());
      _treeAPI.addExpander(DatabaseObjectType.TABLE, trigExp);

      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER, new TriggerDetailsTab());
      _treeAPI.addDetailTab(DatabaseObjectType.TRIGGER,
                            new TriggerSourceTab("The source of the trigger"));

   }

   
   private class SessionListener extends SessionAdapter {
      @Override
      public void sessionClosed(SessionEvent evt) {
         ISession session = evt.getSession();
         shutdownEmbeddedDerby(session);
      }
   }
   
   
   private void shutdownEmbeddedDerby(final ISession session) {
      try {
         ISQLDriver iSqlDr = session.getDriver();
         if (!(iSqlDr.getDriverClassName().startsWith("org.apache.derby.jdbc.EmbeddedDriver"))) {
            return;
         }
         
         IIdentifier drId = iSqlDr.getIdentifier();
         SQLDriverManager sqlDrMan = _app.getSQLDriverManager();
         
         Driver jdbcDr = sqlDrMan.getJDBCDriver(drId);
         
         try {
            jdbcDr.connect("jdbc:derby:;shutdown=true", new Properties());
         } catch (SQLException e) {
            
            
            s_log.info(e.getMessage());
         }
         
         sqlDrMan.registerSQLDriver(iSqlDr);
      } catch (RuntimeException e) {
         s_log.error(e.getMessage(),e);
      } catch (MalformedURLException e) {
         s_log.error(e.getMessage(),e);
      } catch (IllegalAccessException e) {
         s_log.error(e.getMessage(),e);
      } catch (InstantiationException e) {
         s_log.error(e.getMessage(),e);
      } catch (ClassNotFoundException e) {
         s_log.error(e.getMessage(),e);
      }
   }

   
}
