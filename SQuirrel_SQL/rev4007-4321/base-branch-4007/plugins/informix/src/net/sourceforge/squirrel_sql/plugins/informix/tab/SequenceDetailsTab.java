package net.sourceforge.squirrel_sql.plugins.informix.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SequenceDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SequenceDetailsTab.class);

	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("SequenceDetailsTab.title");
		
		String HINT = s_stringMgr.getString("SequenceDetailsTab.hint");
	}

	
	private static final String SQL =
        "SELECT  T2.owner     AS sequence_owner, " +
        "       T2.tabname   AS sequence_name, " +
        "       T1.min_val   AS min_value, " +
        "       T1.max_val   AS max_value, " +
        "       T1.inc_val   AS increment_by, " +
        "       case T1.cycle " +
        "         when '0' then 'NOCYCLE' " +
        "         else 'CYCLE' " +
        "       end AS cycle_flag, " +
        "       case T1.order " +
        "         when '0' then 'NOORDER' " +
        "         else 'ORDER' " +
        "        end AS order_flag, " +
        "       T1.cache     AS cache_size " +
        "FROM    informix.syssequences AS T1, " +
        "        informix.systables    AS T2 " +
        "WHERE   T2.tabid     = T1.tabid " +
        "and T2.owner = ? " +        
        "and T2.tabname = ? ";
    
	public SequenceDetailsTab()
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
