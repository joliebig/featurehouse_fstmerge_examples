
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.dialects.UserCancelledOperationException;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.AnalysisEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.ErrorEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.RecordEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.gui.DualProgressBarDialog;
import net.sourceforge.squirrel_sql.plugins.dbcopy.util.DBUtil;



public class CopyProgressMonitor extends I18NBaseObject
                                 implements CopyTableListener,
                                            UICallbacks {

    private SessionInfoProvider prov = null;
    
    
    private JFrame parent = null;
    
    
    private boolean deleteAllTableData = false;

    
    private CopyExecutor executor = null;
    
    
    private final static ILogger log = 
                         LoggerController.createLogger(CopyProgressMonitor.class);
    
    private ProgressMonitor pm = null;
    
    private static CommentSpec[] commentSpecs =
        new CommentSpec[]
        {
            new CommentSpec("/*", "*/"),
            new CommentSpec("--", "\n")
        };
  
  private static CodeReformator formatter = 
      new CodeReformator(";", commentSpecs);
  
    
    public CopyProgressMonitor(SessionInfoProvider provider) {
        prov = provider;
        parent = prov.getCopyDestSession().getApplication().getMainFrame();
    }
    
    
    
    
    public void copyStarted(CopyEvent e) {
        if (pm != null) {
            pm.setProgress(pm.getMaximum());
        }
        prov = e.getSessionInfoProvider();
        int numTables = prov.getSourceSelectedDatabaseObjects().length;
        int[] tableCounts = e.getTableCounts();
        
        createProgressDialog();
        DualProgressBarDialog.setBottomBarMinMax(0, numTables);
        DualProgressBarDialog.setBottomBarValue(0);
        DualProgressBarDialog.setTopBarValue(0);
        DualProgressBarDialog.setTableCounts(tableCounts);
    }
    
    
    public void tableCopyStarted(TableEvent e) {
        String bottomMessage = getMessage("CopyProgressMonitor.copyingTable", 
                                           new String[] {e.getTableName(),
                                                      ""+e.getTableNumber(),
                                                      ""+e.getTableCount()});
        DualProgressBarDialog.setBottomMessage(bottomMessage);
    }

    
    public void recordCopied(RecordEvent e) {
        DualProgressBarDialog.setTopBarMinMax(0, e.getRecordCount());
        String topMessage = getMessage("CopyProgressMonitor.copyingRecords",
                                       new String[]{""+e.getRecordNumber(), 
                                                    ""+e.getRecordCount()});
        DualProgressBarDialog.setTopMessage(topMessage);
        DualProgressBarDialog.incrementTopBar(1);
    }

    
    public void statementExecuted(StatementEvent e) {
        
    }

    
    public void tableCopyFinished(TableEvent e) {
        DualProgressBarDialog.setTopBarValue(0);
        DualProgressBarDialog.incrementBottomBar(1);
    }
    
    
    public void copyFinished(int seconds) {
        DualProgressBarDialog.stopTimer();
        DualProgressBarDialog.setVisible(false);
        DualProgressBarDialog.dispose();
        String title = getMessage("CopyProgressMonitor.successTitle");
        String message = getMessage("CopyProgressMonitor.successMessage",
                                    seconds);
        showMessageDialog(message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    private String wordWrap(String data, int length) {
        String result = "";
        if (data.length() > length) {
            String[] parts = data.split("\\s");
            StringBuffer tmp = new StringBuffer();
            int count = 0;
            for (int i = 0; i < parts.length; i++) {
                count += parts[i].length();
                if (count > length) {
                    count = 0;
                    tmp.append("\n");
                } else {
                    tmp.append(" ");
                }
                tmp.append(parts[i]);
            }
            result = tmp.toString();
        } else {
            result = data;
        }
        return result;
    }
    
    
    public void handleError(ErrorEvent e) {
        DualProgressBarDialog.stopTimer();
        DualProgressBarDialog.setVisible(false);
        if (e.getType() == ErrorEvent.SETUP_AUTO_COMMIT_TYPE) {
            String exMsg = "";
            if (e.getException() != null) {
                exMsg = e.getException().getMessage();
            }            
            String message = 
                getMessage("CopyProgressMonitor.setupAutoCommitException",
                           exMsg);
            String title = 
                getMessage("CopyProgressMonitor.setupAutoCommitExceptionTitle");
            int messageType = JOptionPane.ERROR_MESSAGE;
            showMessageDialog(message, title, messageType);
        }
        if (e.getType() == ErrorEvent.RESTORE_AUTO_COMMIT_TYPE) {
            String exMsg = "";
            if (e.getException() != null) {
                exMsg = e.getException().getMessage();
            }
            String message = 
                getMessage("CopyProgressMonitor.restoreAutoCommitException", 
                           exMsg);
            String title =
                getMessage("CopyProgressMonitor.restoreAutoCommitExceptionTitle");
            int messageType = JOptionPane.ERROR_MESSAGE;
            showMessageDialog(message, title, messageType);                                  
        }
        if (e.getType() == ErrorEvent.SQL_EXCEPTION_TYPE) {
            String exMessage = wordWrap(e.getException().getMessage(), 80);
            String sql = formatter.reformat(DBUtil.getLastStatement());
            String values = DBUtil.getLastStatementValues();
            String sqlAndValues = sql;
            if (values != null) {
                sqlAndValues += values;
            } else {
                sqlAndValues += "\n(No bind variables)";
            }
            int errorCode = ((SQLException)e.getException()).getErrorCode();
            log.error("SQL Error code = "+errorCode+" sql = "+sqlAndValues,
                      e.getException());
            String message = getMessage("CopyProgressMonitor.sqlErrorMessage",
                                        new String[]{exMessage, 
                                                     ""+errorCode,
                                                     sqlAndValues});
            String title = getMessage("CopyProgressMonitor.sqlErrorTitle");
            showMessageDialog(message, title, JOptionPane.ERROR_MESSAGE);
        }
        if (e.getType() == ErrorEvent.MAPPING_EXCEPTION_TYPE) {
            String title = getMessage("CopyProgressMonitor.mappingErrorTitle");
            String message = getMappingExceptionMessage(e.getException());
            log.error(message, e.getException());
            showMessageDialog(message, title, JOptionPane.ERROR_MESSAGE);
            if (pm != null) {
                pm.setProgress(pm.getMaximum());
            }
        }
        if (e.getType() == ErrorEvent.USER_CANCELLED_EXCEPTION_TYPE) {
            String title = getMessage("CopyProgressMonitor.cancelledTitle");
            String message = getMessage("CopyProgressMonitor.cancelledMessage");
            showMessageDialog(message, title, JOptionPane.INFORMATION_MESSAGE);            
        }
        if (e.getType() == ErrorEvent.GENERIC_EXCEPTION) {
            String exmessage = e.getException().getMessage();
            String message = getMessage("CopyProgressMonitor.errorMessage",
                                        new String[]{exmessage});
            String title = getMessage("CopyProgressMonitor.errorTitle");
            showMessageDialog(message, title, JOptionPane.INFORMATION_MESSAGE);
            
        }
        if (e.getException() != null) {
            log.error("handleError: exception="+e.getException().getMessage(), 
                      e.getException());
        }
        
        
        
        

    }

    private String getMappingExceptionMessage(Exception e) {
        String message = "";
        if (e.getMessage().indexOf(":") != -1) {
            String[] parts = e.getMessage().split(":");
            try {
                int typeCode = Integer.parseInt(parts[1].trim());
                String typeName = JDBCTypeMapper.getJdbcTypeName(typeCode);
                message = getMessage("CopyProgressMonitor.mappingErrorMessage",
                                     new String[]{e.getMessage(), typeName});
            } catch (NumberFormatException nfe) {
                message = e.getMessage();
            }
        } else {
            message = e.getMessage();
        }                
        return message;
    }    
    
    private void showMessageDialog(final String message, 
                                   final String title, 
                                   final int messageType) 
    {
        final JFrame f = parent;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog(f, 
                        message, 
                        title, 
                        messageType);
            }
        });
    }
    
    private void createProgressDialog() {
        ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                executor.cancel();
            }
        };
        DualProgressBarDialog.getDialog(parent,
                                        getMessage("CopyProgressMonitor.copyProgressDialogTitle"), 
                                        false, 
                                        listener);
        DualProgressBarDialog.startTimer();
    }

    
    public void setExecutor(CopyExecutor executor) {
        this.executor = executor;
    }

    
    public CopyExecutor getExecutor() {
        return executor;
    }

    
    private int showConfirmDeleteDialog(String tableName) {
        final String message = getMessage("CopyProgressMonitor.deleteRecordsMessage",
                                          tableName);
        
        final ConfirmMessageResult result = new ConfirmMessageResult();
        
        final String[] buttons = {"Yes", "Yes to all", "No", "Cancel" };
        
        if (SwingUtilities.isEventDispatchThread()) {
            result.option = 
                JOptionPane.showOptionDialog(parent, 
                                             message, 
                                             "Confirmation",
                                             JOptionPane.DEFAULT_OPTION,
                                             JOptionPane.QUESTION_MESSAGE,
                                             null,
                                             buttons,
                                             buttons[2]);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        result.option = 
                            JOptionPane.showOptionDialog(parent, 
                                                         message, 
                                                         "Confirmation",
                                                         JOptionPane.DEFAULT_OPTION,
                                                         JOptionPane.QUESTION_MESSAGE,
                                                         null,
                                                         buttons,
                                                         buttons[2]);
                    }
                });
            } catch (Exception e) {
                log.error(
                        "showConfirmDeleteDialog: encountered unexpected exception ",
                        e);
            }
        }
        return result.option;
    }    
    
    private String showTextInputDialog(final Object message,
                                       final String title,
                                       final int messageType,
                                       final Icon icon,
                                       final Object initialValue) {
        
        final StringBuffer result = new StringBuffer();
        if (SwingUtilities.isEventDispatchThread()) {
            String tmp = (String)JOptionPane.showInputDialog(parent, 
                                                             message, 
                                                             title, 
                                                             messageType, 
                                                             icon, 
                                                             null, 
                                                             initialValue);
            result.append(tmp);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        String tmp = 
                            (String)JOptionPane.showInputDialog(parent, 
                                                                message, 
                                                                title, 
                                                                messageType, 
                                                                icon, 
                                                                null, 
                                                                initialValue);
                        result.append(tmp);
                    }
                });
            } catch (Exception e) {
                log.error(
                    "showTextInputDialog: encountered unexpected exception ",
                    e);
            }
        }
        return result.toString();
    }
    
    
    public boolean deleteTableData(String tableName) 
        throws UserCancelledOperationException 
    {
        if (deleteAllTableData) {
            return true;
        }
        int option = showConfirmDeleteDialog(tableName);
        if (option == 0) { 
            return true;
        }
        if (option == 1) { 
            deleteAllTableData = true;
            return true;
        }
        if (option == 2) { 
            return false;
        }                  
        if (option == 3) { 
            throw new UserCancelledOperationException();
        }
        return false;
    }
    
    public boolean appendRecordsToExisting(String tableName) {
        
        
        return false;
    }

    class ConfirmMessageResult {
        int option;
    }

    
    public void analyzingTable(TableEvent e) {
        if (pm.isCanceled()) {
            
        }
        
        pm.setNote(getMessage("CopyProgressMonitor.analyzingTableMessage")+e.getTableName());
        pm.setProgress(e.getTableNumber());
    }

    
    public void tableAnalysisStarted(AnalysisEvent e) {
        SessionInfoProvider prov = e.getSessionInfoProvider(); 
        
        pm = new ProgressMonitor(parent,  
                                 "Analyzing column names in tables to be copied",
                                 "",
                                 0,
                                 prov.getSourceSelectedDatabaseObjects().length); 
        
    }

}
