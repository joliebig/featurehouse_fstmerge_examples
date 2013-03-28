package net.sourceforge.squirrel_sql.plugins.postgres.commands.handler;


import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecutionInfo;
import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableTableModel;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.postgres.gui.MessageDialog;

import java.sql.SQLWarning;
import java.sql.ResultSet;

public abstract class MessageSQLExecuterHandler extends ProgressSQLExecuterHandler {
    
    private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(MessageSQLExecuterHandler.class);

    protected MessageDialog _mdialog;
    protected long _startTime;

    protected boolean _exceptionOccured = false;


    public MessageSQLExecuterHandler(ISession session, MessageDialog mdialog, String progressDialogTitle, String commandPrefix) {
        super(session, mdialog, progressDialogTitle, commandPrefix);
        _mdialog = mdialog;
    }


    @Override
    public void sqlToBeExecuted(String sql) {
        super.sqlToBeExecuted(sql);
        _mdialog.writeLine("========= " + _commandPrefix + " " + getSuffix(sql) + " =========");
    }


    protected abstract String getSuffix(String sql);


    @Override
    public void sqlExecutionComplete(SQLExecutionInfo info, int processedStatementCount, int statementCount) {
        super.sqlExecutionComplete(info, processedStatementCount, statementCount);
        _mdialog.writeEmptyLine();
    }


    @Override
    public void sqlExecutionWarning(SQLWarning warn) {
        
        _mdialog.writeLine(warn.toString());
    }


    @Override
    public void sqlStatementCount(int statementCount) {
        super.sqlStatementCount(statementCount);
        _startTime = System.currentTimeMillis();
    }


    @Override
    public void sqlCloseExecutionHandler() {
        super.sqlCloseExecutionHandler();
        if (!_exceptionOccured) {
            float executionTime = (float) (System.currentTimeMillis() - _startTime) / 1000;
            _mdialog.writeEmptyLine();
            _mdialog.writeLine(s_stringMgr.getString("MessageSQLExecuterHandler.done", _commandPrefix, executionTime));
        }
        _mdialog.enableCloseButton();
    }


    public void sqlExecutionCancelled() {
        super.sqlExecutionCancelled();
        
    }


    public void sqlDataUpdated(int updateCount) {
        super.sqlDataUpdated(updateCount);
    }


    public void sqlResultSetAvailable(ResultSet rst, SQLExecutionInfo info, IDataSetUpdateableTableModel model) {
        super.sqlResultSetAvailable(rst, info, model);
    }


    public void sqlExecutionException(Throwable th, String postErrorString) {
        super.sqlExecutionException(th, postErrorString);
        _mdialog.writeEmptyLine();
        _mdialog.writeLine(s_stringMgr.getString("MessageSQLExecuterHandler.aborted", _commandPrefix));
        _exceptionOccured = true;
    }
}
