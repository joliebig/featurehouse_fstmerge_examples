package net.sourceforge.squirrel_sql.plugins.postgres.commands;


import net.sourceforge.squirrel_sql.plugins.postgres.gui.MessageDialog;
import net.sourceforge.squirrel_sql.plugins.postgres.gui.VacuumDatabaseDialog;
import net.sourceforge.squirrel_sql.plugins.postgres.commands.handler.MessageSQLExecuterHandler;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;

import java.util.ArrayList;
import java.sql.SQLWarning;

public class VacuumDatabaseCommand extends AbstractPostgresDialogCommand {
    
    protected VacuumDatabaseDialog _mainDialog;

    
    @SuppressWarnings("unused")
    private final static ILogger s_log = LoggerController.createLogger(VacuumDatabaseCommand.class);

    
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(VacuumDatabaseCommand.class);


    protected interface i18n {
        String SHOWSQL_DIALOG_TITLE = s_stringMgr.getString("VacuumDatabaseCommand.sqlDialogTitle");
        String PROGRESS_DIALOG_TITLE = s_stringMgr.getString("VacuumDatabaseCommand.progressDialogTitle");
        String COMMAND_PREFIX = s_stringMgr.getString("VacuumDatabaseCommand.commandPrefix");
        String COMMAND_SUFFIX = s_stringMgr.getString("VacuumDatabaseCommand.commandSuffix");
    }


    public VacuumDatabaseCommand(ISession session) {
        super(session);
    }


    
    public void execute() throws BaseException {
        showDialog();
    }


    protected void showDialog() {
        String[] catalogs = _session.getSchemaInfo().getCatalogs();
        String catalog = "";
        if (catalogs != null && catalogs.length > 0) catalog = catalogs[0];

        _mainDialog = new VacuumDatabaseDialog(catalog);
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

        StringBuilder stmt = new StringBuilder();
        stmt.append("VACUUM ").append(full).append("VERBOSE ").append(analyze);
        if (stmt.length() > 0) {
            stmt.append(sep);
            result.add(stmt.toString());
        }

        return result.toArray(new String[result.size()]);
    }


    
    @Override
    protected void executeScript(String script) {
        final MessageDialog messageDialog = new MessageDialog();
        messageDialog.setTitle("SQL Execution Output");        
        messageDialog.setLocationRelativeTo(_session.getApplication().getMainFrame());

        final VacuumDatabaseSQLExecuterHandler handler = new VacuumDatabaseSQLExecuterHandler(_session, messageDialog, i18n.PROGRESS_DIALOG_TITLE, i18n.COMMAND_PREFIX);
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


    private class VacuumDatabaseSQLExecuterHandler extends MessageSQLExecuterHandler {
        public VacuumDatabaseSQLExecuterHandler(ISession session, MessageDialog mdialog, String progressDialogTitle, String commandPrefix) {
            super(session, mdialog, progressDialogTitle, commandPrefix);
        }


        protected String getSuffix(String sql) {
            return i18n.COMMAND_SUFFIX;
        }


        @Override
        public void sqlExecutionWarning(SQLWarning warn) {
            
            String warning = warn.toString();
            if (warning.contains("INFO: vacuuming ")) {
                _mdialog.writeEmptyLine();
                String tablename = warning.substring("INFO: vacuuming \"".length(), warning.length() - 1);
                _mdialog.writeLine("======= " + _commandPrefix + " " + tablename + " =======");
            }

            super.sqlExecutionWarning(warn);
        }
    }
}
