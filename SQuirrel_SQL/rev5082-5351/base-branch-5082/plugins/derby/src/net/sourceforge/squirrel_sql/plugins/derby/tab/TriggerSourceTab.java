package net.sourceforge.squirrel_sql.plugins.derby.tab;


import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BaseSourceTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class TriggerSourceTab extends BaseSourceTab
{
	
	private static String SQL = "select 'CREATE TRIGGER ' || t.TRIGGERNAME||' \n' " + "    ||(select "
	      + "         CASE " + "         WHEN t3.FIRINGTIME='B' THEN 'BEFORE' "
	      + "         WHEN t3.FIRINGTIME='A' THEN 'AFTER' " + "         END "
	      + "       from SYS.SYSTRIGGERS t3 " + "       where t.TRIGGERID = t3.TRIGGERID) " + "    || ' ' "
	      + "    ||(select CASE " + "         WHEN t2.EVENT='U' THEN 'UPDATE' "
	      + "         WHEN t2.EVENT='D' THEN 'DELETE' " + "         WHEN t2.EVENT='I' THEN 'INSERT' "
	      + "         END " + "       from SYS.SYSTRIGGERS t2 " + "       where t.TRIGGERID = t2.TRIGGERID) "
	      + "     ||' ON ' " + "     || ta.TABLENAME || ' \n'" + "     ||(select " + "        CASE "
	      + "          WHEN t4.REFERENCINGOLD = 0 THEN '' " + "          WHEN t4.REFERENCINGOLD = 1 "
	      + "            THEN ' REFERENCING OLD AS ' || t4.OLDREFERENCINGNAME || ' \n'" + "        END "
	      + "        from SYS.SYSTRIGGERS t4 " + "        where t.TRIGGERID = t4.TRIGGERID) "
	      + "     ||(select " + "        CASE " + "          WHEN t5.REFERENCINGNEW = 0 THEN '' "
	      + "          WHEN t5.REFERENCINGNEW = 1 "
	      + "            THEN ' REFERENCING NEW AS ' || t5.NEWREFERENCINGNAME || ' \n'" + "        END "
	      + "        from SYS.SYSTRIGGERS t5 " + "        where t.TRIGGERID = t5.TRIGGERID) "
	      + "     ||' FOR EACH ROW MODE DB2SQL \n' " + "     || t.triggerdefinition "
	      + "from SYS.SYSTRIGGERS t, SYS.SYSTABLES ta, SYS.SYSSCHEMAS s " + "where t.TABLEID = ta.TABLEID "
	      + "and s.SCHEMAID = t.SCHEMAID " + "and t.TRIGGERNAME = ? " + "and s.SCHEMANAME = ? ";

	
	private final static ILogger s_log = LoggerController.createLogger(TriggerSourceTab.class);

	public TriggerSourceTab(String hint) {
		super(hint);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		if (s_log.isDebugEnabled())
		{
			s_log.debug("Running SQL: " + SQL);
			s_log.debug("Trigger Name=" + doi.getSimpleName());
			s_log.debug("Schema Name=" + doi.getSchemaName());
		}
		ISQLConnection conn = session.getSQLConnection();
		PreparedStatement pstmt = conn.prepareStatement(SQL);
		pstmt.setString(1, doi.getSimpleName());
		pstmt.setString(2, doi.getSchemaName());
		return pstmt;
	}
}
