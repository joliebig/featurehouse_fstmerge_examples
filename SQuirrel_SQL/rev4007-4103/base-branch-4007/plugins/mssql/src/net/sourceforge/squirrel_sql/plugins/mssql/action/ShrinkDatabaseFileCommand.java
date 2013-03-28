package net.sourceforge.squirrel_sql.plugins.mssql.action;



import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class ShrinkDatabaseFileCommand implements ICommand {
	private ISession _session;
	private final MssqlPlugin _plugin;

    String _catalogName;
    String _databaseFileName;
    
	public ShrinkDatabaseFileCommand(ISession session, MssqlPlugin plugin, String catalogName, String databaseFileName) {
		super();
		if (session == null)
			throw new IllegalArgumentException("ISession == null");

        _session = session;
		_plugin = plugin;
		_catalogName = catalogName;
        _databaseFileName = databaseFileName;
	}

	public void execute() {
		final String sqlSep = _session.getQueryTokenizer().getSQLStatementSeparator();
		final StringBuffer buf = new StringBuffer();
        buf.append("USE ");
        buf.append(_catalogName);
        buf.append(sqlSep + "\n");
        buf.append("DBCC SHRINKFILE (");
        buf.append(_databaseFileName);
        buf.append(", TRUNCATEONLY)\n");
        buf.append(sqlSep);
        buf.append("\n");

        _session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(buf.toString(), true);
        _session.getSessionInternalFrame().getSQLPanelAPI().executeCurrentSQL();
        _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
	}
}
