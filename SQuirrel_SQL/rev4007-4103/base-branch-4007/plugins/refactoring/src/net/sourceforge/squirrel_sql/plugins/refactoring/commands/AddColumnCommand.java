package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnDetailDialog;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLExecuterTask;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.HibernateDialect;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.gui.ErrorDialog;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.refactoring.DBUtil;

import org.hibernate.HibernateException;



public class AddColumnCommand extends AbstractRefactoringCommand
{
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(AddColumnCommand.class);

    
    private final static ILogger log = 
                      LoggerController.createLogger(AddColumnCommand.class);
    
    private HibernateDialect dialect = null;
    
    private MainFrame mainFrame = null;
    
    
    public AddColumnCommand(ISession session, IDatabaseObjectInfo[] info)
    {
        super(session, info);
    }
    
    
    public void execute()
    {
        String tableName = _info[0].getQualifiedName();
        try {
            dialect =  
                DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                          _session.getApplication().getMainFrame(), 
                                          _session.getMetaData());
            String dbName = dialect.getDisplayName();
            columnDetailDialog = new ColumnDetailDialog(ColumnDetailDialog.ADD_MODE);
            columnDetailDialog.setTableName(tableName);
            columnDetailDialog.addExecuteListener(new AddButtonListener());
            columnDetailDialog.addEditSQLListener(new EditSQLListener());
            columnDetailDialog.addShowSQLListener(new ShowSQLButtonListener());
            columnDetailDialog.addDialectListListener(new DialectListListener());
            mainFrame = _session.getApplication().getMainFrame();
            columnDetailDialog.setLocationRelativeTo(mainFrame);
            columnDetailDialog.setSelectedDialect(dbName);
            columnDetailDialog.setVisible(true);
        } catch (UserCancelledOperationException e) {
            log.info("User cancelled add column request");
            return;
        }        
        
    }

    protected void getSQLFromDialog(SQLResultListener listener) {
        TableColumnInfo info = columnDetailDialog.getColumnInfo();
        String[] result = null;
        try {
            result = DBUtil.getAlterSQLForColumnAddition(info, dialect);
        } catch (HibernateException e1) {
            String dataType = columnDetailDialog.getSelectedTypeName();
            JOptionPane.showMessageDialog(
                    columnDetailDialog, 
                    "The "+dialect.getDisplayName()+" dialect doesn't support the type "+dataType, 
                    "Missing Dialect Type Mapping", 
                    JOptionPane.ERROR_MESSAGE);            
        } catch (UnsupportedOperationException e2) {
            String dbName = dialect.getDisplayName();
            
            
            String msg = 
                s_stringMgr.getString("AddColumnCommand.unsupportedOperationMsg",
                                      dbName);
            _session.showMessage(msg);

        }
        listener.finished(result);        
    }
    
    private class AddButtonListener implements ActionListener, SQLResultListener {

        public void actionPerformed(ActionEvent e) {
            String columnName = columnDetailDialog.getColumnInfo().getColumnName();
            String tableName = columnDetailDialog.getTableName();
            if (!isColumnNameUnique(columnName)) {
                JOptionPane.showMessageDialog(
                        columnDetailDialog, 
                        "Table "+tableName+" already has a column called "+columnName, 
                        "Problem", 
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            getSQLFromDialog(this);
        }
            
            
        public void finished(String[] sqls) {    
            
            if (sqls == null || sqls.length == 0) {
                return;
            }
            CommandExecHandler handler = new CommandExecHandler(_session);            

            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                
                log.info("AddColumnCommand: executing SQL - "+sql);
                
                SQLExecuterTask executer = 
                    new SQLExecuterTask(_session, 
                                        sql, 
                                        handler);
    
                
                executer.run();                
                
                if (handler.exceptionEncountered()) {
                    
                    break;
                }
            }
            columnDetailDialog.setVisible(false);
        }
        
        
        private boolean isColumnNameUnique(String columnName) {
            boolean result = true;
            SQLDatabaseMetaData d = _session.getSQLConnection().getSQLMetaData();
            try {
            TableColumnInfo[] columnInfos = d.getColumnInfo((ITableInfo)_info[0]);
                for (int i = 0; i < columnInfos.length; i++) {
                    TableColumnInfo columnInfo = columnInfos[i];
                    String existingColumnName = columnInfo.getColumnName();
                    if (columnName.equalsIgnoreCase(existingColumnName)) {
                        result = false;
                        break;
                    }
                }
            } catch (SQLException e) {
                log.error("Unexpected exception - "+e.getMessage(), e);
            }
            return result;
        }
    }
    
    private class ShowSQLButtonListener implements ActionListener, SQLResultListener {

        public void finished(String[] sqls) {
            if (sqls != null) {
                StringBuffer script = new StringBuffer();
                for (int i = 0; i < sqls.length; i++) {
                    script.append(sqls[i]);
                    script.append(";\n\n");
                }
                
                ErrorDialog sqldialog = 
                    new ErrorDialog(columnDetailDialog, script.toString());
                
                String title = 
                    s_stringMgr.getString("AddColumnCommand.sqlDialogTitle");
                sqldialog.setTitle(title);
                sqldialog.setVisible(true);
            }
        }
        
        public void actionPerformed(ActionEvent e) {
            getSQLFromDialog(this);
        }
        
    }
    
    private class DialectListListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            String dbName = columnDetailDialog.getSelectedDBName();
            dialect = DialectFactory.getDialect(dbName);
        }
    }
}