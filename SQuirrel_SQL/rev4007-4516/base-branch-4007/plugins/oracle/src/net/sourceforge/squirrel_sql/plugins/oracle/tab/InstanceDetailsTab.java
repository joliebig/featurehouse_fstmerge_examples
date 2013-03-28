package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;

public class InstanceDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(InstanceDetailsTab.class);

	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("oracle.instanceDetails");
		
		String HINT = s_stringMgr.getString("oracle.displaySnstanceDetails");
	}

	
	private static String SQL =
		"select instance_number, instance_name, host_name, version,"
			+ " startup_time, status, parallel, thread#, archiver, log_switch_wait,"
			+ " logins, shutdown_pending, database_status, instance_role"
			+ " from sys.v_$instance"
			+ " where instance_number = ?";

    
    private static String CHECK_ACCESS_SQL =
        "select instance_number, instance_name, host_name, version,"
            + " startup_time, status, parallel, thread#, archiver, log_switch_wait,"
            + " logins, shutdown_pending, database_status, instance_role"
            + " from sys.v_$instance";

    
	public InstanceDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	
	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setLong(1, Long.parseLong(doi.getSimpleName()));
		return pstmt;
	}
	
	
	public static boolean isAccessible(final ISession session) {
		return OraclePlugin.checkObjectAccessible(session, CHECK_ACCESS_SQL);
	}
}
