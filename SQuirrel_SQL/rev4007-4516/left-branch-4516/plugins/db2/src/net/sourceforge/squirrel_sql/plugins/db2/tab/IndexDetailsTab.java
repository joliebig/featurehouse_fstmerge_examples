package net.sourceforge.squirrel_sql.plugins.db2.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class IndexDetailsTab extends BasePreparedStatementTab
{
    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(IndexDetailsTab.class);

	
	private boolean isOS400 = false;
	
	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("IndexDetailsTab.title");
		
		String HINT = s_stringMgr.getString("IndexDetailsTab.hint");
	}

	
	private static final String SQL =
        "SELECT T1.IID as index_identifier, " +
        "       T1.DEFINER AS index_owner, " +
        "       T1.INDNAME AS index_name, " +
        "       T2.DEFINER AS table_owner, " +
        "       T2.TABNAME AS table_name, " +
        "       T3.TBSPACE AS table_space, " +
        "       case T1.INDEXTYPE " +
        "         when 'BLOK' then 'Block Index' " +
        "         when 'CLUS' then 'Clustering Index' " +
        "         when 'DIM' then 'Dimension Block Index' " +
        "         when 'REG' then 'Regular Index' " +
        "         when 'XPTH' then 'XML Path Index' " +
        "         when 'XRGN' then 'XML Region Index' " +
        "         when 'XVIL' then 'Index over XML column (Logical)' " +
        "         when 'XVIP' then 'Index over XML column (Physical)' " +
        "       end AS index_type, " +
        "       case T1.UNIQUERULE " +
        "         when 'U' then 'UNIQUE' " +
        "         when 'D' then 'NON-UNIQUE' " +
        "         when 'I' then 'UNIQUE (Implements PK)' " +
        "       end AS uniqueness, " +
        "       T1.NLEAF AS number_of_leaf_pages, " +
        "       T1.NLEVELS AS number_of_levels, " +
        "       T1.CREATE_TIME, " +
        "       T1.STATS_TIME AS last_statistics_update, " +
        "       case T1.REVERSE_SCANS " +
        "         when 'Y' then 'Supported' " +
        "         when 'N' then 'Not Supported' " +
        "       end AS reverse_scans " +
        "FROM    SYSCAT.INDEXES   AS T1, " +
        "        SYSCAT.TABLES    AS T2, " +
        "        SYSCAT.TABLESPACES as T3 " +
        "WHERE  T3.TBSPACEID = T1.TBSPACEID " +
        "and T2.TABNAME = T1.TABNAME " +
        "and T2.TABSCHEMA = T1.TABSCHEMA " +
        "AND     T1.TABSCHEMA = ? " +
        "AND     T1.INDNAME = ? ";
    
	
	private static String OS_400_SQL = 
	    "select index_owner, " +
	    "index_name, " +
	    "index_schema, " +
	    "table_owner, " +
	    "table_name, " +
	    "table_schema, " +
	    "case is_unique " +
	    "    when 'D' then 'No (duplicates are allowed)' " +
	    "    when 'V' then 'Yes (duplicate NULL values are allowed)' " +
	    "    when 'U' then 'Yes' " +
	    "    when 'E' then 'Encoded vector index' " +
	    "end as uniqueness, " +
	    "column_count, " +
	    "system_index_name, " +
	    "system_index_schema, " +
	    "system_table_name, " +
	    "system_table_schema, " +
	    "long_comment, " +
	    "iasp_number, " +
	    "index_text, " +
	    "is_spanning_index " +
	    "from qsys2.sysindexes " +
	    "where table_schema = ? " +
	    "and index_name = ? ";	    
	
    
    private final static ILogger s_log =
        LoggerController.createLogger(IndexDetailsTab.class);
	
    
    public IndexDetailsTab(boolean isOS400)
	{
		super(i18n.TITLE, i18n.HINT, true);
		this.isOS400 = isOS400;
	}

    
    @Override	
	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        String sql = SQL;
        if (isOS400) {
            sql = OS_400_SQL;
        }
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL for index details: "+sql);
            s_log.debug("schema="+doi.getSchemaName());
            s_log.debug("index name="+doi.getSimpleName());
        }
		PreparedStatement pstmt = 
		    session.getSQLConnection().prepareStatement(sql);		
		pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
