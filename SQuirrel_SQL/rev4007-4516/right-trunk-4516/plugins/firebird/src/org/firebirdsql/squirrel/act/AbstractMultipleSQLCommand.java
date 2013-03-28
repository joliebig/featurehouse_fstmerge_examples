package org.firebirdsql.squirrel.act;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

abstract class AbstractMultipleSQLCommand implements ICommand
{
    
    private ISession _session;

    
    @SuppressWarnings("unused")
    private final IPlugin _plugin;

    
    public AbstractMultipleSQLCommand(ISession session, IPlugin plugin)
    {
        super();
        if (session == null)
        {
            throw new IllegalArgumentException("ISession == null");
        }
        if (plugin == null)
        {
            throw new IllegalArgumentException("IPlugin == null");
        }

        _session = session;
        _plugin = plugin;
    }

    
    public void execute()
    {
        final StringBuffer buf = new StringBuffer(2048);
        final String sep = 
            "\n" + _session.getQueryTokenizer().getSQLStatementSeparator();

        final IObjectTreeAPI api = _session.getSessionInternalFrame().getObjectTreeAPI();
        final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

        for (int i = 0; i < dbObjs.length; ++i)
        {
            final String cmd = getSQL(dbObjs[i]);
            if (cmd != null && cmd.length() > 0)
            {
                buf.append(cmd).append(sep).append('\n');
            }
        }

        
        if (buf.length() > 0)
        {
            _session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(buf.toString(), true);
            _session.getSessionInternalFrame().getSQLPanelAPI().executeCurrentSQL();
            _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
        }
    }

    
    protected abstract String getSQL(IDatabaseObjectInfo dbObj);
}
