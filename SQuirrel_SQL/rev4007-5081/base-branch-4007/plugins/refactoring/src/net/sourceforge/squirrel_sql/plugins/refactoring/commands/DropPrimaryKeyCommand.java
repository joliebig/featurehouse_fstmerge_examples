package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class DropPrimaryKeyCommand extends AbstractRefactoringCommand {
    
    
    private final static ILogger log = 
                       LoggerController.createLogger(RemoveColumnCommand.class);
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DropPrimaryKeyCommand.class);
        
    
    public DropPrimaryKeyCommand(ISession session, IDatabaseObjectInfo[] info)
    {
        super(session, info);
    }
    
    
    public void execute()
    {
        if (! (_info[0] instanceof ITableInfo)) {
            return;
        }
        ITableInfo ti = (ITableInfo)_info[0];
        try {
            if (!tableHasPrimaryKey()) {
                
                
                String msg = 
                    s_stringMgr.getString("DropPrimaryKeyCommand.noKeyToDrop",
                                          ti.getSimpleName());
                _session.showErrorMessage(msg);
                return;
            }
            super.showColumnListDialog(new DropPrimaryKeyActionListener(), 
                                       new ShowSQLListener(), 
                                       ColumnListDialog.DROP_PRIMARY_KEY_MODE);
        } catch (Exception e) {
            log.error("Unexpected exception "+e.getMessage(), e);
        }
        
        
    }

    protected void getSQLFromDialog(SQLResultListener listener) {
        HibernateDialect dialect = null; 
        
        String result = null;
        try {
            dialect = DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                                _session.getApplication().getMainFrame(), 
                                                _session.getMetaData());            
            result = 
                dialect.getDropPrimaryKeySQL(this.pkName, 
                                             columnListDialog.getTableName());
        } catch (UnsupportedOperationException e2) {
            
            
            String msg = 
                s_stringMgr.getString("DropPrimaryKeyCommand.unsupportedOperationMsg", 
                                      dialect.getDisplayName());
                                      
            _session.showMessage(msg);
        } catch (UserCancelledOperationException e) {
            
        }
        listener.finished(new String[] { result });
        
    }
    
    
    private class ShowSQLListener implements ActionListener, SQLResultListener {
        
        public void finished(String[] sql) {
            if (sql.length == 0) {

                return;
            }
            StringBuffer script = new StringBuffer();
            for (int i = 0; i < sql.length; i++) {
                script.append(sql[i]);
                script.append(";\n\n");                
            }

            ErrorDialog sqldialog = 
                new ErrorDialog(columnListDialog, script.toString());
            
            String title = 
                s_stringMgr.getString("DropPrimaryKeyCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                            
        }
        
        public void actionPerformed( ActionEvent e) {
            getSQLFromDialog(this);
        }
    }
    
    private class DropPrimaryKeyActionListener implements ActionListener,
                                                          SQLResultListener {
        public void finished(String[] sqls) {
            CommandExecHandler handler = new CommandExecHandler(_session);
            
            
            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                log.info("DropPrimaryKeyCommand: executing SQL - "+sql);
                SQLExecuterTask executer = 
                    new SQLExecuterTask(_session, sql, handler);
                executer.run();                            
            }
            columnListDialog.setVisible(false);            
        }
        
        public void actionPerformed(ActionEvent e) {
            if (columnListDialog == null) {
                System.err.println("dialog was null");
                return;
            }
            getSQLFromDialog(this);
        }
        
    }
        
}