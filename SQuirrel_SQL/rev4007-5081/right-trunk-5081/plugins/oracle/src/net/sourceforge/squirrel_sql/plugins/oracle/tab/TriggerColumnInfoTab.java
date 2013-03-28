package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class TriggerColumnInfoTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TriggerColumnInfoTab.class);

	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("oracle.triggerColumns");
		
		String HINT = s_stringMgr.getString("oracle.displayTriggerColumns");
	}

	
	private static String SQL =
		"select table_owner, table_name, column_name, column_list, column_usage"
			+ " from sys.all_trigger_cols where trigger_owner = ?"
			+ " and trigger_name = ?";

	public TriggerColumnInfoTab()
	{
		super(i18n.TITLE, i18n.HINT);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
