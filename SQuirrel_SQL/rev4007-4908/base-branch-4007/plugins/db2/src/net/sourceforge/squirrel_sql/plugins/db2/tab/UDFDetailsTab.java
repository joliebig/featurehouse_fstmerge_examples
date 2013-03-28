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

public class UDFDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TriggerDetailsTab.class);

	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("UdfDetailsTab.title");
		
		String HINT = s_stringMgr.getString("UdfDetailsTab.hint");
	}

	
	private static String SQL =
	    "select " +
	    "name, " +
	    "schema, " +
	    "definer, " +
	    "function_id, " +
	    "parm_count, " +
	    "side_effects, " +
	    "fenced, " +
	    "language, " +
	    "contains_sql, " +
	    "result_cols, " +
	    "class, " +
	    "jar_id " +
	    "from sysibm.SYSFUNCTIONS " +
	    "where schema = ? " +
	    "and name = ? ";
	
	
	private static String OS_400_SQL = 
	    "select " +
	    "routine_name as name, " +
	    "routine_schema as schema, " +
	    "routine_definer as definer, " +
	    "in_parms as parm_count, " +
	    "case external_action " +
	    "    when 'E' then 'has external side effects' " +
	    "    when 'N' then 'has no external side effects' " +
	    "end as side_effects, " +
	    "fenced, " +
	    "external_language as language, " +
	    "sql_data_access as contains_sql, " +
	    "number_of_results as result_cols, " +
	    "external_name " +
	    "from qsys2.SYSFUNCS " +
	    "where routine_schema = ? " +
	    "and routine_name = ? ";
	
	
	private final static ILogger s_log =
		LoggerController.createLogger(TriggerDetailsTab.class);

	
	private boolean isOS400 = false;
	
	
	public UDFDetailsTab(boolean isOS400)
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
            s_log.debug("UDF details SQL: "+sql);
            s_log.debug("UDF schema: "+doi.getSchemaName());
            s_log.debug("UDF name: "+doi.getSimpleName());
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(sql);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
