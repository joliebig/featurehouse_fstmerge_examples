package org.firebirdsql.squirrel.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class TriggerDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TriggerDetailsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("firebird.trigDetails");
		
		String HINT = s_stringMgr.getString("firebird.triggerDetails");
	}

	
	private static String SQL =
		"select rdb$trigger_name, " +
        "rdb$trigger_sequence, " +
        "case rdb$trigger_type " +
        "  when 1 then 'BEFORE INSERT' " +
        "  when 2 then 'AFTER INSERT' " +
        "  when 3 then 'BEFORE UPDATE' " +
        "  when 4 then 'AFTER UPDATE' " +
        "  when 5 then 'BEFORE DELETE' " +
        "  when 6 then 'AFTER DELETE' " +
        "  else 'UNKNOWN TYPE' || rdb$trigger_type " +
        "end as rdb$trigger_type, " +
        "case rdb$trigger_inactive " +
        "  when 0 then 'ACTIVE' " +
        "  when 1 then 'INACTIVE' " +
        "  else 'UNKNOWN' " +
        "end as rdb$trigger_active, " +
		"rdb$description " +
		"from rdb$triggers where " +
		"  rdb$trigger_name = ?";

	
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerDetailsTab.class);

	public TriggerDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Preparing SQL: "+SQL);
        }        
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        if (s_log.isDebugEnabled()) {
            s_log.debug("setString param: "+doi.getSimpleName());
        }        
        pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
