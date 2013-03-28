package net.sourceforge.squirrel_sql.plugins.dbdiff.commands;



import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dbdiff.DBDiffPlugin;

public class SelectCommand implements ICommand
{
    
    private ISession _session;
    
    
    private final DBDiffPlugin _plugin;
    
    
    private final static ILogger log = 
                         LoggerController.createLogger(SelectCommand.class);
               
    
    public SelectCommand(ISession session, DBDiffPlugin plugin)
    {
        super();
        _session = session;
        _plugin = plugin;
    }
    
    
    public void execute()
    {
        IObjectTreeAPI api = _session.getObjectTreeAPIOfActiveSessionWindow();
        if (api != null) {
            IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();
            try {
                _plugin.setDiffSourceSession(_session);
                _plugin.setSelectedDatabaseObjects(dbObjs);
                _plugin.setCompareMenuEnabled(true);
            } catch (Exception e) {
                log.error("Unexected exception: ", e);
            }
        } 
    }
        
}