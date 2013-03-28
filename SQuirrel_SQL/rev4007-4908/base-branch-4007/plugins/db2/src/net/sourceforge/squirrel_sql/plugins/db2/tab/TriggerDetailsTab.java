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
        "SELECT  T1.DEFINER     AS trigger_definer, " +
        "       T1.trigname  AS trigger_name, " +
        "       case T1.TRIGTIME " +
        "         when 'A' then 'AFTER' " +
        "         when 'B' then 'BEFORE' " +
        "         when 'I' then 'INSTEAD OF' " +
        "       end AS trigger_time, " +
        "       case T1.TRIGEVENT " +
        "         when 'I' then 'INSERT' " +
        "         when 'U' then 'UPDATE' " +
        "         when 'D' then 'DELETE' " +
        "         when 'S' then 'SELECT' " +
        "         else T1.TRIGEVENT " +
        "       end AS triggering_event, " +
        "       T2.DEFINER     AS table_definer, " +
        "       T2.TABNAME   AS table_name, " +
        "       case T2.TYPE " +
        "         when 'T' then 'TABLE' " +
        "         when 'V' then 'VIEW' " +
        "         else T2.TYPE " +
        "       end AS table_type, " +
        "       case T1.GRANULARITY " +
        "         when 'R' then 'ROW' " +
        "         when 'S' then 'STATEMENT' " +
        "       else T1.GRANULARITY " +
        "       end AS granularity, " +
        "       case T1.VALID " +
        "         when 'Y' THEN 'VALID' " +
        "         when 'N' THEN 'INVALID' " +
        "         when 'X' THEN 'INOPERATIVE' " +
        "       end AS validity, " +
        "       T1.REMARKS comment " +
        "FROM    SYSCAT.TRIGGERS  AS T1, " +
        "       SYSCAT.TABLES    AS T2 " +
        "WHERE   T2.TABNAME = T1.TABNAME " +
        "and T2.TABSCHEMA = T1.TABSCHEMA " +
        "and T1.TRIGSCHEMA = ? " +
        "and T1.trigname = ? ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerDetailsTab.class);

	public TriggerDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

    
    @Override	
	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Trigger details SQL: "+SQL);
            s_log.debug("Trigger schema: "+doi.getSchemaName());
            s_log.debug("Trigger name: "+doi.getSimpleName());
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
