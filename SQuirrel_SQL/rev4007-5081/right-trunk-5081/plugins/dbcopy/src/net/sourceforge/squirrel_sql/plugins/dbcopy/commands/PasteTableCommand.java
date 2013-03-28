package net.sourceforge.squirrel_sql.plugins.dbcopy.commands;



import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.dbcopy.CopyExecutor;
import net.sourceforge.squirrel_sql.plugins.dbcopy.CopyProgressMonitor;
import net.sourceforge.squirrel_sql.plugins.dbcopy.CopyScripter;
import net.sourceforge.squirrel_sql.plugins.dbcopy.I18NBaseObject;
import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;


public class PasteTableCommand extends I18NBaseObject 
                                  implements ICommand
{
    
    
    @SuppressWarnings("unused")
    private SessionInfoProvider prov = null;

    
    private CopyExecutor executor = null;
    
    
    private CopyProgressMonitor monitor = null;
    
    
    private CopyScripter copyScripter = null;
        
    
    public PasteTableCommand(SessionInfoProvider provider)
    {
        super();
        prov = provider;
        executor = new CopyExecutor(provider);
        monitor = new CopyProgressMonitor(provider);
        copyScripter = new CopyScripter();
        executor.addListener(monitor);
        executor.addListener(copyScripter);
        executor.setPref(monitor);
        monitor.setExecutor(executor);
        
    }

    
    
    
    public void execute() {
        executor.execute();        
    }
}
