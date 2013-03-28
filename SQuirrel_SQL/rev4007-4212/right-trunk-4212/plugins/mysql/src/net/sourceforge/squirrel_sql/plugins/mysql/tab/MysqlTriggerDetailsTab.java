package net.sourceforge.squirrel_sql.plugins.mysql.tab;


import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.BasePreparedStatementTab;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class MysqlTriggerDetailsTab extends BasePreparedStatementTab
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(MysqlTriggerDetailsTab.class);


	
	private interface i18n
	{
		
		String TITLE = s_stringMgr.getString("MysqlTriggerDetailsTab.title");
		
		String HINT = s_stringMgr.getString("MysqlTriggerDetailsTab.hint");
	}

	
	private static String SQL =
        "SELECT " +
        "trigger_name, " +
        "trigger_schema, " +
        "definer, " +
        "event_manipulation, " +
        "action_timing, " +
        "action_orientation, " +
        "event_object_table " +
        "FROM information_schema.TRIGGERS " +
        "WHERE trigger_schema = ? " +
        "and trigger_name = ? ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(MysqlTriggerDetailsTab.class);

	public MysqlTriggerDetailsTab()
	{
		super(i18n.TITLE, i18n.HINT, true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		ISession session = getSession();
        IDatabaseObjectInfo doi = getDatabaseObjectInfo();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Trigger details SQL: "+SQL);
            s_log.debug("Trigger catalog: "+doi.getCatalogName());
            s_log.debug("Trigger name: "+doi.getSimpleName());
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
		pstmt.setString(1, doi.getCatalogName());
        pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
