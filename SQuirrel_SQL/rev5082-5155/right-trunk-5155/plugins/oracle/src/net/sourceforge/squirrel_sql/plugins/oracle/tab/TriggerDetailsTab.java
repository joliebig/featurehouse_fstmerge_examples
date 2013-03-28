package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class TriggerDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TriggerDetailsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("oracle.triggerDetails");
		
		String HINT = s_stringMgr.getString("oracle.displayTriggerDetails");
	}

	
	private static String SQL =
		"select t.owner, t.trigger_name, t.trigger_type, t.triggering_event, t.table_owner, " +
		" t.base_object_type, t.table_name, t.column_name, " +
		" t.referencing_names, t.when_clause, t.status, t.description, t.action_type, o.status as validity " +
		" from sys.all_triggers t, user_objects o " +
		" where t.trigger_name = o.OBJECT_NAME " +
		" and t.owner = ? " +
		" and t.trigger_name = ? ";


	public TriggerDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
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
