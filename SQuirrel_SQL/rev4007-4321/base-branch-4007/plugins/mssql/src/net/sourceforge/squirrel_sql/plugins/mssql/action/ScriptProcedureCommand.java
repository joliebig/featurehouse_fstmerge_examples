package net.sourceforge.squirrel_sql.plugins.mssql.action;



import java.sql.Connection;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.WrappedSQLException;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mssql.util.MssqlIntrospector;

public class ScriptProcedureCommand implements ICommand {
	private ISession _session;
	private final MssqlPlugin _plugin;

	private final IDatabaseObjectInfo[] _dbObjs;

	public ScriptProcedureCommand(ISession session, MssqlPlugin plugin, IDatabaseObjectInfo[] dbObjs) {
		super();
		if (session == null)
			throw new IllegalArgumentException("ISession == null");
		if (dbObjs == null)
			throw new IllegalArgumentException("IDatabaseObjectInfo array is null");

		_session = session;
		_plugin = plugin;
		_dbObjs = dbObjs;
	}

	public void execute() throws BaseException {
        try {
            if (_dbObjs.length > 0) {
                Connection conn = _session.getSQLConnection().getConnection();
                final String sqlSep = 
                    _session.getQueryTokenizer().getSQLStatementSeparator();
                final StringBuffer buf = new StringBuffer();

                for (int i = 0; i < _dbObjs.length; i++) {
                    final IDatabaseObjectInfo ti = _dbObjs[i];
                    

                    
                    
                    if (!conn.getCatalog().equals(ti.getCatalogName()))
                        conn.setCatalog(ti.getCatalogName());
                    
                    buf.append(MssqlIntrospector.getHelpTextForObject(MssqlIntrospector.getFixedVersionedObjectName(ti.getSimpleName()),conn));
                    buf.append("\n");
                    buf.append(sqlSep);
                    buf.append("\n\n");
                }

                _session.getSessionInternalFrame().getSQLPanelAPI().appendSQLScript(buf.toString());
                _session.selectMainTab(ISession.IMainPanelTabIndexes.SQL_TAB);
            }
        }
        catch (java.sql.SQLException ex) {
            ex.printStackTrace();
			throw new WrappedSQLException(ex);
		}
	}
}
