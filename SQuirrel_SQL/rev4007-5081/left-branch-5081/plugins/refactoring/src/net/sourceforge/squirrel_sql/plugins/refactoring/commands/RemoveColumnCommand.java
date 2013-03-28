package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class RemoveColumnCommand extends AbstractRefactoringCommand
{
    
    
    private final static ILogger log = 
                       LoggerController.createLogger(RemoveColumnCommand.class);
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(RemoveColumnCommand.class);
    
    
    public RemoveColumnCommand(ISession session, IDatabaseObjectInfo[] info)
    {
        super(session, info);
    }
    
    
    public void execute()
    {
        if (! (_info[0] instanceof ITableInfo)) {
            return;
        }
        try {
            ITableInfo ti = (ITableInfo)_info[0];
            TableColumnInfo[] columns = 
                _session.getSQLConnection().getSQLMetaData().getColumnInfo(ti);

            if (columns.length < 2) {
                
                
                String msg = 
                    s_stringMgr.getString("RemoveColumnAction.singleColumnMessage");
                _session.showErrorMessage(msg);
                return;
            }
            
            try {
                HibernateDialect dialect =  
                    DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                              _session.getApplication().getMainFrame(), 
                                              _session.getMetaData());
                if (!dialect.supportsDropColumn()) {
                    
                    
                    String msg = 
                        s_stringMgr.getString("RemoveColumnAction.removeColumnNotSupported",
                                              dialect.getDisplayName());
                    _session.showErrorMessage(msg);
                    return;                    
                }
            } catch (UserCancelledOperationException e) {
                log.info("User cancelled add column request");
                return;
            }        
            
            
            
            super.showColumnListDialog(new DropActionListener(), 
                                       new DropSQLActionListener(), 0);
        } catch (SQLException e) {
            log.error("Unexpected exception "+e.getMessage(), e);
        }
        
        
    }

    protected void getSQLFromDialog(SQLResultListener listener) {
        TableColumnInfo[] columns = columnListDialog.getSelectedColumnList();
        
        
        HibernateDialect dialect = null; 
            
        
        String[] result = new String[columns.length];
        try {
            dialect = DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                                _session.getApplication().getMainFrame(), 
                                                _session.getMetaData());
            
            String tableName = _info[0].getQualifiedName();
            for (int i = 0; i < columns.length; i++) {
                TableColumnInfo info = columns[i];
                String columnName = info.getColumnName();
                result[i] = dialect.getColumnDropSQL(tableName, columnName);
            }
        } catch (UnsupportedOperationException e2) {
            
            
            String msg = 
                s_stringMgr.getString("RemoveColumnCommand.unsupportedOperationMsg", 
                                      dialect.getDisplayName());
                                      
            _session.showMessage(msg);
        } catch (UserCancelledOperationException e) {
            
        }
        listener.finished(result);        
    }
    
    
    private class DropSQLActionListener implements ActionListener, 
                                                   SQLResultListener 
    {
        public void finished(String[] sqls) {
            if (sqls == null || sqls.length == 0) {

                return;
            }

            StringBuffer script = new StringBuffer();
            for (int i = 0; i < sqls.length; i++) {
                script.append(sqls[i]);
                script.append(";\n\n");
            }
            
            ErrorDialog sqldialog = 
                new ErrorDialog(columnListDialog, script.toString());
            
            String title = 
                s_stringMgr.getString("RemoveColumnCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                                        
        }
        
        public void actionPerformed(ActionEvent e) {
            getSQLFromDialog(this);
        }
    }
    
    private class DropActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            
            
            if (columnListDialog == null) {
                System.err.println("dialog was null");
                return;
            }
            HibernateDialect dialect = null;
            try {
                dialect =  
                    DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                              _session.getApplication().getMainFrame(), 
                                              _session.getMetaData());
            } catch (UserCancelledOperationException ex) {
                log.info("User cancelled add column request");
                return;                
            }
            
            CommandExecHandler handler = new CommandExecHandler(_session);
            
            
            TableColumnInfo[] columns = columnListDialog.getSelectedColumnList();
            for (int i = 0; i < columns.length; i++) {
                TableColumnInfo column = columns[i];
                String dropSQL = 
                    dialect.getColumnDropSQL(column.getTableName(), 
                                             column.getColumnName());
                log.info("AddColumnCommand: executing SQL - "+dropSQL);
                SQLExecuterTask executer = 
                    new SQLExecuterTask(_session, 
                                        dropSQL, 
                                        handler);
    
                
                executer.run();                
                
                if (handler.exceptionEncountered()) {
                    
                    break;
                }
                
            }
            columnListDialog.setVisible(false);
        }
        
    }
        
}