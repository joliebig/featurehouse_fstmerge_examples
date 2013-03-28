package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class TriggerSourceTab extends BaseSourceTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TriggerSourceTab.class);

	
	private static String SQL =
		"select trigger_body from sys.all_triggers"
			+ " where owner = ? and trigger_name = ?";

	public TriggerSourceTab()
	{
		
		super(s_stringMgr.getString("oracle.showTriggerSource"));
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
