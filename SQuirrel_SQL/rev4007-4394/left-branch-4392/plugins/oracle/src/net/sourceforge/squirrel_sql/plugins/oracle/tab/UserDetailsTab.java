package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;

public class UserDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(UserDetailsTab.class);

	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("oracle.userDetails");
		
		String HINT = s_stringMgr.getString("oracle.displayUserDetails");
	}
    
    private static final String SQL_CHECK_ACCESS = 
        "select username, user_id,"
            + " account_status, lock_date, expiry_date, default_tablespace,"
            + " temporary_tablespace, created, initial_rsrc_consumer_group,"
            + " external_name from dba_users";        
    
	
	private static final String SQL_ADMIN =
		"select username, user_id,"
			+ " account_status, lock_date, expiry_date, default_tablespace,"
			+ " temporary_tablespace, created, initial_rsrc_consumer_group,"
			+ " external_name from dba_users"
			+ " where username = ?";
    
	
	private static final String SQL_USER =
		"select username, user_id,"
			+ " account_status, lock_date, expiry_date, default_tablespace,"
			+ " temporary_tablespace, created, initial_rsrc_consumer_group,"
			+ " external_name from user_users"
			+ " where username = ?";

	
	protected boolean isAdmin;
	
	public UserDetailsTab(final ISession session)
	{
		super(i18n.TITLE, i18n.HINT, true);
        session.getApplication().getThreadPool().addTask(new Runnable() {
            public void run() {
                isAdmin=OraclePlugin.checkObjectAccessible(session, SQL_CHECK_ACCESS);
            }
        });
		
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();

		final PreparedStatement pstmt = session.getSQLConnection().prepareStatement(isAdmin?SQL_ADMIN:SQL_USER);
		
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
