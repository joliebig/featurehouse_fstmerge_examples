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
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class AddPrimaryKeyCommand extends AbstractRefactoringCommand {
    
    
    private final static ILogger log = 
                       LoggerController.createLogger(RemoveColumnCommand.class);
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(AddPrimaryKeyCommand.class);
        
    
    public AddPrimaryKeyCommand(ISession session, IDatabaseObjectInfo[] info)
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
            if (tableHasPrimaryKey()) {
                
                
                String msg = 
                    s_stringMgr.getString("AddPrimaryKeyCommand.primaryKeyExists", 
                                           ti.getSimpleName());
                _session.showErrorMessage(msg);
                return;
            }
            
            super.showColumnListDialog(new AddPrimaryKeyActionListener(), 
                                       new ShowSQLListener(), 
                                       ColumnListDialog.ADD_PRIMARY_KEY_MODE);
        } catch (Exception e) {
            _session.showErrorMessage(e);
            log.error("Unexpected exception "+e.getMessage(), e);
        }
        
        
    }

    protected void getSQLFromDialog(SQLResultListener listener) {
        TableColumnInfo[] columns = columnListDialog.getSelectedColumnList();
        HibernateDialect dialect = null; 
            
        
        String[] result = null;
        try {
            dialect = DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                                _session.getApplication().getMainFrame(), 
                                                _session.getMetaData());

            String pkName = columnListDialog.getPrimaryKeyName();
            
            result = dialect.getAddPrimaryKeySQL(pkName, columns, (ITableInfo)_info[0]);
        } catch (UnsupportedOperationException e2) {
            
            
            String msg = 
                s_stringMgr.getString("AddPrimaryKeyCommand.unsupportedOperationMsg", 
                                      dialect.getDisplayName());
                                      
            _session.showErrorMessage(msg);
        } catch (UserCancelledOperationException e) {
            
        }
        listener.finished(result);
        
    }
    
    
    private class ShowSQLListener implements ActionListener, SQLResultListener {
        public void actionPerformed( ActionEvent e) {
            getSQLFromDialog(this);
        }
        
        public void finished(String[] addPKSQLs) {
            if (addPKSQLs == null || addPKSQLs.length == 0) {

                return;
            }

            StringBuffer script = new StringBuffer();
            for (int i = 0; i < addPKSQLs.length; i++) {
                script.append(addPKSQLs[i]);
                script.append(";\n\n");
            }
            
            ErrorDialog sqldialog = 
                new ErrorDialog(columnListDialog, script.toString());
            
            String title = 
                s_stringMgr.getString("AddPrimaryKeyCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                
            
        }
    }
    
    private class AddPrimaryKeyActionListener implements ActionListener, 
                                                         SQLResultListener 
    {
        public void finished(String[] addPKSQLs) {
            CommandExecHandler handler = new CommandExecHandler(_session);
            if (addPKSQLs != null) {
                for (int i = 0; i < addPKSQLs.length; i++) {
                    String addPKSQL = addPKSQLs[i];
                    log.info("AddPrimaryKeyCommand: executing SQL - "+addPKSQL);
                    SQLExecuterTask executer = 
                        new SQLExecuterTask(_session, addPKSQL, handler);
                    executer.run();
                }
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