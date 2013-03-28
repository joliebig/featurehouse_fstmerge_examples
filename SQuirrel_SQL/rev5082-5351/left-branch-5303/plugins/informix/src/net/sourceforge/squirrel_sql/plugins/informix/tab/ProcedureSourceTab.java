package net.sourceforge.squirrel_sql.plugins.informix.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class ProcedureSourceTab extends InformixSourceTab
{
	
	private static String SQL =
        "SELECT T1.procid, T2.data, T2.seqno " +
        "FROM informix.sysprocedures AS T1, informix.sysprocbody AS T2 " +
        "WHERE procname = ? " +
        "AND T2.procid = T1.procid " +
        "AND datakey = 'T' " +
        "ORDER BY T1.procid, T2.seqno ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(ProcedureSourceTab.class);

	public ProcedureSourceTab(String hint)
	{
		super(hint);
        sourceType = STORED_PROC_TYPE;
	}

	
	@Override
	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL: "+SQL);
            s_log.debug("procname="+doi.getSimpleName());
        }

		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
