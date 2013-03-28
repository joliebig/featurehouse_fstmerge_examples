package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;

public class SessionDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SessionDetailsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("oracle.sessionDetails");
		
		String HINT = s_stringMgr.getString("oracle.displaySessionDetails");
	}

	
	private static String SQL =
		"select sid, user#, username, status, server, schemaname, osuser,"
			+ " machine, terminal, program"
			+ " from sys.v_$session"
			+ " where sid = ?";

    
    private static String CHECK_ACCESS_SQL =
        "select sid, user#, username, status, server, schemaname, osuser,"
            + " machine, terminal, program"
            + " from sys.v_$session";
    
	public SessionDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
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
	
	
	public static boolean isAccessible(final ISession session) {
		return OraclePlugin.checkObjectAccessible(session, CHECK_ACCESS_SQL);
	}
}
