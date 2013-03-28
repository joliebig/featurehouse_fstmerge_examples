package net.sourceforge.squirrel_sql.plugins.mssql.action;



import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;

public class UpdateStatisticsCommand implements ICommand {
	private ISession _session;
	private final MssqlPlugin _plugin;

	public UpdateStatisticsCommand(ISession session, MssqlPlugin plugin) {
		super();
		if (session == null)
			throw new IllegalArgumentException("ISession == null");

		_session = session;
		_plugin = plugin;
	}

	public void execute() {
        final String sqlSep = _session.getQueryTokenizer().getSQLStatementSeparator();
        final IObjectTreeAPI api = _session.getSessionInternalFrame().getObjectTreeAPI();
		final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

		
        final StringBuffer cmd = new StringBuffer(512);
		for (int i = 0; i < dbObjs.length; ++i) {
            cmd.append("UPDATE STATISTICS ");
            cmd.append(dbObjs[i].getCatalogName());
            cmd.append(".");
            cmd.append(dbObjs[i].getSchemaName());
            cmd.append(".");
            cmd.append(dbObjs[i].getSimpleName());
            cmd.append(" WITH FULLSCAN, ALL\n");
            cmd.append(sqlSep);
            cmd.append("\n");
		}

        if (cmd != null && cmd.length() > 0) {
			_session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(cmd.toString(), true);
			_session.getSessionInternalFrame().getSQLPanelAPI().executeCurrentSQL();
			_session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
		}
	}
}
