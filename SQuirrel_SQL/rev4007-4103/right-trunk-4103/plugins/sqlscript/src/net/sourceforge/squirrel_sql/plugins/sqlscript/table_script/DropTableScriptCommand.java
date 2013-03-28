package net.sourceforge.squirrel_sql.plugins.sqlscript.table_script;



import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.sqlscript.FrameWorkAcessor;
import net.sourceforge.squirrel_sql.plugins.sqlscript.SQLScriptPlugin;

public class DropTableScriptCommand implements ICommand {
    
    private ISession _session;

    
    private final SQLScriptPlugin _plugin;

    
    private static ILogger s_log = LoggerController
            .createLogger(DropTableScriptCommand.class);

    
    private static final StringManager s_stringMgr = StringManagerFactory
            .getStringManager(DropTableScriptCommand.class);

    
    public DropTableScriptCommand(ISession session, SQLScriptPlugin plugin) {
        super();
        _session = session;
        _plugin = plugin;
    }

    
    public void execute() {
        IObjectTreeAPI api = FrameWorkAcessor.getObjectTreeAPI(_session,
                _plugin);
        IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
        scriptTablesToSQLEntryArea(dbObjs);
    }

    public void scriptTablesToSQLEntryArea(final IDatabaseObjectInfo[] dbObjs) {
        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                final String script = dropTableScriptString(dbObjs);
                if (script != null) {
                    GUIUtils.processOnSwingEventThread(new Runnable() {
                        public void run() {
                            ISQLPanelAPI api = FrameWorkAcessor.getSQLPanelAPI(
                                    _session, _plugin);
                            api.appendSQLScript(script, true);
                            int tabIdx = ISession.IMainPanelTabIndexes.SQL_TAB;
                            _session.selectMainTab(tabIdx);
                        }
                    });
                }
            }
        });
    }

    public String dropTableScriptString(IDatabaseObjectInfo[] dbObjs) {
        StringBuffer sbScript = new StringBuffer(1000);
        try {
            for (int k = 0; k < dbObjs.length; k++) {
                if (false == dbObjs[k] instanceof ITableInfo) {
                    continue;

                }
                ITableInfo ti = (ITableInfo) dbObjs[k];

                String sTable = ScriptUtil.getTableName(ti);
                sbScript.append("DROP TABLE ");
                sbScript.append(sTable);
                sbScript.append(ScriptUtil.getStatementSeparator(_session));
                sbScript.append("\n");
            }
        } catch (Exception e) {
            _session.showErrorMessage(e);
        }
        return sbScript.toString();
    }
}