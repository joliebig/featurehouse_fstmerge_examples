package net.sourceforge.squirrel_sql.plugins.informix.tab;


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
		
		String TITLE = s_stringMgr.getString("TriggerDetailsTab.title");
		
		String HINT = s_stringMgr.getString("TriggerDetailsTab.hint");
	}

	
	private static String SQL =
        "SELECT  T1.owner     AS trigger_owner, " +
        "       T1.trigname  AS trigger_name, " +
        "       case T1.event  " +
        "         when 'I' then 'INSERT' " +
        "         when 'U' then 'UPDATE' " +
        "         when 'D' then 'DELETE' " +
        "         when 'S' then 'SELECT' " +
        "         else T1.event " +
        "       end AS triggering_event, " +
        "       T2.owner     AS table_owner, " +
        "       T2.tabname   AS table_name, " +
        "       case T2.tabtype " +
        "         when 'T' then 'TABLE' " +
        "         when 'V' then 'VIEW' " +
        "         else T2.tabtype " +
        "       end AS table_type, " +
        "       T1.old       AS reference_before, " +
        "       T1.new       AS reference_after " +
        "FROM   informix.systriggers  AS T1, " +
        "       informix.systables    AS T2 " +
        "WHERE   T2.tabid     = T1.tabid " +
        "and T1.trigname = ? ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerDetailsTab.class);

	public TriggerDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Trigger details SQL: "+SQL);
            s_log.debug("Trigger name: "+doi.getSimpleName());
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName());
		return pstmt;
	}
}
