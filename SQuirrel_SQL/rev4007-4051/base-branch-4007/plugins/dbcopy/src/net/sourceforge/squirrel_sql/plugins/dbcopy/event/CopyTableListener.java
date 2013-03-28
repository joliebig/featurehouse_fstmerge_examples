
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;


public interface CopyTableListener {

    public void tableAnalysisStarted(AnalysisEvent e);
    
    public void analyzingTable(TableEvent e);
    
    
    public void copyStarted(CopyEvent e);
    
    
    public void tableCopyStarted(TableEvent e);
    
    
    public void tableCopyFinished(TableEvent e);
    
    
    public void recordCopied(RecordEvent e);
    
    
    public void statementExecuted(StatementEvent e);
    
    
    public void copyFinished(int seconds);
    
    
    public void handleError(ErrorEvent e);
}
