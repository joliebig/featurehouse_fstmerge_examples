package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SessionStatisticsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionStatisticsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("oracle.sessionStatistics");
		
		String HINT = s_stringMgr.getString("oracle.displaySessionStatistics");
	}

	
	private static String SQL =
		"select sn.name, ss.value"
			+ " from sys.v_$sesstat ss, sys.v_$statname sn"
			+ " where ss.sid = ?"
			+ " and ss.statistic# = sn.statistic#";

	public SessionStatisticsTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        String[] parts = doi.getSimpleName().split("\\s+");
        pstmt.setLong(1, Long.parseLong(parts[0]));
		return pstmt;
	}
}
