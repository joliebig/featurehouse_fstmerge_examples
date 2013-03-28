package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;



import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.dialects.CreateScriptPreferences;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.sqlscript.prefs.SQLScriptPreferencesManager;

public class CreateTableScriptCommand implements ICommand
{
   
   private ISession _session;

   
   private final SQLScriptPlugin _plugin;

    
    private static ILogger s_log = 
        LoggerController.createLogger(CreateTableScriptCommand.class);
   
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(CreateTableScriptCommand.class);

    static interface i18n {
        
        
        String JDBCODBC_MESSAGE = 
            s_stringMgr.getString("CreateTableScriptCommand.jdbcOdbcMessage"); 
    }
    
    private static SQLScriptPreferenceBean prefs = 
            SQLScriptPreferencesManager.getPreferences();
    
    
   
   public CreateTableScriptCommand(ISession session, SQLScriptPlugin plugin)
   {
      super();
      _session = session;
      _plugin = plugin;
   }

   
   public void execute()
   {
      IObjectTreeAPI api = FrameWorkAcessor.getObjectTreeAPI(_session, _plugin);
      IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
      scriptTablesToSQLEntryArea(dbObjs);
   }


   public void scriptTablesToSQLEntryArea(final IDatabaseObjectInfo[] dbObjs)
   {
       _session.getApplication().getThreadPool().addTask(new Runnable() {
           public void run() {
               final String script = createTableScriptString(dbObjs);
                if(null != script)
                {
                    GUIUtils.processOnSwingEventThread(new Runnable() {
                        public void run() {
                            ISQLPanelAPI api = 
                                FrameWorkAcessor.getSQLPanelAPI(_session, _plugin);
                            api.appendSQLScript(script, true);
                            _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);                            
                        }
                    });
                }               
           }
       });
	}
   
   public String createTableScriptString(IDatabaseObjectInfo dbObj) {
       return createTableScriptString(new IDatabaseObjectInfo[] { dbObj });
   }

   public String createTableScriptString(IDatabaseObjectInfo[] dbObjs) {
        StringBuilder result = new StringBuilder(1000);
        ISQLDatabaseMetaData md = _session.getMetaData();
        try {
            boolean isJdbcOdbc = md.getURL().startsWith("jdbc:odbc:");
            if (isJdbcOdbc) {
                _session.showErrorMessage(i18n.JDBCODBC_MESSAGE);
                s_log.error(i18n.JDBCODBC_MESSAGE);
            }

            TableScriptConfigCtrl tscc = new TableScriptConfigCtrl(_session
                    .getApplication().getMainFrame());
            if (1 < dbObjs.length) {
                tscc.doModal();
                if (false == tscc.isOk()) {
                    return null;
                }
            }
            
            CreateScriptPreferences csprefs = new CreateScriptPreferences();
            csprefs.setConstraintsAtEnd(tscc.isConstAndIndAtEnd());
            csprefs.setIncludeExternalReferences(
                    tscc.includeConstToTablesNotInScript());
            csprefs.setDeleteAction(prefs.getDeleteAction());
            csprefs.setDeleteRefAction(prefs.isDeleteRefAction());
            csprefs.setUpdateAction(prefs.getUpdateAction());
            csprefs.setUpdateRefAction(prefs.isUpdateRefAction());
            csprefs.setQualifyTableNames(prefs.isQualifyTableNames());
            
            List<ITableInfo> tables = convertArrayToList(dbObjs);
            
            HibernateDialect dialect = 
                DialectFactory.getDialect(DialectFactory.SOURCE_TYPE, 
                                          _session.getApplication().getMainFrame(), 
                                          md);
            List<String> sqls = 
                dialect.getCreateTableSQL(tables, md, csprefs, isJdbcOdbc);
            String sep = _session.getQueryTokenizer().getSQLStatementSeparator();
            
            for (String sql : sqls) {
                result.append(sql);
                result.append("\n");
                result.append(sep);
                result.append("\n");
            }
        } catch (Exception e) {
            _session.showErrorMessage(e);
        }
        return result.toString();
    }
   
   private List<ITableInfo> convertArrayToList(IDatabaseObjectInfo[] dbObjs) {
       List<ITableInfo> result = new ArrayList<ITableInfo>();
       for (IDatabaseObjectInfo dbObj : dbObjs) {
           if (dbObj instanceof ITableInfo) {
               ITableInfo ti = (ITableInfo)dbObj;
               result.add(ti);
           }
       }
       return result;
   }
}