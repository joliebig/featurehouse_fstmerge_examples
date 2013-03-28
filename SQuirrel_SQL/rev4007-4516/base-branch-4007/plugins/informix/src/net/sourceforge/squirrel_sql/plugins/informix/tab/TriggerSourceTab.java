package net.sourceforge.squirrel_sql.plugins.informix.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class TriggerSourceTab extends InformixSourceTab
{
	
	private static String SQL =
	    "SELECT  T2.data, T2.datakey, T2.seqno " +
	    "FROM    informix.systriggers AS T1, informix.systrigbody AS T2 " +
	    "WHERE   trigname = ? " +
	    "AND     T2.trigid = T1.trigid " +
	    "AND     datakey IN ('D', 'A') " +
	    "ORDER   BY datakey DESC, seqno ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerSourceTab.class);

	public TriggerSourceTab(String hint)
	{
		super(hint);
        sourceType = TRIGGER_TYPE;
	}

	
	@Override
	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL: "+SQL);
            s_log.debug("trigname="+doi.getSimpleName());
        }
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
