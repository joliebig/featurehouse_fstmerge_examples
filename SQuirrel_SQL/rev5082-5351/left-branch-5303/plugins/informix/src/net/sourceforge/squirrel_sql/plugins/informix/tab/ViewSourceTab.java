package net.sourceforge.squirrel_sql.plugins.informix.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class ViewSourceTab extends InformixSourceTab
{
	
	private static String SQL =
        "SELECT viewtext " +
        "FROM informix.systables AS T1, informix.sysviews AS T2 " +
        "WHERE tabname = ? " +
        "AND T2.tabid = T1.tabid ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(ViewSourceTab.class);

	public ViewSourceTab(String hint)
	{
		super(hint);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		ISQLConnection conn = session.getSQLConnection();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running View Source SQL: "+SQL);
            s_log.debug("View Name="+doi.getSimpleName());
            s_log.debug("Schema Name="+doi.getSchemaName());
        }                
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
