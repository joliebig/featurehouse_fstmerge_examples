package net.sourceforge.squirrel_sql.plugins.dbdiff.commands;



import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.dbdiff.DiffExecutor;
import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;


public class CompareCommand implements ICommand
{
    
    
    private DiffExecutor executor = null;
        
    
    public CompareCommand(SessionInfoProvider provider)
    {
        super();
        executor = new DiffExecutor(provider);
    }

    
    
    
    public void execute() {
        executor.execute();        
    }
}
