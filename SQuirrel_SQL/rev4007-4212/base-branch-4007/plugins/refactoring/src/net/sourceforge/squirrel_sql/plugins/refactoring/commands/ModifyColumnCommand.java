package net.sourceforge.squirrel_sql.plugins.refactoring.commands;



import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.gui.db.ColumnDetailDialog;
import net.sourceforge.squirrel_sql.client.gui.db.ColumnListDialog;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
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
import net.sourceforge.squirrel_sql.plugins.refactoring.DBUtil;

import org.hibernate.HibernateException;


public class ModifyColumnCommand extends AbstractRefactoringCommand
{
    
    
    private final static ILogger log = 
                       LoggerController.createLogger(RemoveColumnCommand.class);
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(RemoveColumnCommand.class);
    
    private ColumnListDialog listDialog = null;
    
    private MainFrame mainFrame = null;    
    
    private TableColumnInfo columnToModify = null;
    
    private HibernateDialect dialect = null;    
    
    static interface i18n {
        
        
        String MODIFY_ONE_COL_MSG = 
            s_stringMgr.getString("ModifyColumnCommand.modifyOneColMsg");        
    }
    
    
    public ModifyColumnCommand(ISession session, IDatabaseObjectInfo[] info)
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
            
            
            if (columns.length == 1) {
                columnToModify = columns[0];
                showColumnDetailsDialog();
                return;
            }
            
            if (listDialog == null) {
                listDialog = 
                    new ColumnListDialog(columns, 
                                         ColumnListDialog.MODIFY_COLUMN_MODE);
                ActionListener listener = 
                    new ColumnListSelectionActionListener();
                listDialog.addColumnSelectionListener(listener);
                mainFrame = _session.getApplication().getMainFrame();
                listDialog.setLocationRelativeTo(mainFrame);
                listDialog.setSingleSelection();
            }
            listDialog.setTableName(ti.getQualifiedName());
            listDialog.setVisible(true);
        } catch (SQLException e) {
            log.error("Unexpected exception "+e.getMessage(), e);
        }
    }

    protected void getSQLFromDialog(SQLResultListener listener) {
        TableColumnInfo to = columnDetailDialog.getColumnInfo();
        String dbName = columnDetailDialog.getSelectedDBName();
        HibernateDialect dialect = DialectFactory.getDialect(dbName);
        
        String[] result = null;
        try {
            result = DBUtil.getAlterSQLForColumnChange(columnToModify, to, dialect);
        } catch (HibernateException e1) {
            String dataType = columnDetailDialog.getSelectedTypeName();
            
            JOptionPane.showMessageDialog(columnDetailDialog, 
                    "The "+dialect.getDisplayName()+" dialect doesn't support the type "+dataType, 
                    "Missing Dialect Type Mapping", 
                    JOptionPane.ERROR_MESSAGE);            
        } catch (UnsupportedOperationException e2) {
            
            
            
            
            _session.showMessage(e2.getMessage());
        }
        listener.finished(result);
        
    }
    
    private void showColumnDetailsDialog() {
        try {
            dialect =  
                DialectFactory.getDialect(DialectFactory.DEST_TYPE, 
                                          _session.getApplication().getMainFrame(), 
                                          _session.getMetaData());
            String dbName = dialect.getDisplayName();                
            columnDetailDialog = 
                new ColumnDetailDialog(ColumnDetailDialog.MODIFY_MODE);
            columnDetailDialog.setExistingColumnInfo(columnToModify);
            columnDetailDialog.setTableName(_info[0].getQualifiedName());
            columnDetailDialog.addShowSQLListener(new ShowSQLButtonListener());
            columnDetailDialog.addEditSQLListener(new EditSQLListener());
            columnDetailDialog.addExecuteListener(new OKButtonListener());
            mainFrame = _session.getApplication().getMainFrame();
            columnDetailDialog.setLocationRelativeTo(mainFrame);
            columnDetailDialog.setSelectedDialect(dbName);
            columnDetailDialog.setVisible(true);
        } catch (UserCancelledOperationException ex) {
            log.info("User cancelled the operation", ex);
        }        
    }
    
    private class ColumnListSelectionActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (listDialog == null) {
                System.err.println("dialog was null");
                return;
            }
            listDialog.setVisible(false);
            TableColumnInfo[] colInfos = listDialog.getSelectedColumnList();
            if (colInfos == null || colInfos.length != 1) {
                _session.showMessage(i18n.MODIFY_ONE_COL_MSG);
                return;
            }
            columnToModify = colInfos[0];
            showColumnDetailsDialog();
        }
    }
        
    private class OKButtonListener implements ActionListener, SQLResultListener {

        public void finished(String[] sqls) {
            CommandExecHandler handler = new CommandExecHandler(_session);
            
            if (sqls == null || sqls.length == 0) {
                
                return;
            }            
            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                
                log.info("ModifyColumnCommand: executing SQL - "+sql);
                
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
        
        
        public void actionPerformed(ActionEvent e) {
            getSQLFromDialog(this);
        }
        
    }

    private class ShowSQLButtonListener implements ActionListener, SQLResultListener {

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
                new ErrorDialog(columnDetailDialog, script.toString());
            
            String title = 
                s_stringMgr.getString("ModifyColumnCommand.sqlDialogTitle");
            sqldialog.setTitle(title);
            sqldialog.setVisible(true);                            
        }
        
        
        public void actionPerformed(ActionEvent e) {
            getSQLFromDialog(this);
        }
        
    }
    
}