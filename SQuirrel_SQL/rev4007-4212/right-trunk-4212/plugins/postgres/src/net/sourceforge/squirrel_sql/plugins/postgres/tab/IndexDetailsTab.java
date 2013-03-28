package net.sourceforge.squirrel_sql.plugins.postgres.tab;

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
		
		String TITLE = s_stringMgr.getString("IndexDetailsTab.title");
		
		String HINT = s_stringMgr.getString("IndexDetailsTab.hint");
	}

	
	private static final String SQL =
        "select inds.schemaname as schemaname, " +
        "       inds.relname as tablename, " +
        "       i.indisunique as is_unique, " +
        "       i.indisprimary as is_primary_key, " +
        "       i.indisclustered as is_clustered, " +
        "       inds.idx_scan as num_index_scans, " +
        "       inds.idx_tup_read as num_index_entries_returned, " +
        "       inds.idx_tup_fetch as num_table_rows_fetched " +
        "from pg_catalog.pg_index i, pg_catalog.pg_stat_all_indexes inds " +
        "where i.indexrelid = inds.indexrelid " +
        "and inds.schemaname = ? " +
        "and inds.indexrelname = ? ";
        
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
