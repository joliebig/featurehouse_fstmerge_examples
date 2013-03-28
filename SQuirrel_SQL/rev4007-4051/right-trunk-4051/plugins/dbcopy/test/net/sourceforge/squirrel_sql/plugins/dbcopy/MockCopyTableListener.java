
package net.sourceforge.squirrel_sql.plugins.dbcopy;

import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableAdaptor;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.CopyTableListener;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.ErrorEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.RecordEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.StatementEvent;
import net.sourceforge.squirrel_sql.plugins.dbcopy.event.TableEvent;

public class MockCopyTableListener extends CopyTableAdaptor 
                                   implements CopyTableListener {

    private boolean showSqlStatements = false;
    
    public MockCopyTableListener() {
    }
    
    public void copyStarted(CopyEvent e) {
        

    }

    public void tableCopyStarted(TableEvent e) {
        int total = e.getTableCount();
        int num = e.getTableNumber();
        System.out.println("Started copying table "+e.getTableName()+" ( "+
                           num+" of "+total+" )");
    }

    public void tableCopyFinished(TableEvent e) {
        int total = e.getTableCount();
        int num = e.getTableNumber();
        System.out.println("Finished copying table "+e.getTableName()+" ( "+
                           num+" of "+total+" )");
    }

    public void recordCopied(RecordEvent e) {
        
    }

    public void statementExecuted(StatementEvent e) {
        if (showSqlStatements) {
            System.out.println(e.getStatement());
            String[] bindVarVals = e.getBindValues();
            if (bindVarVals != null && bindVarVals.length > 0) {
                System.out.println("bindVarVals: ");
                for (int i = 0; i < bindVarVals.length; i++) {
                    String string = bindVarVals[i];
                    System.out.println("bindVarVals["+i+"] = "+string);
                }
            }
        }
    }

    public void copyFinished(int seconds) {
        System.out.println("Copy operation finished in "+seconds+" seconds");
    }

    public void handleError(ErrorEvent e) {
        Exception ex = e.getException();
        System.err.println("ErrorEvent type = "+e.getType());
        System.err.println("Encountered unexpected exception - "+ex.getMessage());
        ex.printStackTrace();
    }

    
    public void setShowSqlStatements(boolean showSqlStatements) {
        this.showSqlStatements = showSqlStatements;
    }

    
    public boolean isShowSqlStatements() {
        return showSqlStatements;
    }

}
