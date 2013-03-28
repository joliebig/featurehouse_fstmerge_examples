package net.sourceforge.squirrel_sql.plugins.derby.tab;


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
        "select tr.TRIGGERNAME       AS name, " +
        "       sc.SCHEMANAME        AS schemaname, " +
        "       tr.CREATIONTIMESTAMP AS createtime, " +
        "       CASE " +
        "         WHEN tr.EVENT='U' THEN 'UPDATE' " +
        "         WHEN tr.EVENT='D' THEN 'DELETE' " +
        "         WHEN tr.EVENT='I' THEN 'INSERT' " +
        "       END AS event, " +
        "       CASE " +
        "         WHEN tr.FIRINGTIME='B' THEN 'BEFORE' " +
        "         WHEN tr.FIRINGTIME='A' THEN 'AFTER' " +
        "       END AS firingtime, " +
        "       CASE " +
        "         WHEN tr.TYPE='R' THEN 'ROW' " +
        "         WHEN tr.TYPE='S' THEN 'STATEMENT' " +
        "       END AS type, " +
        "       t.TABLENAME AS TABLENAME " +
        "from SYS.SYSTRIGGERS tr, SYS.SYSSCHEMAS sc, SYS.SYSTABLES t " +
        "where TRIGGERNAME = ? " +
        "and sc.SCHEMANAME = ? " +
        "and tr.SCHEMAID = sc.SCHEMAID " +
        "and tr.TABLEID = t.TABLEID ";
    
	
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
            s_log.debug("Trigger schema: "+doi.getSchemaName());
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName());
        pstmt.setString(2, doi.getSchemaName());
		return pstmt;
	}
}
