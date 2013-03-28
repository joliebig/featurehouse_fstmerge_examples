package net.sourceforge.squirrel_sql.plugins.refactoring.commands;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.sourceforge.squirrel_sql.client.gui.ProgessCallBackDialog;
import net.sourceforge.squirrel_sql.client.session.DefaultSQLExecuterHandler;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class DropTablesCommand extends AbstractRefactoringCommand
{
    
    private final ILogger s_log =
        LoggerController.createLogger(DropTablesCommand.class);
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DropTablesCommand.class);
    
    private List<ITableInfo> orderedTables = null;
    
    private DropTableCommandExecHandler handler = null;
    
    ProgessCallBackDialog getOrderedTablesCallBack = null;
    
    private static interface i18n {
                
        
        String PROGRESS_DIALOG_ANALYZE_TITLE = 
            s_stringMgr.getString("DropTablesCommand.progressDialogAnalyzeTitle");
        
        
        String PROGRESS_DIALOG_DROP_TITLE = 
            s_stringMgr.getString("DropTablesCommand.progressDialogDropTitle");        
        
        
        String LOADING_PREFIX = 
            s_stringMgr.getString("DropTablesCommand.loadingPrefix");

        
        String DROPPING_CONSTRAINT_PREFIX = 
            s_stringMgr.getString("DropTablesCommand.droppingConstraintPrefix");

        
        String DROPPING_TABLE_PREFIX = 
            s_stringMgr.getString("DropTablesCommand.droppingTablePrefix");
        

    }
    
    
    private HashSet<String> matViewLookup = null;
    
	
	public DropTablesCommand(ISession session, IDatabaseObjectInfo[] tables)
	{
		super(session, tables);
	}

	
	public void execute()
	{
        try {
            super.showDropTableDialog(new DropTablesActionListener(), 
                                      new ShowSQLListener());
        } catch (Exception e) {
            s_log.error("Unexpected exception "+e.getMessage(), e);
        }
	}

    @Override
    protected void getSQLFromDialog(SQLResultListener listener) {
        HibernateDialect dialect = null; 
        List<ITableInfo> tables = dropTableDialog.getTableInfoList();
        boolean cascadeConstraints = dropTableDialog.getCascadeConstraints();
        
        ArrayList<String> result = new ArrayList<String>();
        try {
            orderedTables = getOrderedTables(tables);
            
            dialect = DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                                _session.getApplication().getMainFrame(), 
                                                _session.getMetaData()); 
            String sep = _session.getQueryTokenizer().getSQLStatementSeparator();
            
            
            
            
            if (cascadeConstraints)  {
                for (ITableInfo info: orderedTables) {
                    List<String> dropFKSQLs = 
                        getDropChildFKConstraints(dialect, info);
                    for (String dropFKSQL : dropFKSQLs) {
                        StringBuilder dropSQL = new StringBuilder(); 
                        dropSQL.append(dropFKSQL);
                        dropSQL.append("\n");
                        dropSQL.append(sep);
                        result.add(dropSQL.toString());                        
                    }
                }
            }
            
            
            
            for (ITableInfo info : orderedTables) {
                boolean isMaterializedView = isMaterializedView(info, _session);
                List<String> sqls = dialect.getTableDropSQL(info, 
                                                            false, 
                                                            isMaterializedView);
                for (String sql : sqls) {
                    StringBuilder dropSQL = new StringBuilder(); 
                    dropSQL.append(sql);
                    dropSQL.append("\n");
                    dropSQL.append(sep);
                    result.add(dropSQL.toString());
                }
            }            
        } catch (UnsupportedOperationException e2) {
            
            
            String msg = 
                s_stringMgr.getString("DropTablesCommand.unsupportedOperationMsg",
                                      dialect.getDisplayName());
            _session.showMessage(msg);
        } catch (UserCancelledOperationException e) {
            
        }
        listener.finished(result.toArray(new String[result.size()]));
    }
    
    private List<String> getDropChildFKConstraints(HibernateDialect dialect, 
                                                   ITableInfo ti) {
        ArrayList<String> result = new ArrayList<String>();
        ForeignKeyInfo[] fks = ti.getExportedKeys();
        for (int i = 0; i < fks.length; i++) {
            ForeignKeyInfo info = fks[i];
            String fkName = info.getForeignKeyName();
            String fkTable = info.getForeignKeyTableName();
            result.add(dialect.getDropForeignKeySQL(fkName, fkTable));            
        }
        return result;
    }
    
    private List<ITableInfo> getOrderedTables(final List<ITableInfo> tables) {
        List<ITableInfo> result = tables;
        SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
        
        try {
            
            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    getOrderedTablesCallBack = 
                        new ProgessCallBackDialog(dropTableDialog,
                                                  i18n.PROGRESS_DIALOG_ANALYZE_TITLE,
                                                  tables.size());
                    
                    getOrderedTablesCallBack.setLoadingPrefix(i18n.LOADING_PREFIX);                    
                }
            }, true);
            
            
            
            result = SQLUtilities.getDeletionOrder(tables, 
                                                   md, 
                                                   getOrderedTablesCallBack);
        } catch (SQLException e) {
            s_log.error(
                "Encountered exception while attempting to order tables " +
                "according to constraints: "+e.getMessage(), e);
        }
        return result;
    }
    
    
    private boolean isMaterializedView(ITableInfo ti,
                                      ISession session)
    {
        if (!DialectFactory.isOracle(session.getMetaData())) {
            
            return false;
        }
        if (matViewLookup == null) {
            initMatViewLookup(session, ti.getSchemaName());
        }
        return matViewLookup.contains(ti.getSimpleName());
    }

    private void initMatViewLookup(ISession session, String schema) {
        matViewLookup = new HashSet<String>();
        
        
        
        
        String sql = 
            "SELECT TABLE_NAME FROM ALL_TAB_COMMENTS " +
            "where COMMENTS like 'snapshot%' " +
            "and OWNER = ? ";
        
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = session.getSQLConnection().prepareStatement(sql);
            stmt.setString(1, schema);
            rs = stmt.executeQuery();
            if (rs.next()) {
                String tableName = rs.getString(1);
                matViewLookup.add(tableName);
            }
        } catch (SQLException e) {
            s_log.error(
                "Unexpected exception while attempting to find mat. views " +
                "in schema: "+schema, e);
        } finally {
            SQLUtilities.closeResultSet(rs);
            SQLUtilities.closeStatement(stmt);            
        }
        
    }
    
    private class ShowSQLListener implements ActionListener, SQLResultListener {
        
        
        
        public void finished(String[] sql) {
            if (sql.length == 0) {

                return;
            }
            StringBuffer script = new StringBuffer();
            for (int i = 0; i < sql.length; i++) {
                script.append(sql[i]);
                script.append("\n\n");                
            }

            ErrorDialog sqldialog = 
                new ErrorDialog(dropTableDialog, script.toString());
            
            String title = 
                s_stringMgr.getString("DropTablesCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                
        }

        public void actionPerformed( ActionEvent e) {
            _session.getApplication().getThreadPool().addTask(new GetSQLTask(this));
        }
    }
    
    private class DropTablesActionListener implements ActionListener, SQLResultListener {

        
        public void finished(String[] sqls) {
            final StringBuilder script = new StringBuilder();
            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                if (s_log.isDebugEnabled()) {
                    s_log.debug("DropTablesCommand: adding SQL - "+sql);
                }
                script.append(sql);
                script.append("\n");
            }
            
            GUIUtils.processOnSwingEventThread(new Runnable() {
                public void run() {
                    DropTablesCommand.this.handler = 
                        new DropTableCommandExecHandler(_session);

                    final SQLExecuterTask executer = 
                        new SQLExecuterTask(_session, 
                                            script.toString(), 
                                            DropTablesCommand.this.handler);
                    executer.setSchemaCheck(false);
                    _session.getApplication().getThreadPool().addTask(new Runnable() {
                        public void run() {
                            executer.run();
                            
                            GUIUtils.processOnSwingEventThread(new Runnable() {
                                public void run() {
                                    dropTableDialog.setVisible(false);
                                    _session.getSchemaInfo().reloadAll();
                                }
                            });
                        }
                    });
                    
                    
                }
            });            
        }

        public void actionPerformed(ActionEvent e) {
            if (dropTableDialog == null) {
                return;
            }
            _session.getApplication().getThreadPool().addTask(new GetSQLTask(this));
        }
        
    }
    
    public class GetSQLTask implements Runnable {
        
        private SQLResultListener _listener;
        
        public GetSQLTask(SQLResultListener listener) {
            _listener = listener;
        }

        
        public void run() {
            getSQLFromDialog(_listener);
        }
    }

    private class DropTableCommandExecHandler extends DefaultSQLExecuterHandler {
        
        ProgessCallBackDialog cb = null;
                
        
        int tableCount = 0;
        
        public DropTableCommandExecHandler(ISession session)
        {
            super(session);
            cb = new ProgessCallBackDialog(dropTableDialog,
                                          i18n.PROGRESS_DIALOG_DROP_TITLE,
                                          DropTablesCommand.this.orderedTables.size());
        }

        
        
        @Override
        public void sqlStatementCount(int statementCount) {
            cb.setTotalItems(statementCount);
        }


        
        @Override
        public void sqlToBeExecuted(String sql) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("Statement to be executed: "+sql);
            }
            
            if (sql.startsWith("ALTER")) {
                cb.setLoadingPrefix(i18n.DROPPING_CONSTRAINT_PREFIX);
                
                String[] parts = StringUtilities.split(sql, ' ');
                cb.currentlyLoading(parts[parts.length - 1]);
            } else {         
                cb.setLoadingPrefix(i18n.DROPPING_TABLE_PREFIX);
                if (tableCount < DropTablesCommand.this.orderedTables.size()) {
                    ITableInfo ti = DropTablesCommand.this.orderedTables.get(tableCount);
                    cb.currentlyLoading(ti.getSimpleName());
                }
                tableCount++;
            }
        }
        
        
    }
    
}
