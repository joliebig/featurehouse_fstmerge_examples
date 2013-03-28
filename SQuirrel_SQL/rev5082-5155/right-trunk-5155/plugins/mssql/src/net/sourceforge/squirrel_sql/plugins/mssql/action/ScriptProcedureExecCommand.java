package net.sourceforge.squirrel_sql.plugins.mssql.action;



import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.WrappedSQLException;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.mssql.MssqlPlugin;
import net.sourceforge.squirrel_sql.plugins.mssql.util.MssqlIntrospector;

public class ScriptProcedureExecCommand implements ICommand {
	private ISession _session;
	private final MssqlPlugin _plugin;

	private final IDatabaseObjectInfo[] _dbObjs;

	public ScriptProcedureExecCommand(ISession session, MssqlPlugin plugin, IDatabaseObjectInfo[] dbObjs) {
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
                final StringBuffer buf = new StringBuffer();

                for (int i = 0; i < _dbObjs.length; i++) {
                    final IDatabaseObjectInfo oi = _dbObjs[i];
                    
                    if (!conn.getCatalog().equals(oi.getCatalogName())) 
                        conn.setCatalog(oi.getCatalogName());
                    
                    if (oi.getSimpleName().endsWith(";0")) {
                        
                        buf.append("/* WILL NOT EXECUTE USER-DEFINED FUNCTION ");
                        buf.append(oi.getQualifiedName());
                        buf.append(" */\n\n");
                        continue;
                    }
                    
                    

                    String useThisName = MssqlIntrospector.getFixedVersionedObjectName(oi.getSimpleName());
                    CallableStatement stmt = conn.prepareCall("{ call sp_help (?) }");
                    stmt.setString(1, useThisName);
                    ResultSet rs;
                    
                    StringBuffer procExec = new StringBuffer();
                    procExec.append("DECLARE @rc int\nEXECUTE @rc = [");
                    procExec.append(oi.getCatalogName());
                    procExec.append("].[");
                    procExec.append(oi.getSchemaName());
                    procExec.append("].[");
                    procExec.append(useThisName);
                    procExec.append("] ");

                    if (!stmt.execute())
                        return;

                    
                    rs = stmt.getResultSet();
                    if (!stmt.getMoreResults())
                        return;
                    rs = stmt.getResultSet();
                    
                    while (rs.next()) {
                        String paramName = rs.getString(1);
                        String paramType = rs.getString(2);
                        short paramLength = rs.getShort(3);
                        int paramPrec = rs.getInt(4);
                        int paramScale = rs.getInt(5);
                        
                        procExec.append(paramName);
                        if (!rs.isLast())
                            procExec.append(", ");
                        
                        buf.append("DECLARE ");
                        buf.append(paramName);
                        buf.append(" ");
                        buf.append(MssqlIntrospector.formatDataType(paramType,paramLength,paramPrec,paramScale));
                        buf.append("\n");
                        buf.append("SET ");
                        buf.append(paramName);
                        buf.append(" = NULL\n");
                    }
                    
                    procExec.append("\nSELECT @rc\n\n");
                    
                    buf.append(procExec);
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
