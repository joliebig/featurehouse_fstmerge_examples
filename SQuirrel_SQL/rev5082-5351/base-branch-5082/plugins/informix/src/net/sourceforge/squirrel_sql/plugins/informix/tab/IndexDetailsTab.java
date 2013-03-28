package net.sourceforge.squirrel_sql.plugins.informix.tab;

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
        "SELECT  T1.owner     AS index_owner, " +
        "       T1.idxname   AS index_name, " +
        "       T2.owner     AS table_owner, " +
        "       T2.tabname   AS table_name, " +
        "       case T1.clustered " +
        "       when 'C' then 'CLUSTERED' " +
        "       else 'NON-CLUSTERED' " +
        "       end AS index_type, " +
        "       case T1.idxtype " +
        "         when 'U' then 'UNIQUE' " +
        "         else 'NON-UNIQUE' " +
        "         end AS uniqueness, " +
        "       T3.dbspace   AS table_space, " +
        "       T4.fextsiz   AS first_extent, " +
        "       T4.nextsiz   AS next_extent, " +
        "      ( " +
        "           SELECT  COUNT(*) " +
        "           FROM    sysmaster:informix.sysptnext " +
        "           WHERE   pe_partnum = T3.partn " +
        "       ) AS num_extents, " +
        "       T4.nptotal   AS pages_total, " +
        "       T4.npused    AS pages_used " +
        "FROM   informix.sysindices   AS T1, " +
        "       informix.systables    AS T2, " +
        "       informix.sysfragments AS T3, " +
        "       sysmaster:informix.sysptnhdr AS T4 " +
        "WHERE   T1.tabid     >  99 " +
        "AND     T2.tabid     = T1.tabid " +
        "AND     T3.tabid     = T1.tabid " +
        "AND     T3.indexname = T1.idxname " +
        "AND     T4.partnum   = T3.partn " +
        "AND     T1.owner = ? " +        
        "AND     T1.idxname = ? " +
        "ORDER   BY 2 ";
        
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
