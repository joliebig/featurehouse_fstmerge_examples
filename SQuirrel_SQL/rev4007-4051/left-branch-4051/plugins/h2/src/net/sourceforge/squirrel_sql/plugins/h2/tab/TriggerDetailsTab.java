package net.sourceforge.squirrel_sql.plugins.h2.tab;


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
        "SELECT TRIGGER_CATALOG,TRIGGER_SCHEMA,TRIGGER_NAME, " +
        "TRIGGER_TYPE,TABLE_CATALOG,TABLE_SCHEMA,TABLE_NAME, " +
        "BEFORE,JAVA_CLASS,QUEUE_SIZE,NO_WAIT,REMARKS,SQL " +
        "FROM INFORMATION_SCHEMA.TRIGGERS " +
        "WHERE TABLE_SCHEMA = ? " +
        "AND TRIGGER_NAME = ? ";
    
	
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
            s_log.debug("Trigger schema: "+doi.getSchemaName());
            s_log.debug("Trigger name: "+doi.getSimpleName());
        }
		PreparedStatement pstmt = session.getSQLConnection().prepareStatement(SQL);
        pstmt.setString(1, doi.getSchemaName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
