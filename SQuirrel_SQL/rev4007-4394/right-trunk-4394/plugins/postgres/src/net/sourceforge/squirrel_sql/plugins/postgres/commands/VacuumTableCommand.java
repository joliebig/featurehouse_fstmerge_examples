package net.sourceforge.squirrel_sql.plugins.postgres.commands;


import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.plugins.postgres.gui.VacuumTableDialog;
import net.sourceforge.squirrel_sql.plugins.postgres.gui.MessageDialog;
import net.sourceforge.squirrel_sql.plugins.postgres.commands.handler.MessageSQLExecuterHandler;

import java.util.ArrayList;

public class VacuumTableCommand extends AbstractPostgresDialogCommand {
    
    protected VacuumTableDialog _mainDialog;

    
    protected final ITableInfo[] _infos;

    
    @SuppressWarnings("unused")
    private final static ILogger s_log = LoggerController.createLogger(VacuumTableCommand.class);

    
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(VacuumTableCommand.class);


    protected interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("VacuumTableCommand.sqlDialogTitle");
        String PROGRESS_DIALOG_TITLE = s_stringMgr.getString("VacuumTableCommand.progressDialogTitle");
        String COMMAND_PREFIX = s_stringMgr.getString("VacuumTableCommand.commandPrefix");
    }


    public VacuumTableCommand(ISession session, IDatabaseObjectInfo[] infos) {
        super(session);

        ITableInfo[] tableinfos = new ITableInfo[infos.length];
        for (int i = 0; i < infos.length; i++) {
            if (infos[i] instanceof ITableInfo) {
                tableinfos[i] = (ITableInfo) infos[i];
            } else {
                
                throw new IllegalArgumentException("Not all selected objects where tables.");
            }
        }
        _infos = tableinfos;
    }


    
    public void execute() throws BaseException {
        showDialog(_infos);
    }


    protected void showDialog(ITableInfo[] tableinfos) {
        _mainDialog = new VacuumTableDialog(tableinfos);
        _mainDialog.addExecuteListener(new ExecuteListener());
        _mainDialog.addEditSQLListener(new EditSQLListener(_mainDialog));
        _mainDialog.addShowSQLListener(new ShowSQLListener(i18n.SHOWSQL_DIALOG_TITLE, _mainDialog));
        _mainDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());
        _mainDialog.setVisible(true);
    }


    
    @Override
    protected String[] generateSQLStatements() {
        ArrayList<String> result = new ArrayList<String>();
        final String sep = _session.getQueryTokenizer().getSQLStatementSeparator();

        String full = (_mainDialog.getFullOption() ? "FULL " : "");
        String analyze = (_mainDialog.getAnalyzeOption() ? "ANALYZE " : "");

        for (ITableInfo info : _mainDialog.getContent()) {
            StringBuilder stmt = new StringBuilder();
            stmt.append("VACUUM ").append(full).append("VERBOSE ").append(analyze).append(info.getQualifiedName());
            if (stmt.length() > 0) {
                stmt.append(sep);
                result.add(stmt.toString());
            }
        }

        return result.toArray(new String[result.size()]);
    }


    
    @Override
    protected void executeScript(String script) {
        final MessageDialog messageDialog = new MessageDialog();
        messageDialog.setTitle("SQL Execution Output");        
        messageDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());

        final VacuumTableSQLExecuterHandler handler = new VacuumTableSQLExecuterHandler(_session, messageDialog, i18n.PROGRESS_DIALOG_TITLE, i18n.COMMAND_PREFIX);
        final SQLExecuterTask executer = new SQLExecuterTask(_session, script, handler);
        executer.setSchemaCheck(false);

        _session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                GUIUtils.processOnSwingEventThread(new Runnable() {
                    public void run() {
                        _mainDialog.setVisible(false);
                        messageDialog.setVisible(true);
                    }
                });
                executer.run();
            }
        });
    }


    private class VacuumTableSQLExecuterHandler extends MessageSQLExecuterHandler {
        public VacuumTableSQLExecuterHandler(ISession session, MessageDialog mdialog, String progressDialogTitle, String commandPrefix) {
            super(session, mdialog, progressDialogTitle, commandPrefix);
        }


        protected String getSuffix(String sql) {
            String[] parts = sql.split(" ");
            return parts[parts.length - 1];
        }
    }
}
