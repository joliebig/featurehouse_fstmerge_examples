package net.sourceforge.squirrel_sql.plugins.oracle.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class IndexDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(IndexDetailsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("oracle.details");
		
		String HINT = s_stringMgr.getString("oracle.displayDetails");
	}

	
	private static final String SQL =
            "select index_type, tablespace_name, table_owner, table_name, table_type, uniqueness"
            +" compression, initial_extent, next_extent, min_extents, max_extents,"
            +" pct_increase, pct_threshold, status, num_rows, last_analyzed"
            +" from sys.all_indexes where owner = ?"
            +" and index_name = ?";

	public IndexDetailsTab()
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
