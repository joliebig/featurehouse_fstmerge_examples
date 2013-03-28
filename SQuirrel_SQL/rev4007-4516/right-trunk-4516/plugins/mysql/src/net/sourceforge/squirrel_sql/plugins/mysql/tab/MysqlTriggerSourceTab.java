package net.sourceforge.squirrel_sql.plugins.mysql.tab;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.tabs.FormattedSourceTab;

public class MysqlTriggerSourceTab extends FormattedSourceTab
{
	
	private static String SQL =
        "SELECT ACTION_STATEMENT " +
        "FROM INFORMATION_SCHEMA.TRIGGERS " +
        "WHERE TRIGGER_SCHEMA = ? " +
        "AND TRIGGER_NAME = ? ";
    
	
	private final static ILogger s_log =
		LoggerController.createLogger(MysqlTriggerSourceTab.class);

	public MysqlTriggerSourceTab(String hint, String stmtSep)
	{
		super(hint);
        super.setupFormatter(stmtSep, null);
        super.setCompressWhitespace(true);
	}

	protected PreparedStatement createStatement() throws SQLException
	{
		final ISession session = getSession();
		final IDatabaseObjectInfo doi = getDatabaseObjectInfo();

		ISQLConnection conn = session.getSQLConnection();
        if (s_log.isDebugEnabled()) {
            s_log.debug("Running SQL for View source tab: "+SQL);
            s_log.debug("Binding catalog name "+doi.getCatalogName()+
                        " as first bind value");
            s_log.debug("Binding table name "+doi.getSimpleName()+
                        " as second bind value");                        
        }
		PreparedStatement pstmt = conn.prepareStatement(SQL);
        
        pstmt.setString(1, doi.getCatalogName());
		pstmt.setString(2, doi.getSimpleName());
		return pstmt;
	}
}
